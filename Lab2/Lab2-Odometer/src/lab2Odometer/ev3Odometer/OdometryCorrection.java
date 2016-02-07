/* 
 * OdometryCorrection.java
 */

//
// Group 37:
// 
// Omar Akkila 260463681
// Frank Ye 260689448

package lab2Odometer.ev3Odometer;
import lejos.hardware.Sound;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.port.Port;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
		
	private Port sensorPort = LocalEV3.get().getPort("S1");

	private static EV3ColorSensor colorSensor;
	private static SampleProvider colorSample;
			
	private float[] colorData;

	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
		this.colorSensor = new EV3ColorSensor(sensorPort);
		this.colorSample = colorSensor.getRedMode();
		this.colorData = new float[colorSensor.sampleSize()];
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;

		while (true) {
			correctionStart = System.currentTimeMillis();
			
			// put your correction code here			
			this.colorSample.fetchSample(colorData, 0);
			double pi = Math.PI;
			double piOverTwo = pi / 2.0;
			double threePiOverTwo = (3.0 * pi) / 2.0;
			
			double currentTheta = odometer.getTheta();	//theta value retreived from odometer
			double relativeTheta = currentTheta; //relative theta is the theta angle in radians realtive to the axis pointing towards the front of the cart
			double thetaThreshold = 0.4; //Predicted error in theta readings
			double lowTheta = currentTheta - thetaThreshold;
			double highTheta = currentTheta + thetaThreshold;
			
			// Adjusts relativeTheta relative to forward axis after rotating
			if (piOverTwo <= highTheta && piOverTwo >= lowTheta){
				relativeTheta = Math.abs(currentTheta - piOverTwo);
			}
			if (pi <= highTheta && pi >= lowTheta) {
				relativeTheta = Math.abs(currentTheta - pi);
			}
			if (threePiOverTwo <= highTheta && threePiOverTwo >= lowTheta) {
				relativeTheta = Math.abs(currentTheta - threePiOverTwo);
			}
			
			
			if (this.colorData[0] <= 0.3 && relativeTheta <= 0.4) {
				// According to values measured by odometer and preset x, y thresholds, correct position accordingly.
				//
				// This code works for a 3x3 square but in the future we would modify this code to work with multiples of 15
				// and take into account the orientation, so that it can work with larger, variant paths.
				if (yAtFifteen() && ((odometer.getX() < 5 && odometer.getX() > -5) || ( odometer.getX() < 65 && odometer.getX() > 55))){
					Sound.beep();
					odometer.setY(15);
				} else if (yAtFortyFive()) {
					Sound.beep();
					odometer.setY(45);
				} else if (xAtFifteen()) {
					Sound.beep();
					odometer.setX(15);
				} else if (xAtFortyFive()) {
					Sound.beep();
					odometer.setX(45);
				}
			}		

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
	
	public boolean yAtFifteen() {
		return odometer.getY() >= 0 && odometer.getY() <= 30;
	}
	
	public boolean yAtFortyFive() {
		return odometer.getY() >= 30 && odometer.getY() <= 50;
	}
	
	public boolean xAtFifteen() {
		return odometer.getX() >= 0 && odometer.getX() <= 30;
	}
	
	public boolean xAtFortyFive() {
		return odometer.getX() >= 30 && odometer.getX() <= 50;
	}
}