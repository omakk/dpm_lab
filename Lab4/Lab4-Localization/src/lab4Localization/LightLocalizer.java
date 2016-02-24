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

public class LightLocalizer {
	private Odometer odo;
	private Navigation nav;
	private SampleProvider colorSensor;
	private float[] colorData;
	private static double LS_TO_CENTER_DIST = 13.15; //Distance in cm from the light sensor to teh conter of rotation
	
	public LightLocalizer(Odometer odo, SampleProvider colorSensor, float[] colorData, Navigation nav) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.nav = nav;
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
		
		EV3LargeRegulatedMotor[] motors = this.odo.getMotors();
		EV3LargeRegulatedMotor leftMotor = motors[0];
		EV3LargeRegulatedMotor rightMotor = motors[1];	
		final int SPEED = 125;
		double angleNegY = 0.0, anglePosY = 0.0, angleNegX = 0.0, anglePosX = 0.0; // Our angles used for trigonmetry calculations
		long startTime, endTime, elapsedTime;	
		
		// While you have not detected a black line, move forward in positive y direction
		// We know we are facing the psotive-y direction because we would have performed UltraSonic localization
		startTime = System.currentTimeMillis();
		while (getFilteredData() > 0.25) {
			leftMotor.setSpeed(SPEED);
			rightMotor.setSpeed(SPEED);
			leftMotor.forward();
			rightMotor.forward();
		}		
		endTime = System.currentTimeMillis();
		elapsedTime = endTime - startTime;
		
		Sound.beep();
		leftMotor.stop(true);
		rightMotor.stop(false);
		
		// Move the cart backwards by 0.80 the distance it intially took until it dscovered a black line
		startTime = System.currentTimeMillis();
		endTime = startTime;
		while ( endTime - startTime <= elapsedTime * 0.80) {
			leftMotor.setSpeed(SPEED);
			rightMotor.setSpeed(SPEED);
			leftMotor.backward();
			rightMotor.backward();
			endTime = System.currentTimeMillis();
		}
		
		Sound.beep();
		leftMotor.stop(true);
		rightMotor.stop(false);
		
		// Turn 90 degrees clockwise and move forward until you detect the black line while moving in the positive x direction
		// If we happen to be travelling forward for too long (ie: ((currentTime - startTime) / 1000) seconds) then move backward (negative-x direction)
		// until you detect the line.
		this.nav.turnTo(90.0, true);
		
		startTime = System.currentTimeMillis();
		while (getFilteredData() > 0.25) {
			leftMotor.setSpeed(SPEED);
			rightMotor.setSpeed(SPEED);
			leftMotor.forward();
			rightMotor.forward();
		}
		endTime = System.currentTimeMillis();
		elapsedTime = endTime - startTime;
		
		Sound.beep();
		leftMotor.stop(true);
		rightMotor.stop(false);
		
		// Move the cart backwards by 0.80 the distance it intially took until it dscovered a black line
		startTime = System.currentTimeMillis();
		endTime = startTime;
		while ( endTime - startTime <= elapsedTime * 0.80) {
			leftMotor.setSpeed(SPEED);
			rightMotor.setSpeed(SPEED);
			leftMotor.backward();
			rightMotor.backward();
			endTime = System.currentTimeMillis();
		}
	
		Sound.beep();
		leftMotor.stop(true);
		rightMotor.stop(false);

		// Now, we are within a good range to perform an in-place rotation of the cart to detect the necessary 4 black lines for localization.
		// NOTE: After we have stopped, we should be oriented toward the positive-x direction (somewhere around 90 degrees clockwise from positive-y)
		
		// We start by rotating counter-clockwise with our first grid-line being the negative-y axis
		int lineCounter = 0;
		while (lineCounter < 4) {
			
			leftMotor.setSpeed(SPEED);
			rightMotor.setSpeed(SPEED);
			leftMotor.backward();
			rightMotor.forward();
			
			if (getFilteredData() <= 0.25) {
				Sound.beep();
				++lineCounter;
			}
			
			switch(lineCounter) {
				case (0):
					break;
				case (1):
					//Negative-y axis
					angleNegY = this.odo.getAng();
					break;
				case (2):
					//Positive-x axis
					anglePosX = this.odo.getAng();
					break;
				case (3):
					//Positive-y axis
					anglePosY = this.odo.getAng();
					break;
				case (4):
					//Negative-x axis
					angleNegX = this.odo.getAng();
					break;
				default:
					break;
			}
		}
		
		leftMotor.stop(true);
		rightMotor.stop(false);
		
		// Derived through trigonometry
		double angleY = angleNegY - anglePosY;
		double angleX = anglePosX - angleNegX;
		//double deltaAng = 180 - angleNegX - (angleX / 2.0); // Calculation derived using trigonometry
		this.odo.setPosition(new double[] {(-LS_TO_CENTER_DIST * Math.cos(Math.toRadians((angleY)/2.0))),
										   (-LS_TO_CENTER_DIST * Math.cos(Math.toRadians((angleX)/2.0))),
										    this.odo.getAng()},
							 new boolean[] {true, true, true});
	}

	public float getFilteredData() {
		this.colorSensor.fetchSample(this.colorData, 0);
		return this.colorData[0];	
	}
}
