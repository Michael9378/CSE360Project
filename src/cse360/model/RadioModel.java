package cse360.model;

import java.util.ArrayList;

public class RadioModel {
	public ArrayList<StationModel> favoriteStations;
	public ArrayList<StationModel> availableStations;
	private double volume = 0;
	private StationModel currentStation;

	public RadioModel(){
		availableStations = new ArrayList<StationModel>();
		sampleStations();
		int test;
		test = 1;
	}

	public void sampleStations(){
        addStation("KUPD","Arizona's Real Rock!",97.9, "media/Cena.mp3");
        addStation("KBAQ","Your Classical Companion",89.5, "media/Cena.mp3");
        addStation("KEXX","Hits! Now!",103.9, "media/Cena.mp3");
        addStation("KOOL-FM","The Valley's Greatest Hits!",94.5, "media/Cena.mp3");
        addStation("KESZ","Holiday music station",99.9, "media/Cena.mp3");
        addStation("KMXP","80's, 90's, and Today!", 96.9, "media/Cena.mp3");
        addStation("KNRJ-The Beat","The Hip Hop Station",101.1, "media/Cena.mp3");
        addStation("KZON","The Valley's Hit Music Leader",101.5, "media/Cena.mp3");
        addStation("KYOT-FM","We Play Everything",95.5, "media/Cena.mp3");
        addStation("KZZP-KISSFM","The number 1 Hit Music Station in Phoenix",104.7, "media/Cena.mp3");
        addStation("JOHN-CN","This Sunday night, John Cena defends \n" + "his title in the WWE Suuuuuuuuper Slam!",92.3, "media/Cena.mp3");
	}

	public double getVolume(){
		return volume;
	}


	public void setVolume(double newVol){
		if(newVol >= 0 && 1 >= newVol)
			volume = newVol;
	}

	public String getCurrentStationName(){
		return currentStation.getName();
	}

