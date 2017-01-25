package cse360.model;

public class StationModel {
	private String name;
	private String desc;
	private double frequency;
	private String mp3File;

	public StationModel(String name, String desc, double frequency, String mp3File){
		this.name = name;
		this.desc = desc;
		this.frequency = frequency;
		this.mp3File = mp3File;
	}

	public String getName(){
		return this.name;
	}

	public void setName(String newName){
		name = newName;
	}

	public String getDesc(){
		return this.desc;
	}

	public void setDesc(String newDesc){
		desc = newDesc;
	}

	public double getFrequency(){
		// truncate the frequency to one decimal place then return
		return this.frequency;
	}

	public void setFrequency(double newFreq){
		frequency = newFreq;
	}

	public String getMP3(){
		return this.mp3File;
	}

	public void setMP3(String newMp3){
		mp3File = newMp3;
	}

	public void playStation(){
		// code to start playing media from mp3 file name
	}

	public void stopStation(){
		// code to end music
	}
}
