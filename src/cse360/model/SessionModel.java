package cse360.model;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class SessionModel {
	public SimpleStringProperty userName;
	public SimpleIntegerProperty sessionID;
	public SimpleStringProperty tripDuration;
	public SimpleDoubleProperty tripDistance;
	public SimpleStringProperty fuelLevel;
	public SimpleIntegerProperty avgVelocity;
	public SimpleIntegerProperty topVelocity;

	public SessionModel(String name, int id, String duration, double distance, String fuel, int avg, int top){
		userName = new SimpleStringProperty(name);
		sessionID = new SimpleIntegerProperty(id);
		tripDuration = new SimpleStringProperty(duration);
		tripDistance = new SimpleDoubleProperty(distance);
		fuelLevel = new SimpleStringProperty(fuel);
		avgVelocity = new SimpleIntegerProperty(avg);
		topVelocity = new SimpleIntegerProperty(top);
	}

	public String getUserName(){
		return userName.get();
	}

	public int getSessionID(){
		return sessionID.get();
	}

	public String getTripDuration(){
		return tripDuration.get();
	}

	public double getTripDistance(){
		return tripDistance.get();
	}

	public String getFuelLevel(){
		return fuelLevel.get();
	}

	public int getAvgVelocity(){
		return avgVelocity.get();
	}

	public int getTopVelocity(){
		return topVelocity.get();
	}
}
