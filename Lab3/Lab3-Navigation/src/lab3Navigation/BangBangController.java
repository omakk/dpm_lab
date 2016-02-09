package lab3Navigation;

import lejos.hardware.motor.*;
import java.lang.Math;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwidth;
	private final int motorDefault, motorLow, motorHigh;
	private int distance, pingCounter;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private Navigator nav;
	
	public BangBangController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
							  int bandCenter, int bandwidth, int motorLow, int motorHigh, Navigator nav) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.nav = nav;
		this.motorDefault = 225;
		this.pingCounter = 0;
		leftMotor.setSpeed(motorHigh);				// Start robot moving forward
		rightMotor.setSpeed(motorHigh);
//		leftMotor.forward();
//		rightMotor.forward();
	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		int error = this.bandCenter - this.distance;
		if (Math.abs(error) <= bandwidth) {
//			this.leftMotor.setSpeed(this.motorDefault);
//			this.rightMotor.setSpeed(this.motorDefault);
//			this.leftMotor.forward();
//			this.rightMotor.forward();
		} else if (error > 0) { //Too close
			try{
				this.nav.sleep(3000);
				nav.setNavigatingState(false);
			} catch (InterruptedException e) {
				e.notify();
			}
			this.leftMotor.setSpeed(this.motorHigh + 70);
			this.rightMotor.setSpeed(this.motorLow - 70);
			this.leftMotor.forward();
			this.rightMotor.forward();
		} else if (Math.abs(error) > 100) { //Large negative error (Could be gap or corner)
			//Counts the number of pings where high negative were recorded
			// If pingCoutner reached a certain value then it's probably a corner
			if (this.pingCounter++ >= 16) {
//				this.leftMotor.setSpeed(this.motorLow - 70);
//				this.rightMotor.setSpeed(this.motorHigh + 70);
//				this.leftMotor.forward();
//				this.rightMotor.forward();
//				if(!this.nav.isNavigating()) {
//					nav.notify();
//				}
			}
		} else if (error < 0) { //Too far but not too too far
//			this.pingCounter = 0;
//			this.leftMotor.setSpeed(this.motorLow - 45);
//			this.rightMotor.setSpeed(this.motorHigh + 50);
//			this.leftMotor.forward();
//			this.rightMotor.forward();
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

