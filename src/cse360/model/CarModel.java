package cse360.model;

public class CarModel {
	//protected Map map;
	public static double fuel = 1; // fuel is a percentage of gas left in tank, start with full tank
	public static double totalMiles = 0;
	public final static int TANK_VOLUME = 14; // Total capacity of gas tank in gallons
	public final static int MPG = 25;

	public static boolean hasFuel(){
		// if we have fuel, return true, else false
		if(CarModel.fuel > 0)
			return true;
		return false;
	}
	// takes in an amount of gallons and adds it to the current tank's capacity.
	public static boolean addFuel(int gallons){
		// convert amount of fuel being added to a float to add to current fuel.
		float moreFuel = gallons/TANK_VOLUME;
		if((moreFuel + CarModel.fuel) <= 1) {
			CarModel.fuel += moreFuel;
			// we have added all the fuel we wanted to, return true
			return true;
		}
		// there is too much fuel. Fill the tank and send an error.
		CarModel.fuel = 1;
		// we have to much fuel, send false back to signify an overflow.
		return false;
	}
	public static boolean updateTotalMiles(double addedMiles){
		// check if this update will run us out of fuel
		if((CarModel.fuel*TANK_VOLUME - addedMiles/MPG) >= 0){
			// if we will have fuel, add the passed miles to the total miles
			CarModel.totalMiles += addedMiles;
			// use up some fuel in the process
			CarModel.fuel -= addedMiles/(MPG*TANK_VOLUME);
			return true;
		}
		// we're out of fuel!
		return false;
	}
	public static boolean canStart(){
		if(CarModel.fuel > 0) {
			return true;
		}
		// no fuel, dont start.
		return false;
	}
}
