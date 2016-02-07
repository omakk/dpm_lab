/*
 * Group 37:
 * Omar Akkila    260463681
 * Frannk Ye      260689448
 */

package lab3Navigation;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;


public class Navigator extends Thread {
	private Odometer o;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private final int FWDSPEED = 250;
	private final int ROTATE_SPEED = 100; 
	private static final double WR = 2.13; //Wheel Radius
	
	public Navigator(Odometer o) {
		//odometer object will be passed into constructor so that we can access the methods in the odometer class from this class
		this.o = o;
		this.leftMotor = this.o.getLeftMotor();
		this.rightMotor = this.o.getRightMotor();
	}
	
	public void run() {
		this.travelTo(60.0, 30.0);
//		this.travelTo(30.0, 30.0);
//		this.travelTo(30.0, 60.0);
//		this.travelTo(60.0, 0.0);
	}
	
	public void travelTo (double xD, double yD){
		double xR = o.getX();
		double yR = o.getY();
		double dist = euclidianDist(xR, yR, xD, yD);
		double thetaD = Math.toDegrees(Math.atan2(yD - yR, xD - xR));
		turnTo(thetaD);
		this.leftMotor.setSpeed(FWDSPEED);
		this.rightMotor.setSpeed(FWDSPEED);
		this.leftMotor.rotate(convertDistance(WR, dist), true);
		this.rightMotor.rotate(convertDistance(WR, dist), false);
	}
	
	public void turnTo (double thetaD) {
		Sound.beep();
		double thetaR = Math.toDegrees(o.getTheta());
		double error = thetaD - thetaR;
		this.leftMotor.setSpeed(ROTATE_SPEED);
		this.rightMotor.setSpeed(ROTATE_SPEED);
		if (error < -180) {
			this.leftMotor.rotateTo(360 + (int) error, true);
			this.rightMotor.rotateTo(-360 - (int) error, false);
		} else if (error > 180) {
			this.leftMotor.rotateTo(360 - (int) error, true);
			this.rightMotor.rotateTo(-360 + (int) error, false);
		} else {
			this.leftMotor.rotateTo((int) error, true);
			this.rightMotor.rotateTo(- (int) error, false);
		}
	}
	
	public boolean isNavigating() {
		return true;
	}
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
	
	private double euclidianDist (double x1, double y1, double x2, double y2 ) {
		return Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
	}
}
