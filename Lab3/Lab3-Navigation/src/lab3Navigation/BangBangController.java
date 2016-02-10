/*
 * Group 37:
 * 
 * Omar Akkila 260463681
 * Frank Ye 260689448
 * 
 * Part 2 (Naviagation + Avoidance) does not work and we were demoed late by a TA named Luke. 
 * Hence, why the code submission is late.
 */

package lab3Navigation;

import lejos.hardware.motor.*;
import java.lang.Math;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth;
	private final int motorDefault, motorLow, motorHigh;
	private int distance, pingCounter;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	
	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
							  int bandCenter, int bandwidth, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.motorDefault = 225;
		this.pingCounter = 0;
	}
	
	public void drive (int error) {
		if (Math.abs(error) <= bandwidth) {
			this.pingCounter = 0;
			this.leftMotor.setSpeed(this.motorDefault);
			this.rightMotor.setSpeed(this.motorDefault);
			this.leftMotor.forward();
			this.rightMotor.forward();
		} else if (error > 0) { //Too close
			this.pingCounter = 0;
			this.leftMotor.setSpeed(this.motorLow - 70);
			this.rightMotor.setSpeed(this.motorHigh + 70);
			this.leftMotor.forward();
			this.rightMotor.forward();
		} else if (error < 0) { //Too far
			if (this.pingCounter++ >= 16) {
				this.leftMotor.setSpeed(this.motorHigh + 70);
				this.rightMotor.setSpeed(this.motorLow - 70);
				this.leftMotor.forward();
				this.rightMotor.forward();
			}
		}
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}
	
	public int getError() {
		return this.bandCenter - this.distance;
	}
}

