package cse360.model;

public class ContactModel {
	protected String name;
	protected String phoneNumber;

	public ContactModel(String name, String phoneNumber) {
		this.name = name;
		this.phoneNumber = phoneNumber;
	}
	// gets name
	public String getName(){
		return name;
	}
	// gets phoneNumber
	public String getPhoneNumber(){
		return phoneNumber;
	}
	// takes in a new number and updates the Contact's number
	public boolean updatePhoneNumber(String newNumber) {
		phoneNumber = newNumber;
		return true;
	}
}
