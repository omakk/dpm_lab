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
import lejos.robotics.SampleProvider;

//
//  Control of the wall follower is applied periodically by the 
//  UltrasonicPoller thread.  The while loop at the bottom executes
//  in a loop.  Assuming that the us.fetchSample, and cont.processUSData
//  methods operate in about 20mS, and that the thread sleeps for
//  50 mS at the end of each loop, then one cycle through the loop
//  is approximately 70 mS.  This corresponds to a sampling rate
//  of 1/70mS or about 14 Hz.
//


public class UltrasonicPoller extends Thread{
	private SampleProvider us;
	private UltrasonicController cont;
	private float[] usData;
	
	private boolean useBB = false;
	
	public double bangbangX, bangbangY, refX, refY;
	
	private static int bandCenter, bandwidth;
	private int distance;
	
	private boolean obstacle = false;
	
	public UltrasonicPoller(SampleProvider us, float[] usData, UltrasonicController cont, int bandCenter) {
		this.us = us;
		this.cont = cont;
		this.usData = usData;
		this.bandCenter = bandCenter;
	}

//  Sensors now return floats using a uniform protocol.
//  Need to convert US result to an integer [0,255]
	
	public void run() {
		int distance;
		while (true) {
			us.fetchSample(usData,0);							// acquire data
			distance=(int)(usData[0]*100.0);					// extract from buffer, cast to int
			if (useBB) {
				cont.drive(this.bandCenter - distance);
				if (( (int) Math.sqrt(Math.pow((bangbangX - refX), 2) + Math.pow((bangbangY - refY), 2)) > 80)) {
					continue;
				}
			} else  {
				processUSData(distance);
				useBB = false;
				obstacleExists(false);
			}
			try { Thread.sleep(50); } catch(Exception e){}		// Poor man's timed sampling
		}
	}
	
	
	public void processUSData(int distance) {
		this.distance = distance;
		int error = this.bandCenter - this.distance;
		if (error > 0) {
			obstacleExists(true);
			useBB = true;
		} else {
			obstacleExists(false);
		}
	}
	
	public boolean obstacleExists() {
		return this.obstacle;
	}
	
	public void obstacleExists( boolean b) {
		this.obstacle = b;
	}

}
