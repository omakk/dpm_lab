package lab3Navigation;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class Lab3 {

	private static final int bandCenter = 35;
	private static final int bandWidth = 2;
	private static final int motorLow = 150;
	private static final int motorHigh = 250;
	
	private static final Port usPort = LocalEV3.get().getPort("S2");
	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	
	private static final TextLCD t = LocalEV3.get().getTextLCD();
	
	public static void main (String[] args) {
	
		//Setup odometer, navigator, and controller objects
		Odometer o = new Odometer(leftMotor, rightMotor);
		OdometryDisplay od = new OdometryDisplay(o, t);
		Navigator nav = new Navigator(o);
		// You can use either BangBang or P.
		// Use whichever works better
		BangBangController bangbang = new BangBangController(leftMotor, rightMotor, bandCenter, bandWidth, motorLow, motorHigh, nav);
		PController p = new PController(leftMotor, rightMotor, bandCenter, bandWidth);

		int option = 0;
		printMainMenu();
		while (option == 0)								// Wait for a button press.  The button
			option = Button.waitForAnyPress();			// ID (option) determines what type of path to use.
		
		// Setup ultrasonic sensor
		// Note that the EV3 version of leJOS handles sensors a bit differently.
		// There are 4 steps involved:
		// 1. Create a port object attached to a physical port (done already above)
		// 2. Create a sensor instance and attach to port
		// 3. Create a sample provider instance for the above and initialize operating mode
		// 4. Create a buffer for the sensor data
		
		@SuppressWarnings("resource")							    // Because we don't bother to close this resource
		SensorModes usSensor = new EV3UltrasonicSensor(usPort);		// usSensor is the instance
		SampleProvider usDistance = usSensor.getMode("Distance");	// usDistance provides samples from this instance
		float[] usData = new float[usDistance.sampleSize()];		// usData is the buffer in which data are returned
		
		// Setup Ultrasonic Poller															// This thread samples the US and invokes
		UltrasonicPoller usPoller = new UltrasonicPoller(usDistance, usData, bangbang);		// the selected controller on each cycle
		
		// Depending on which button was pressed, perform the path used in the nav-only demo or perform the path
		// used in the nav + avoidance path which is the default path when running the thread.
		// Also, start your odometer and odometer display threads
		switch(option) {
		case Button.ID_LEFT:										// Nav-only demo path selected
			o.start();
			od.start();
			nav.start();
			nav.setNavigatingState(true);
//			(new Navigator(o) {
//				public void run() {
//					this.travelTo(60, 30);
//					this.travelTo(30.0, 30.0);
//					this.travelTo(30, 60);
//					this.travelTo(60, 0);
//					this.travelTo(0.0, 60.0);
//					this.travelTo(60.0, 0.0);
//				}
//			}).start();
			break;
		case Button.ID_RIGHT:										// Default thread path (nav + avoidance) selected
			o.start();
			od.start();
			usPoller.start();
			try {
				Thread.sleep(2500);
			} catch (InterruptedException e) {
				// This code block intentionally left blank
			}
			nav.start();
			nav.setNavigatingState(true);
//			(new Navigator(o) {
//				public void run() {
//					this.travelTo(0.0, 60.0);
//					this.travelTo(60.0, 0.0);
//				}
//			}).start();
			break;
		default:
			System.out.println("Error - invalid button");			// None of the above - abort
			System.exit(-1);
			break;
		}
				
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
	
	public static void printMainMenu() {									// a static method for drawing
		t.clear();															// the screen at initialization
		t.drawString("< Nav | N+Avoid >",  0, 0);
	}
}
