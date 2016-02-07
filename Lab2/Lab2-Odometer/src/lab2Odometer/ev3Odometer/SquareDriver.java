/*
 * SquareDriver.java
 */

//
// Group 37:
// 
// Omar Akkila 260463681
// Frank Ye 260689448

package lab2Odometer.ev3Odometer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;

import java.io.File;

import lejos.hardware.Sound;

public class SquareDriver {
	private static final int FORWARD_SPEED = 250;
	private static final int ROTATE_SPEED = 150;

	public static void drive(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
			double leftRadius, double rightRadius, double width) {
		// reset the motors
		for (EV3LargeRegulatedMotor motor : new EV3LargeRegulatedMotor[] { leftMotor, rightMotor }) {
			motor.stop();
			motor.setAcceleration(3000);
		}

		// wait 5 seconds
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// there is nothing to be done here because it is not expected that
			// the odometer will be interrupted by another thread
		}

		for (int i = 0; i < 4; i++) {
			// drive forward two tiles
			leftMotor.setSpeed(FORWARD_SPEED);
			rightMotor.setSpeed(FORWARD_SPEED);

			leftMotor.rotate(convertDistance(leftRadius, 60.96), true);
			rightMotor.rotate(convertDistance(rightRadius, 60.96), false);

			// turn 90 degrees clockwise
			leftMotor.setSpeed(ROTATE_SPEED);
			rightMotor.setSpeed(ROTATE_SPEED);

			leftMotor.rotate(convertAngle(leftRadius, width, 89.35), true);
			rightMotor.rotate(-convertAngle(rightRadius, width, 89.35), false);
		}
		//The champ is here
		Sound.playSample(new File("AND_HIS_NAME_IS_JOHN_CENA.wav"), 100); //A little victory tune to play after turning the square. File was uploaded to cart
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
}