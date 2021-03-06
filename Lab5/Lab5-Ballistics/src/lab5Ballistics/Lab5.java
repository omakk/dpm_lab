/*
 * Group 37:
 * 
 * Omar Akkila	260463681
 * Frank Ye		260689448
 */

package lab5Ballistics;

import lejos.hardware.Button;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class Lab5 {

	private static final EV3LargeRegulatedMotor leftMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));
	private static final EV3LargeRegulatedMotor rightMotor = new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
	
	public static void main (String[] args) {
		leftMotor.setAcceleration(30000);
		rightMotor.setAcceleration(30000);
				
		LocalEV3.get().getTextLCD().drawString("READY TO FIRE-UU!!", 0, 0);
		
		/*
		 * Press enter button twice to launch catapult OR press the escape button once to exit the program.
		 * The catapult is launched by rotating both motors 70 degrees counter clockwise and allowed to fall back down after
		 * the motors have been floated.
		 */
		while (Button.waitForAnyPress() == Button.ID_ENTER && Button.waitForAnyPress() != Button.ID_ESCAPE) {
			Sound.beep();
			
			leftMotor.setSpeed(700);
			rightMotor.setSpeed(700);
			
			leftMotor.rotate(-70, true);
			rightMotor.rotate(-70, false);
			
			leftMotor.flt(true);
			rightMotor.flt(false);
		}
		
		System.exit(0);
	}
}
