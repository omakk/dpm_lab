/*
 * Group 37:
 * 
 * Omar Akkila	260463681
 * Frank Ye		260689448
 * 
 * This lab was demoed late by Luke (at around 6pm); hence, the late code submission
 */

package lab4Localization;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static int ROTATION_SPEED = 70;
	private final int FILTER_OUT = 30;

	private Odometer odo;
	private SampleProvider usSensor;
	private float[] usData;
	private float distance;
	private LocalizationType locType;
	private int filterControl = 0;
	
	public USLocalizer(Odometer odo,  SampleProvider usSensor, float[] usData, LocalizationType locType) {
		this.odo = odo;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB;
		double averageAng, deltaAng = 0.0;
		final double CLIP = 45.0;
		double k = 1.0;
		
		EV3LargeRegulatedMotor leftMotor = this.odo.getLeftMotor();
		EV3LargeRegulatedMotor rightMotor = this.odo.getRightMotor();
		
		if (locType == LocalizationType.FALLING_EDGE) {
			// rotate the robot until it sees no wall			
			while (this.getFilteredData() <= CLIP + k) {
				leftMotor.setSpeed(ROTATION_SPEED);
				rightMotor.setSpeed(ROTATION_SPEED);
				leftMotor.backward();
				rightMotor.forward();
			}
			
			leftMotor.stop(true);
			rightMotor.stop(false);
			
			// keep rotating until the robot sees a wall, then latch the angle
			while (this.getFilteredData() > CLIP - k) {
				leftMotor.setSpeed(ROTATION_SPEED);
				rightMotor.setSpeed(ROTATION_SPEED);
				leftMotor.backward();
				rightMotor.forward();
			}
			
			Sound.beep();
			double angleB1 = this.odo.getAng();
			leftMotor.stop(true);
			rightMotor.stop(false);
			
			// switch direction and wait until it sees no wall
			while (this.getFilteredData() <= CLIP + k) {
				leftMotor.setSpeed(ROTATION_SPEED);
				rightMotor.setSpeed(ROTATION_SPEED);
				leftMotor.forward();
				rightMotor.backward();
			}
			
			Sound.beep();
			double angleB2 = this.odo.getAng();
			angleB = (angleB1 + angleB2) / 2.0;
			leftMotor.stop(true);
			rightMotor.stop(false);
			
			// keep rotating until the robot sees a wall, then latch the angle
			while (this.getFilteredData() > CLIP - k) {
				leftMotor.setSpeed(ROTATION_SPEED);
				rightMotor.setSpeed(ROTATION_SPEED);
				leftMotor.forward();
				rightMotor.backward();
			}
			
			Sound.beep();
			angleA = this.odo.getAng();
			leftMotor.stop(true);
			rightMotor.stop(false);
			
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			averageAng = (angleA - angleB) / 2.0;
			
			if (angleA < angleB) deltaAng = 45 - averageAng;
			else if (angleA > angleB) deltaAng = 225 - averageAng;
			
			// update the odometer position (example to follow:)
			pos = this.odo.getPosition();
			pos[2] = deltaAng;
						
			odo.setPosition(pos, new boolean [] {true, true, true});
			
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			
			// rotate the robot until it sees a wall
			while (this.getFilteredData() > CLIP - k) {
				leftMotor.setSpeed(ROTATION_SPEED);
				rightMotor.setSpeed(ROTATION_SPEED);
				leftMotor.backward();
				rightMotor.forward();
			}
			
			Sound.beep();
			leftMotor.stop(true);
			rightMotor.stop(false);
			
			// keep rotating until the robot does not see a wall, then latch the angle
			while (this.getFilteredData() <= CLIP + k) {
				leftMotor.setSpeed(ROTATION_SPEED);
				rightMotor.setSpeed(ROTATION_SPEED);
				leftMotor.backward();
				rightMotor.forward();
			}
			
			Sound.beep();
			double angleA1 = this.odo.getAng();
			leftMotor.stop(true);
			rightMotor.stop(false);
			
			// switch direction and wait until it sees a wall
			while (this.getFilteredData() > CLIP - k) {
				leftMotor.setSpeed(ROTATION_SPEED);
				rightMotor.setSpeed(ROTATION_SPEED);
				leftMotor.forward();
				rightMotor.backward();
			}
			
			Sound.beep();
			double angleA2 = this.odo.getAng();
			angleA = (angleA1 + angleA2) / 2.0;
			leftMotor.stop(true);
			rightMotor.stop(false);
			
			// keep rotating until the robot does not see a wall, then latch the angle
			while (this.getFilteredData() <= CLIP + k) {
				leftMotor.setSpeed(ROTATION_SPEED);
				rightMotor.setSpeed(ROTATION_SPEED);
				leftMotor.forward();
				rightMotor.backward();
			}
			
			Sound.beep();
			angleB = this.odo.getAng();
			leftMotor.stop(true);
			rightMotor.stop(false);
			
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			averageAng = (angleB - angleA) / 2.0;

			if (angleA < angleB) deltaAng = 45 - averageAng;
			else if (angleA > angleB) deltaAng = 225 - averageAng;
						
			// update the odometer position (example to follow:)
			pos = this.odo.getPosition();
			pos[2] = deltaAng;
			
			odo.setPosition(pos, new boolean [] {true, true, true});
			
		}
	}
	
	private float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = usData[0] * 100;
		
		if (distance >= 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the filter value
			filterControl ++;
		} else if (distance == 255){
			// true 255, therefore set distance to 255
			this.distance = distance;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
		}
		
				
		return distance;
	}

}
