/*
 * Odometer.java
 */

package lab2Odometer.ev3Odometer;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.motor.Motor;

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;

	private EV3LargeRegulatedMotor leftMotor;
	private EV3LargeRegulatedMotor rightMotor;
	
	
	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;

	// lock object for mutual exclusion
	private Object lock;
	
	// Tachometer counts of the previous and current sample
	private double lastTachoL, lastTachoR, nowTachoL, nowTachoR;
	
	private final double WB = 16.7; //Wheel Base in cm 
	private final double WR = 2.15; //Wheel Radius in cm

	// default constructor
	public Odometer(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor) {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		lock = new Object();
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;

		while (true) {
			double distL, distR;
			updateStart = System.currentTimeMillis();
			// put (some of) your odometer code here

			// get tacho counts
			this.nowTachoL = this.leftMotor.getTachoCount();
			this.nowTachoR = this.rightMotor.getTachoCount();
			
			distL = Math.PI * this.WR * (this.nowTachoL - this.lastTachoL) / 180;
			distR = Math.PI * this.WR * (this.nowTachoR - this.lastTachoR) / 180;
			
			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
				
				double deltaD, deltaT, dX, dY;
				
				this.lastTachoL = this.nowTachoL;
				this.lastTachoR = this.nowTachoR;
				
				deltaD = 0.5 * (distL + distR);
				deltaT = (distL - distR) / this.WB;
				this.theta += deltaT;
				
				// Bounded theta
				if (this.theta > (Math.PI * 2)) this.theta %= (Math.PI * 2);
				if (this.theta < 0) this.theta += (Math.PI * 2);
				
				dX = deltaD * Math.sin(this.theta);
				dY = deltaD * Math.cos(this.theta);
				
				this.x += dX;
				this.y += dY;
			}

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}