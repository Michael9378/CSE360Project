package cse360.model;

import java.util.ArrayList;

public class UserModel {
	protected String name;
	public TripModel trip;
	public RadioModel radio;
	public ArrayList<ContactModel> contacts;

	// constructor takes in just a name
	public UserModel(String name){
		this.name = name;
		this.trip = new TripModel();
		this.radio = new RadioModel();
		this.contacts = new ArrayList<ContactModel>();
	}
	// constructor takes in a name and current trip if one exists
	public UserModel(String name, TripModel curTrip) {
		this.name = name;
		this.trip = curTrip;
		this.radio = new RadioModel();
		this.contacts = new ArrayList<ContactModel>();
	}

	// gets this user's name
	public String getName(){
		return name;
	}

	// takes in a name and number, creates a contact and adds it to the contacts list
	public boolean addContact(String name, String number) {
		contacts.add(new ContactModel(name,number));
		return true;
	}
	// searches by name or number to find contact, returns index of contact if found, else returns -1
	public int findContact(String compName) {
		for(int i = 0; i < contacts.size(); i++) {
			ContactModel temp = contacts.get(i);
			if(compName.equals(temp.getName()) || compName.equals(temp.getPhoneNumber())) {
				return i;
			}
		}
		// no contact found
		return -1;
	}
	// searches contacts by name and removes first instance of the contact
	public boolean removeContact(String name){
		// save index returned by findContact
		int delIndex = findContact(name);
		// check if its greater than -1
		if(delIndex >= 0) {
			// if so, delete the contact at the delIndex
			contacts.remove(delIndex);
			return true;
		}
		// if not found, return false.
		return false;
	}

}
