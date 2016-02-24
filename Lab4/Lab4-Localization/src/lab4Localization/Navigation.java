/*
 * File: Navigation.java
 * Written by: Sean Lawlor
 * ECSE 211 - Design Principles and Methods, Head TA
 * Fall 2011
 * Ported to EV3 by: Francois Ouellet Delorme
 * Fall 2015
 * 
 * Movement control class (turnTo, travelTo, flt, localize)
 */

/*
 * Group 37:
 * 
 * Omar Akkila	260463681
 * Frank Ye		260689448
 * 
 * This lab was demoed late by Luke (at around 6pm); hence, the late code submission
 */

package lab4Localization;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Navigation {
	final static int FAST = 200, SLOW = 100, ACCELERATION = 4000;
	final static double DEG_ERR = 3.0, CM_ERR = 0.7;
	private Odometer odometer;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	private double xR, yR, dist, thetaD;

	public Navigation(Odometer odo) {
		this.odometer = odo;

		EV3LargeRegulatedMotor[] motors = this.odometer.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];

		// set acceleration
//		this.leftMotor.setAcceleration(ACCELERATION);
//		this.rightMotor.setAcceleration(ACCELERATION);
	}

	/*
	 * Functions to set the motor speeds jointly
	 */
	public void setSpeeds(float lSpd, float rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	public void setSpeeds(int lSpd, int rSpd) {
		this.leftMotor.setSpeed(lSpd);
		this.rightMotor.setSpeed(rSpd);
		if (lSpd < 0)
			this.leftMotor.backward();
		else
			this.leftMotor.forward();
		if (rSpd < 0)
			this.rightMotor.backward();
		else
			this.rightMotor.forward();
	}

	/*
	 * Float the two motors jointly
	 */
	public void setFloat() {
		this.leftMotor.stop();
		this.rightMotor.stop();
		this.leftMotor.flt(true);
		this.rightMotor.flt(true);
	}

	/*
	 * TravelTo function which takes as arguments the x and y position in cm Will travel to designated position, while
	 * constantly updating it's heading
	 */
	public void travelTo(double x, double y) {
//		double minAng;
//		while (Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y - odometer.getY()) > CM_ERR) {
//			minAng = Math.toDegrees((Math.atan2(y - odometer.getY(), x - odometer.getX()))); // Changed from * 180 / Math.PI to Math.toDegrees()
//			if (minAng < 0)
//				minAng += 360.0;
//			this.turnTo(minAng, false);
//			this.setSpeeds(FAST, FAST);
//		}
//		this.setSpeeds(0, 0);
		
		this.xR = this.odometer.getX();
		this.yR = this.odometer.getY();
		this.dist = euclidianDist(xR, yR, x, y);
		this.thetaD = Math.toDegrees(Math.atan2(x-xR, y-yR));
		turnTo(this.thetaD, false);
		
		leftMotor.setSpeed(SLOW);
		rightMotor.setSpeed(SLOW);
		
		this.leftMotor.rotate(convertDistance(2.15,dist), true);
		this.rightMotor.rotate(convertDistance(2.15, dist), false);
		
//		while(Math.abs(x - odometer.getX()) > CM_ERR || Math.abs(y - (odometer.getY())) > CM_ERR) {				
//			this.leftMotor.setSpeed(15);
//			this.rightMotor.setSpeed(15);
//			
//			this.leftMotor.forward();
//			this.rightMotor.forward();
//		}
	}

	/*
	 * TurnTo function which takes an angle and boolean as arguments The boolean controls whether or not to stop the
	 * motors when the turn is completed
	 */
	public void turnTo(double angle, boolean stop) {

//		double error = angle - this.odometer.getAng();
//
//		while (Math.abs(error) > DEG_ERR) {
//
//			error = angle - this.odometer.getAng();
//
//			if (error < -180.0) {
//				this.setSpeeds(-SLOW, SLOW);
//			} else if (error < 0.0) {
//				this.setSpeeds(SLOW, -SLOW);
//			} else if (error > 180.0) {
//				this.setSpeeds(SLOW, -SLOW);
//			} else {
//				this.setSpeeds(-SLOW, SLOW);
//			}
//		}

		double error = angle - this.odometer.getAng();
		this.leftMotor.setSpeed(SLOW);
		this.rightMotor.setSpeed(SLOW);
//		this.setSpeeds(SLOW, SLOW);
		
		double angularInput;
		
		if (error< -180) angularInput = 360 + error;
		else if (error >180) angularInput = 360 - error;
		else angularInput = error;
		
		leftMotor.rotate(convertAngle (2.15, 16.4, angularInput), true);
		rightMotor.rotate(-convertAngle (2.15, 16.4 , angularInput), false);
		
		if (stop) {
			leftMotor.setSpeed(0);
			rightMotor.setSpeed(0);
		}
	}
	
	public int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	public int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	private double euclidianDist (double x1, double y1, double x2, double y2 ) {
		return Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
	}
	
	/*
	 * Go foward a set distance in cm
	 */
	public void goForward(double distance) {
		this.travelTo(Math.cos(Math.toRadians(this.odometer.getAng())) * distance, Math.cos(Math.toRadians(this.odometer.getAng())) * distance);

	}
}