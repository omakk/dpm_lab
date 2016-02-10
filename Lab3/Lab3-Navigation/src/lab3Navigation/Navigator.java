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

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;


public class Navigator extends Thread {
	private Odometer o;
	private boolean navigating, avoided;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private final UltrasonicPoller usP;
	private final int FWDSPEED = 250;
	private final int ROTATE_SPEED = 100; 
	private static final double WR = 2.15; //Wheel Radius
//	private static final double width = 16.7;
	
	double xR, yR, dist, thetaD;
	
	
	
	public Navigator(Odometer o, UltrasonicPoller usP) {
		// Odometer and Ultrasonic Poller object will be passed into constructor 
		// so that we can access the methods in the odometer class from this class
		this.o = o;
		this.leftMotor = this.o.getLeftMotor();
		this.rightMotor = this.o.getRightMotor();
		this.usP = usP;
		this.avoided = false;
	}
	
	public void run() {
//		this.travelTo(60.0, 30.0);
//		this.travelTo(30.0, 30.0);
//		this.travelTo(30.0, 60.0);
//		this.travelTo(60.0, 0.0);
		this.travelTo(0.0, 60.0);
		this.travelTo(60.0, 0.0);
	}
	
	public void travelTo (double xD, double yD){
		
		this.xR = o.getX();
		this.yR = o.getY();
		this.dist = euclidianDist(xR, yR, xD, yD);
		this.thetaD = Math.toDegrees(Math.atan2(xD-xR, yD-yR));
		turnTo(this.thetaD);
		
		while(true){
			
			if (this.usP.obstacleExists()){
				usP.refX = o.getX();
				usP.refY = o.getY();
				while(this.usP.obstacleExists()) {
					usP.bangbangX = o.getX();
					usP.bangbangY = o.getY();
					try{
						Thread.sleep(25);
					} catch (InterruptedException e){
						
					}
					this.avoided = true;
				}
			}
			
			double currentTheta = Math.toDegrees(o.getTheta());
			if( avoided && ((currentTheta > this.thetaD + 30) || (currentTheta < this.thetaD - 3))) {
				turnTo(Math.toDegrees(Math.atan2(xD-o.getX(), yD-o.getY())));
				avoided = false;
			}
			
			if(this.dist < 0.9) {
				this.leftMotor.stop(true);
				this.rightMotor.stop(false);
				break;
			}
		
			this.leftMotor.setSpeed(FWDSPEED);
			this.rightMotor.setSpeed(FWDSPEED);
			
			this.leftMotor.forward();
			this.rightMotor.forward();
			
			this.dist = euclidianDist(o.getX(), o.getY(), xD, yD);
			
	//		this.leftMotor.rotate(convertDistance(WR,dist), true);
	//		this.rightMotor.rotate(convertDistance(WR, dist), false);
		}
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
		
		leftMotor.rotate(convertAngle (WR, 16.73, angularInput), true);
		rightMotor.rotate(-convertAngle (WR, 16.73, angularInput), false);
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
