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

public interface UltrasonicController {
	
	public void drive(int error);
	
	public int readUSDistance();
	
	public int getError();
}