	public String getCurrentStationDesc(){
		return currentStation.getDesc();
	}
	// returns the frequency of the next available station, or returns the current frequency
	public double getNextStation(double unfreq){
		// format frequency
		double freq = formatStation(unfreq);
		// check to make sure the frequency is within range
		if(88 <= freq && freq < 108){
			// loop through all possible stations between freq + 0.1 and 108.0
			// start loop at passed freq + 1
			for(int i = (int) (freq*10) + 1; i < 1080; i++){
				// check if current frequency is a station
				if(getStation(i/10.0) != -1){
					// if it is a station, return current frequency
					return i/10.0;
				}
			}
		}
		return freq;
	}
	// returns the frequency of the previously available station, or returns the current frequency
	public double getPrevStation(double unfreq){
		// format frequency
		double freq = formatStation(unfreq);
		// check to make sure the frequency is within range
		if(88 < freq && freq <= 108){
			// loop through all possible stations between freq - 0.1 and 88.0
			// start loop at passed freq - 1
			for(int i = (int) (freq*10) - 1; i >= 880; i--){
				// check if current frequency is a station
				if(getStation(i/10.0) != -1){
					// if it is a station, return current frequency
					return i/10.0;
				}
			}
		}
		return freq;
	}
	// get station based on frequency
	public int getStation(double freq){
		for(int i = 0; i < availableStations.size(); i++){
			if(availableStations.get(i).getFrequency() == freq)
				return i;
		}
		return -1;
	}
	// get station from name
	public int getStation(String stationName){
		for(int i = 0; i < availableStations.size(); i++){
			if(availableStations.get(i).getName().equals(stationName))
				return i;
		}
		return -1;
	}
	// return the station at the passed index.
	public StationModel returnStation(int index){
		if(index < availableStations.size()){
			return availableStations.get(index);
		}
		return null;
	}
	// set current station to passed index
	public boolean changeStation(int index){
		if(availableStations.size() > index && index > -1){
			currentStation = availableStations.get(index);
			play();
			return true;
		}
		else {
			return false;
		}
	}
	// add station to available station list
	public boolean addStation(String name, String desc, double unformatFrequency, String mp3File){
		double frequency = formatStation(unformatFrequency);
		// make sure the station name and frequency is available
		if(getStation(frequency) == -1 && getStation(name) == -1) {
			StationModel temp = new StationModel(name, desc, frequency, mp3File);
			availableStations.add(temp);
			sortStations();
			return true;
		}
		else {
			return false;
		}
	}
	// sort the stations in order by frequency
	private boolean sortStations(){
		boolean sorted = false;
		// this function should be called right after every addition
		// because of this, the only unsorted element is the last one
		// start at the last element and swap down the list until it is placed.
		if(availableStations.size() <= 1){
			// if only one or no elements, array is sorted, yay! Skip loop.
			sorted = true;
		}
		// stop loop when reaches the end or sorted is true.
		for(int i = availableStations.size()-1; i > 0 && !sorted; i--){
			if(availableStations.get(i).getFrequency() < availableStations.get(i - 1).getFrequency()){
				// the frequency of the previous station in the list is greater than the next
				// swap the two stations and set sorted to false
				StationModel temp = availableStations.get(i);
				availableStations.set(i, availableStations.get(i - 1));
				availableStations.set(i - 1, temp);
			}
			// if everything is fine, sorted is true
			else { sorted = true;}
		}
		return true;
	}
	// remove station from available station list
	public boolean removeStation(String name){
		// make sure the station name and frequency is available
		if(getStation(name) != -1) {
			availableStations.remove(getStation(name));
			return true;
		}
		else {
			return false;
		}
	}
	// remove station from available station list
	public boolean removeStation(double freq){
		// make sure the station name and frequency is available
		if(getStation(freq) != -1) {
			availableStations.remove(getStation(freq));
			return true;
		}
		else {
			return false;
		}
	}
	// function to play current station
	public boolean play(){
		currentStation.playStation();
		return true;
	}
	// function to stop current station
	public boolean stop(){
		currentStation.stopStation();
		return true;
	}
	// function to add a station to favorite list
	public boolean addFavorite(double freq){
		// check to make sure station is found
		if(getStation(freq) != -1){
			favoriteStations.add(availableStations.get(getStation(freq)));
			return true;
		}
		else {
			return false;
		}
	}
	// function to add a station to favorite list
	public boolean addFavorite(String name){
		// check to make sure station is found
		if(getStation(name) != -1){
			favoriteStations.add(availableStations.get(getStation(name)));
			return true;
		}
		else {
			return false;
		}
	}

	// get station based on frequency
	public int getFavoriteStation(double freq){
		for(int i = 0; i < favoriteStations.size(); i++){
			if(favoriteStations.get(i).getFrequency() == freq)
				return i;
		}
		return -1;
	}
	// get station from name
	public int getFavoriteStation(String stationName){
		for(int i = 0; i < favoriteStations.size(); i++){
			if(favoriteStations.get(i).getName().equals(stationName))
				return i;
		}
		return -1;
	}
	// remove station from available station list
	public boolean removeFavoriteStation(String name){
		// make sure the station name and frequency is available
		if(getFavoriteStation(name) != -1) {
			favoriteStations.remove(getFavoriteStation(name));
			return true;
		}
		else {
			return false;
		}
	}
	// remove station from available station list
	public boolean removeFavoriteStation(double freq){
		// make sure the station name and frequency is available
		if(getFavoriteStation(freq) != -1) {
			favoriteStations.remove(getFavoriteStation(freq));
			return true;
		}
		else {
			return false;
		}
	}
	private double formatStation(double frequency){
		// create a double with only one decimal place by
		// multiplying a double by 10, truncating to an int,
		// and then making back into a double
		int freqRound = (int) (frequency*10);
		// truncate freq to one decimal place
		double returnFrequency = freqRound/10.0;
		return returnFrequency;
	}

}
