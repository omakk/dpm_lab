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
	private boolean navigating;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private final int FWDSPEED = 250;
	private final int ROTATE_SPEED = 100; 
	private static final double WR = 2.15; //Wheel Radius
	//private static final double width = 16.3;
	
	public Navigator(Odometer o) {
		//Odometer object will be passed into constructor so that we can access the methods in the odometer class from this class
		this.o = o;
		this.leftMotor = this.o.getLeftMotor();
		this.rightMotor = this.o.getRightMotor();
	}
	
	public void run() {
//		this.travelTo(60, 30);
//		this.travelTo(30.0, 30.0);
//		this.travelTo(30, 60);
		this.travelTo(0.0, 60.0);
		this.travelTo(60.0, 0.0);
	}
	
	public void travelTo (double xD, double yD){
		double xR = o.getX();
		double yR = o.getY();
		double dist = euclidianDist(xR, yR, xD, yD);
		double thetaD = Math.toDegrees(Math.atan2(xD-xR, yD-yR));
//		{if (yD-yR>0) {
//			
//			thetaD = Math.toDegrees(Math.atan2(yD-yR, xD-xR));
//		}
//		 if ((yD-yR)< 0){
//
//				if ((xD-xR)> 0){
//					thetaD = Math.toDegrees(Math.atan2(yD-yR, xD-xR)+ Math.PI);
//			}
//				else {
//					thetaD = Math.toDegrees(Math.atan2(yD-yR, xD-xR)- Math.PI);
//				}
//		}
		turnTo(thetaD);
		
	
		this.leftMotor.setSpeed(FWDSPEED);
		this.rightMotor.setSpeed(FWDSPEED);
		
		this.leftMotor.rotate(convertDistance(WR,dist), true);
		this.rightMotor.rotate(convertDistance(WR, dist), false);
		}
	
	
	public void turnTo (double thetaD) {
		
		double thetaR = Math.toDegrees(o.getTheta());
		int error = (int) (thetaD - thetaR);
		this.leftMotor.setSpeed(ROTATE_SPEED);
		this.rightMotor.setSpeed(ROTATE_SPEED);
		
		int angularInput;
		
		if (error< -180) angularInput = 360 + error;
		else if (error >180) angularInput = 360 - error;
		else angularInput = error;
		
		leftMotor.rotate(convertAngle (WR, 16.8, angularInput), true);
		rightMotor.rotate(-convertAngle (WR, 16.8, angularInput), false);
	}
	
	public boolean isNavigating() {
		return this.navigating;
	}
	
	public void setNavigatingState(boolean b) {
		this.navigating = b;
	}
	
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
	
	private double euclidianDist (double x1, double y1, double x2, double y2 ) {
		return Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
	}
}
