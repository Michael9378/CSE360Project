package cse360.model;

import java.util.logging.Handler;
public class TripModel {

	protected int currentAcceleration = 0;
	protected int currentVelocity = 0;
	protected int maxVelocity = 0;
	protected int averageVelocity = 0;
	protected int tripDuration = 0;
	protected double tripMiles = 0;
	private final int MAX_SPEED = 999;
	private final int MIN_SPEED = -99;

	// gets current speed in miles per hour
	public int getVelocity() {
		return this.currentVelocity;
	}
	// gets max speed in miles per hour
	public int getMaxVelocity() {
		return this.maxVelocity;
	}
	// gets average speed in miles per hour
	public int getAvgVelocity() {
		return this.averageVelocity;
	}
	// DEPRECATED takes in an int as miles per hour and sets the current velocity
	public boolean setVelocity(int mph){
		if(mph >= 0) {
			this.currentVelocity = mph;
			return true;
		}
		// speed is negative.
		return false;
	}
	public int getTripduration(){
		return tripDuration;
	}
	public double getTripdistance(){
		return tripMiles;
	}
	// gets current acceleration in miles per hour per second
	public int getAcceleration() {
		return this.currentAcceleration;
	}
	// takes in an int in miles per hour per second and sets the current acceleration as such if under 50
	public boolean setAcceleration(int mphps) {
		if(mphps <= 50) {
			this.currentAcceleration = mphps;
			return true;
		}
		// acceleration is faster than reasonable, return an error
		return false;
	}
	// reads current acceleration and updates 3 velocity values, trip duration, and distance; to be called once a second.
	public boolean updateStats(){
		// add acceleration figure to current velocity
		if((currentVelocity + currentAcceleration) > MAX_SPEED){
			setAcceleration(MAX_SPEED - currentVelocity);
		}

		if((currentVelocity + currentAcceleration) < MIN_SPEED){
			setAcceleration(MIN_SPEED - currentVelocity);
		}
		currentVelocity += currentAcceleration;
		if(CarModel.updateTotalMiles(Math.abs((float)(currentVelocity)/3600))){
			// with updated currentVelocity, update other velocities and trip distance
			tripMiles += Math.abs((float)(currentVelocity)/3600);
			tripDuration ++;
			// add previous tripDuration weight (duration-1) to current average, then add current velocity, then divide by tripduration weight.
			averageVelocity = (int) ((averageVelocity*(tripDuration - 1)+currentVelocity)/tripDuration);
			// find and set max
			maxVelocity = Math.max(maxVelocity, currentVelocity);
			return true;

		}
		// no fuel!
		return false;
	}
	// Use this function for properly setting speed as it uses accel
	public void setSpeed(int setVelocity) {

		Thread t = new Thread()
		{
		    public void run() {
		    	while (!Thread.currentThread().isInterrupted()) {
		            // continue processing
		    		// if the velocities match, break out of recursive function
		    		if(Math.abs(setVelocity - currentVelocity) < 1  ) {
		    			currentAcceleration = 0;
		    			Thread.currentThread().interrupt();
		    			break;
		    		}
		    		if(setVelocity > currentVelocity) {
		    			if((setVelocity - currentVelocity) >= 5) {
		    				currentAcceleration = 5;
		    			}
		    			else {
		    				currentAcceleration = 1;
		    			}
		    		}
		    		if(setVelocity < currentVelocity) {
		    			if((currentVelocity - setVelocity) >= 5) {
		    				currentAcceleration = -5;
		    			}
		    			else {
		    				currentAcceleration = -1;
		    			}
		    		}

		            try {
		                Thread.sleep(1000);
		            } catch (InterruptedException e) {
		                // good practice
		                Thread.currentThread().interrupt();
		                return;
		            }
		        }
		    }
		};
		t.start();

	}
}
