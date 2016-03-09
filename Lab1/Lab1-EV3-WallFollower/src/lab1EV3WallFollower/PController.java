package lab1EV3WallFollower;
import lejos.hardware.motor.EV3LargeRegulatedMotor;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwidth;
	private final int motorDefault, motorStraight = 200, FILTER_OUT = 20;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;
	private int distance, filterControl, pingCounter;
	private final int MAXCORRECTION; 
	
	public PController(EV3LargeRegulatedMotor leftMotor, EV3LargeRegulatedMotor rightMotor,
					   int bandCenter, int bandwidth) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwidth = bandwidth;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		this.motorDefault = 225;
		this.pingCounter = 0;
		this.MAXCORRECTION = 110;
		leftMotor.setSpeed(motorStraight);					// Initialize motor rolling forward
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		filterControl = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		
		// rudimentary filter - toss out invalid samples corresponding to null signal.
		// (n.b. this was not included in the Bang-bang controller, but easily could have).
		//
		if (distance == 255 && filterControl < FILTER_OUT) {
			// bad value, do not set the distance var, however do increment the filter value
			filterControl ++;
		} else if (distance == 255){
			// true 255, therefore set distance to 255
			this.distance = distance;
		} else {
			// distance went below 255, therefore reset everything.
			filterControl = 0;
			this.distance = distance;
		}
		
		int error = this.bandCenter - this.distance;
		int deltaSpeed = calcProp(error);
		int motorLow = this.motorDefault - deltaSpeed;
		int motorHigh = this.motorDefault + deltaSpeed;
		if (Math.abs(error) <= bandwidth) {
			this.leftMotor.setSpeed(this.motorDefault);
			this.rightMotor.setSpeed(this.motorDefault);
			this.leftMotor.forward();
			this.rightMotor.forward();
		} else if (error > 0) { // Too close
			this.leftMotor.setSpeed(motorHigh);
			this.rightMotor.setSpeed(motorLow);
			this.leftMotor.forward();
			this.rightMotor.forward();
		} else if (Math.abs(error) > 100) { //Large negative error (Could be gap or corner)
			//Counts the number of pings where high negative were recorded
			// If pingCoutner reached a certain value then it's probably a corner
			if (this.pingCounter++ >= 17) {
				this.leftMotor.setSpeed(motorLow);
				this.rightMotor.setSpeed(motorHigh);
				this.leftMotor.forward();
				this.rightMotor.forward();
			}
		} else if (error < 0) { //Too far but not too too far
			this.pingCounter = 0;
			this.leftMotor.setSpeed(motorLow);
			this.rightMotor.setSpeed(motorHigh);
			this.leftMotor.forward();
			this.rightMotor.forward();
		}
	
	}
	
	public int calcProp( int error) {
		final double KP  = 9.23; //Chosen after many a trial and error
		int correction;
		
		error = Math.abs(error);
		correction = (int) (KP * (double)error);
		if (correction >= this.motorDefault) correction = this.MAXCORRECTION;
		return correction;
	}

	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

	public int getError() {
		return this.bandCenter - this.distance;
	}
}
