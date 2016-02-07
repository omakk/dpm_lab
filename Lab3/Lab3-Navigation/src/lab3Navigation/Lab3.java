package lab3Navigation;

import lejos.hardware.Button;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Lab3 {

	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	
	public static void main (String[] args) {
	
		final TextLCD t = LocalEV3.get().getTextLCD();
		Odometer o = new Odometer(leftMotor, rightMotor);
		OdometryDisplay od = new OdometryDisplay(o, t);
		Navigator nav = new Navigator(o);
		
		o.start();
		od.start();
		nav.start();
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
}
