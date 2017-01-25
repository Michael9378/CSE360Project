package cse360.view;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cse360.model.CarModel;
import cse360.model.ContactModel;
import cse360.model.SessionModel;
import cse360.model.StationModel;
import cse360.model.UserModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;

public class DashController {
	// Driving
	@FXML private Button engine, logoutButton, accel, decel;
	@FXML private TextField velocityInput;
	@FXML private Text currentAccel, currentVelocity,currentVelocity2,currentVelocity3,currentVelocity4, avgVelocity, maxVelocity, tripTime, tripDistance, odometer, currentFuel;
	private final int MAX_SPEED = 999, MIN_SPEED = -99;
	boolean engineStatus = false;
	boolean firstStart = true;
	boolean logout = true;
	// user to be updated form login screen in future
	UserModel curUser = new UserModel("User1");
	// Radio
	@FXML private Slider freqSlider, volSlider;
	@FXML private Text freq, stationDesc, volLevel;
	@FXML private MenuItem setFav1, setFav2, setFav3, setFav4, setFav5, setFav6;
	@FXML private Button fav1, fav2, fav3, fav4, fav5, fav6, seekMore, seekLess;
	private double currentFreq = 88;
	private double fav1Freq = 0, fav2Freq = 0, fav3Freq = 0, fav4Freq = 0, fav5Freq = 0, fav6Freq = 0;
	private double radioVol = 0;
	private Media song;
	private MediaPlayer mediaPlayer;
	private boolean playingMusic = false;
	// phone
	@FXML private Button phoneCall, phoneEnd, phoneOctothorpe, phoneStar, phone6, phone1, phone4, phone2, phone5, phone9, phone7, phone3, phone0, phone8;
	@FXML private TextArea numberDisplay;
	@FXML private MenuButton callList, deleteList;
	@FXML private TextField newConName, newConNum;
	@FXML private Button addContact;
	@FXML private Label phoneState;
	@FXML private TableView<SessionModel> table;
	private String callString = "";
	// navigation
	@FXML private Slider map;
	int numSessions = 0;
	public void initialize(){}

	public void initDash(final LoginManager loginManager, String userName){
		// List to hold SessionModel objects to populate table.
		ObservableList<SessionModel> data = FXCollections.observableArrayList();

		// Location of session data.
		String fileName = "bin/resources/sessiondata";

		// I don't remember why I initialized this here and I'm too scared to take it out.
		String line = null;

		// Session ID column.
		TableColumn sessionIDColumn = new TableColumn ("Session ID");
		sessionIDColumn.setCellValueFactory(new PropertyValueFactory<SessionModel, Integer>("sessionID"));

		// Trip duration column.
		TableColumn sessionDurationColumn = new TableColumn ("Duration");
		sessionDurationColumn.setCellValueFactory(new PropertyValueFactory<SessionModel, String>("tripDuration"));

		// Trip distance column.
		TableColumn sessionDistanceColumn = new TableColumn ("Distance");
		sessionDistanceColumn.setCellValueFactory(new PropertyValueFactory<SessionModel, Double>("tripDistance"));

		// Fuel level column.
		TableColumn sessionFuelColumn = new TableColumn ("Fuel Level");
		sessionFuelColumn.setCellValueFactory(new PropertyValueFactory<SessionModel, String>("fuelLevel"));

		// Average velocity column.
		TableColumn sessionAvgColumn = new TableColumn ("Average Velocity");
		sessionAvgColumn.setCellValueFactory(new PropertyValueFactory<SessionModel, Integer>("avgVelocity"));

		// Top velocity column.
		TableColumn sessionTopColumn = new TableColumn ("Top Velocity");
		sessionTopColumn.setCellValueFactory(new PropertyValueFactory<SessionModel, Integer>("topVelocity"));

		// Array to hold session data.
		SessionModel dataArray[] = null;

		// Load session data from resource file.
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			// Parse number of session objects and create array of appropriate size.
			line = bufferedReader.readLine();
			numSessions = Integer.parseInt(line);
			dataArray = new SessionModel[numSessions];

			// Create session objects and populate array with them.
			for(int i = 0; i < numSessions; i++){
				String name = bufferedReader.readLine();
				String tempNum = bufferedReader.readLine();
				int sessionNum = Integer.parseInt(tempNum);
				String duration = bufferedReader.readLine();
				String tempDistance = bufferedReader.readLine();
				double distance = Double.parseDouble(tempDistance);
				String fuel = bufferedReader.readLine();
				String tempAvg = bufferedReader.readLine();
				int avg = Integer.parseInt(tempAvg);
				String tempTop = bufferedReader.readLine();
				int top = Integer.parseInt(tempTop);
				dataArray[i] = new SessionModel(name, sessionNum, duration, distance, fuel, avg, top);
			}
			bufferedReader.close();
		}
		catch(FileNotFoundException ex) {
            System.out.println(
                "Unable to open file '" +
                fileName + "'");
        }
        catch(IOException ex) {
            ex.printStackTrace();
        }

		// Add to table sessions with appropriate user name.
		for(int i = 0; i < numSessions; i++){
			if(dataArray[i].getUserName().equals(userName)){
				data.add(dataArray[i]);
			}
		}

		// Populate table.
		table.setItems(data);
		table.getColumns().addAll(sessionIDColumn, sessionDurationColumn, sessionDistanceColumn, sessionFuelColumn, sessionAvgColumn, sessionTopColumn);

		// DRIVING
		/*---------------------------------------------*/

		// do not allow movement until car starts
		accel.setDisable(true);
		decel.setDisable(true);
		velocityInput.setDisable(true);

		engine.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				if(engineStatus){
					engineStatus = false;
					engine.setText("On");
					curUser.trip.setSpeed(0);
					enableControls(false);
				}
				else if(firstStart){
					startTime();
					engineStatus = true;
					firstStart = false;
					engine.setText("Off");
					enableControls(true);
				}
				else{
					engineStatus = true;
					engine.setText("Off");
					enableControls(true);
				}
			}
		});

		logoutButton.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				if(curUser.trip.getVelocity() == 0){

					// Array list to read in file to memory.
					ArrayList<String> list = new ArrayList<String>();

					// This is why I'm scared.
					String line = null;

					// Variable to hold current session number.
					int seshNum = 0;

					// Read old file into memory, make appropriate changes, and write back.
					try{

						FileReader fileReader = new FileReader(fileName);
						BufferedReader bufferedReader = new BufferedReader(fileReader);

						// Read into memory.
						while((line = bufferedReader.readLine()) != null){
							list.add(line);
						}

						fileReader.close();

						// Increment session number.
						seshNum = Integer.parseInt(list.get(0));
						seshNum++;

						// Add current session data.
						list.set(0, Integer.toString(seshNum));
						list.add(userName);
						list.add(Integer.toString(seshNum-1));
						list.add(tripTime.getText());
						list.add(tripDistance.getText());
						list.add(currentFuel.getText());
						list.add(avgVelocity.getText());
						list.add(maxVelocity.getText());

						// Write back.
						BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
						for(int i = 0; i < list.size(); i++){
							bw.write(list.get(i));
							bw.newLine();
						}
						bw.flush();
						bw.close();
					}
					catch (IOException ex){
						ex.printStackTrace();
					}
					loginManager.showLoginScreen();
				}
				else {
					// show error that the car is moving
				}
			}
		});
		// speed controllers
		// get the current acceleration, convert to int, increment and then reset current accel
		accel.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				// get current accel
				int temp = curUser.trip.getAcceleration();
				//int temp = Integer.parseInt(currentAccel.getText());
				// increment
				temp ++;
				// update trip object
				curUser.trip.setAcceleration(temp);
			}
		});
		// get the current acceleration, convert to int, decrement and then reset current accel
		decel.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				// get current accel
				int temp = curUser.trip.getAcceleration();
				// decrement
				temp --;
				// update trip object
				curUser.trip.setAcceleration(temp);
			}
		});
		velocityInput.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				int setVelocity = Integer.parseInt(velocityInput.getText());
				curUser.trip.setAcceleration(0);
				if(setVelocity > MAX_SPEED){
					setVelocity = MAX_SPEED;
				}
				if(setVelocity < MIN_SPEED){
					setVelocity = MIN_SPEED;
				}
				curUser.trip.setSpeed(setVelocity);
				velocityInput.setText("");
			}
		});

		// END DRIVING
		/*---------------------------------------------*/
		// RADIO

		// create slider functionality
		freqSlider.setMax(20.0);
		freqSlider.setOnMouseDragged(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				int temp = (int) ((freqSlider.getValue() + 88)*10);
				currentFreq = temp/10.0;
				updateRadioGui();
			}
		});

		// set max slider to 1 to match set volume for media player
		volSlider.setMax(1.0);
		volSlider.setOnMouseDragged(new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent event) {
				// set the new volume to the slider value
				updateVolume(volSlider.getValue());
			}
		});
		// enable the favorite drop down and buttons
		favoriteStationFunction();
		// enable seek functionalities
		seekMore.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				if(playingMusic){
					mediaPlayer.stop();
					playingMusic = false;
				}
				currentFreq = curUser.radio.getNextStation(currentFreq);
				freqSlider.setValue((currentFreq - 88));
				updateRadioGui();
			}
		});
		seekLess.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				if(playingMusic){
					mediaPlayer.stop();
					playingMusic = false;
				}
				currentFreq = curUser.radio.getPrevStation(currentFreq);
				freqSlider.setValue((currentFreq - 88));
				updateRadioGui();
			}
		});
		// have slider that starts at 0 to set current radioVol

		// END RADIO
		/*---------------------------------------------*/
		// PHONE

		populateContacts();
		enableDialer();
		// when call button pressed, call number entered
		// on end call, return volume to current vol

		addContact.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				String newName = newConName.getText();
				String newNum = newConNum.getText();
				if(newName.length() > 0 && newNum.length() > 0){
					ContactModel temp = new ContactModel(newName, newNum);
					addContactList(temp);
					curUser.addContact(newName, newNum);
					newConName.setText("");
					newConNum.setText("");
				}
			}
		});

		// END PHONE
		/*---------------------------------------------*/
		// START NAVIGATION

		map.setMax(1.0);
		map.setDisable(true);

		// END NAVIGATION
		/*---------------------------------------------*/

	}

	// Helper Functions

	public void startTime(){
		// this code to be run once every second
		Runnable updateTasks = new Runnable() {
		    public void run() {
		    	if(!curUser.trip.updateStats()){
		    		curUser.trip.setSpeed(0);
		    	}
		    	updateDriveGui();
		    	updateNavMap();
		    }
		};
		// schedule runnable to execute "updateTasks" once every second
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
		executor.scheduleAtFixedRate(updateTasks, 0, 1, TimeUnit.SECONDS);
	}

	public void enableControls(boolean t){
		// set movement buttons opposite of logout button
		accel.setDisable(!t);
		decel.setDisable(!t);
		velocityInput.setDisable(!t);
		logout = !t;
	}
	// functions for drive tab
	// updates the text labels on drive tab
	public boolean updateDriveGui(){
		DecimalFormat tens = new DecimalFormat("0.0"); // for fuel and odometer
		DecimalFormat hundreds = new DecimalFormat("0.00"); // for trip
		// set texts of new values
		currentVelocity.setText(Integer.toString(curUser.trip.getVelocity()));
		currentVelocity2.setText(Integer.toString(curUser.trip.getVelocity()));
		currentVelocity3.setText(Integer.toString(curUser.trip.getVelocity()));
		currentVelocity4.setText(Integer.toString(curUser.trip.getVelocity()));
		currentAccel.setText(Integer.toString(curUser.trip.getAcceleration()));
		avgVelocity.setText(Integer.toString(curUser.trip.getAvgVelocity()));
		maxVelocity.setText(Integer.toString(curUser.trip.getMaxVelocity()));
		tripDistance.setText(hundreds.format(curUser.trip.getTripdistance()));
		tripTime.setText(secToTime(curUser.trip.getTripduration()));
		currentFuel.setText(tens.format(CarModel.fuel*100)+"%");
		odometer.setText(tens.format(CarModel.totalMiles));
		if(logout && curUser.trip.getVelocity() == 0){
			logoutButton.setDisable(false);
		}
		else {
			logoutButton.setDisable(true);
		}
		return true;
	}
	// takes in int seconds and turns it into a mm:ss format
	public String secToTime(int toteSeconds){
		int minutes = 0;
		double seconds = 0;
		String str = "";
		seconds = toteSeconds%60;
		minutes = toteSeconds/60;
		DecimalFormat time = new DecimalFormat("00");
		str = minutes + ":" + time.format(seconds);
		return str;
	}
	// radio functions
	// update the slider's label to reflect current station
	public boolean updateRadioGui(){
		// set a decimal format for frequency
		DecimalFormat station = new DecimalFormat("0.0");
		// set the frequency
		freq.setText(station.format(currentFreq) + " FM");
		// check if we are at a station
		int currentStation = curUser.radio.getStation(currentFreq);
		if(currentStation != -1){
			// if we are, set the description
			String desc = curUser.radio.returnStation(currentStation).getDesc();
			stationDesc.setText(desc);
			// and grab the mp3 file associated with the station
			String file = curUser.radio.returnStation(currentStation).getMP3();
			file = Paths.get(file).toUri().toString();
			// and play the file
			song = new Media(file);
			mediaPlayer = new MediaPlayer(song);
			mediaPlayer.play();
			playingMusic = true;
			updateVolume(curUser.radio.getVolume());
		}
		else {
			mediaPlayer.stop();
			playingMusic = false;
		}
		return true;
	}
	// set functionality for favorite buttons and drop down list
	public void favoriteStationFunction(){
		// set current Freq to favorite buttons
		setFav1.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				// save current freq as fav1Freq
				fav1Freq = currentFreq;
			}
		});
		setFav2.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				// save current freq as fav2Freq
				fav2Freq = currentFreq;
			}
		});
		setFav3.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				// save current freq as fav3Freq
				fav3Freq = currentFreq;
			}
		});
		setFav4.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				// save current freq as fav4Freq
				fav4Freq = currentFreq;
			}
		});
		setFav5.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				// save current freq as fav5Freq
				fav5Freq = currentFreq;
			}
		});
		setFav6.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				// save current freq as fav6Freq
				fav6Freq = currentFreq;
			}
		});
		fav1.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				// save current freq as fav1Freq
				if(88 <= fav1Freq && fav1Freq <= 108){
					if(playingMusic){
						mediaPlayer.stop();
						playingMusic = false;
					}
					currentFreq = fav1Freq;
					freqSlider.setValue((currentFreq - 88));
					updateRadioGui();
				}
			}
		});
		fav2.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				// save current freq as fav2Freq
				if(88 <= fav2Freq && fav2Freq <= 108){
					if(playingMusic){
						mediaPlayer.stop();
						playingMusic = false;
					}
					currentFreq = fav2Freq;
					freqSlider.setValue((currentFreq - 88));
					updateRadioGui();
				}
			}
		});
		fav3.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				// save current freq as fav3Freq
				if(88 <= fav3Freq && fav3Freq <= 108){
					if(playingMusic){
						mediaPlayer.stop();
						playingMusic = false;
					}
					currentFreq = fav3Freq;
					freqSlider.setValue((currentFreq - 88));
					updateRadioGui();
				}
			}
		});
		fav4.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				// save current freq as fav4Freq
				if(88 <= fav4Freq && fav4Freq <= 108){
					if(playingMusic){
						mediaPlayer.stop();
						playingMusic = false;
					}
					currentFreq = fav4Freq;
					freqSlider.setValue((currentFreq - 88));
					updateRadioGui();
				}
			}
		});
		fav5.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				// save current freq as fav5Freq
				if(88 <= fav5Freq && fav5Freq <= 108){
					if(playingMusic){
						mediaPlayer.stop();
						playingMusic = false;
					}
					currentFreq = fav5Freq;
					freqSlider.setValue((currentFreq - 88));
					updateRadioGui();
				}
			}
		});
		fav6.setOnAction(new EventHandler<ActionEvent>(){
			@Override public void handle(ActionEvent event){
				// save current freq as fav6Freq
				if(88 <= fav6Freq && fav6Freq <= 108){
					if(playingMusic){
						mediaPlayer.stop();
						playingMusic = false;
					}
					currentFreq = fav6Freq;
					freqSlider.setValue((currentFreq - 88));
					updateRadioGui();
				}
			}
		});
	}
	// takes care of media volume, radio volume, and gui volume
	public void updateVolume(double volume){
		// set decimal format for volume level
		DecimalFormat ones = new DecimalFormat("0");
		// set ne radio volume
		curUser.radio.setVolume(volume);
		// update the text
		volLevel.setText(ones.format(volume*100) + "%");
		// set the new volume to current media player if playing
		if(playingMusic)
			mediaPlayer.setVolume(volume);

	}
	// populate the contact list with contacts
	public void populateContacts(){
		for(int i = 0; i < curUser.contacts.size(); i++) {
			ContactModel temp = curUser.contacts.get(i);
			// add to drop downs here
			addContactList(temp);
		}
	}
	// add functionality to the buttons on the phone panel
	public void enableDialer(){

		phoneCall.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent event){
                // call current number and clear callstring
            	startCall(numberDisplay.getText());
            	clearCallString();
            }
		});

		phoneEnd.setOnAction(new EventHandler<ActionEvent>(){
            @Override public void handle(ActionEvent event){
                // end current call and clear callstring
            	endCall();
            	clearCallString();
            }
		});

		phoneOctothorpe.setOnAction(new EventHandler<ActionEvent>(){
	            @Override public void handle(ActionEvent event){
	                   // add current button text value to callString
	                   callString += phoneOctothorpe.getText();
	                   updateNumberDisplay();
	            }
	     });

	     phoneStar.setOnAction(new EventHandler<ActionEvent>(){
	            @Override public void handle(ActionEvent event){
	                   // add current button text value to callString
	                   callString += phoneStar.getText();
	                   updateNumberDisplay();
	            }
	     });

	     phone6.setOnAction(new EventHandler<ActionEvent>(){
	            @Override public void handle(ActionEvent event){
	                   // add current button text value to callString
	                   callString += phone6.getText();
	                   updateNumberDisplay();
	            }
	     });

	     phone1.setOnAction(new EventHandler<ActionEvent>(){
	            @Override public void handle(ActionEvent event){
	                   // add current button text value to callString
	                   callString += phone1.getText();
	                   updateNumberDisplay();
	            }
	     });

	     phone4.setOnAction(new EventHandler<ActionEvent>(){
	            @Override public void handle(ActionEvent event){
	                   // add current button text value to callString
	                   callString += phone4.getText();
	                   updateNumberDisplay();
	            }
	     });

	     phone2.setOnAction(new EventHandler<ActionEvent>(){
	            @Override public void handle(ActionEvent event){
	                   // add current button text value to callString
	                   callString += phone2.getText();
	                   updateNumberDisplay();
	            }
	     });

	     phone5.setOnAction(new EventHandler<ActionEvent>(){
	            @Override public void handle(ActionEvent event){
	                   // add current button text value to callString
	                   callString += phone5.getText();
	                   updateNumberDisplay();
	            }
	     });

	     phone9.setOnAction(new EventHandler<ActionEvent>(){
	            @Override public void handle(ActionEvent event){
	                   // add current button text value to callString
	                   callString += phone9.getText();
	                   updateNumberDisplay();
	            }
	     });

	     phone7.setOnAction(new EventHandler<ActionEvent>(){
	            @Override public void handle(ActionEvent event){
	                   // add current button text value to callString
	                   callString += phone7.getText();
	                   updateNumberDisplay();
	            }
	     });

	     phone3.setOnAction(new EventHandler<ActionEvent>(){
	            @Override public void handle(ActionEvent event){
	                   // add current button text value to callString
	                   callString += phone3.getText();
	                   updateNumberDisplay();
	            }
	     });

	     phone0.setOnAction(new EventHandler<ActionEvent>(){
	            @Override public void handle(ActionEvent event){
	                   // add current button text value to callString
	                   callString += phone0.getText();
	                   updateNumberDisplay();
	            }
	     });

	     phone8.setOnAction(new EventHandler<ActionEvent>(){
	            @Override public void handle(ActionEvent event){
	                   // add current button text value to callString
	                   callString += phone8.getText();
	                   updateNumberDisplay();
	            }
	     });

	}

	public void updateNumberDisplay(){
		numberDisplay.setText(callString);
	}

	public void clearCallString(){
		callString = "";
		updateNumberDisplay();
	}

	public void addContactList(ContactModel temp){
		// construct a label for the menu items
		String menuLabel = temp.getName() + " | " + temp.getPhoneNumber();
		// create a menu item to add to the call list
		MenuItem menuItemCall = new MenuItem(menuLabel);
		menuItemCall.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        startCall(temp.getPhoneNumber());
		    }
		});
		// add the menu item to the call list
		callList.getItems().add(menuItemCall);
		// repeat process for delete list
		MenuItem menuItemDelete = new MenuItem(menuLabel);
		menuItemDelete.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        // code to delete contacts from contact lists and from menu items
		    	removeMenuItem(menuLabel);
		    	// remove the contact from the user list
		    	curUser.removeContact(temp.getName());
		    }
		});

		deleteList.getItems().add(menuItemDelete);
	}

	public void removeMenuItem(String menuLabel){
		// search to find menu item matching the passed label and delete it form both menus
		ObservableList<MenuItem> menuList = callList.getItems();
		for(int i = 0; i < menuList.size(); i++){
			if( menuLabel.equals(menuList.get(i).getText()) ){
				callList.getItems().get(i).setVisible(false);
				deleteList.getItems().get(i).setVisible(false);
				break;
			}
		}
	}

	public void startCall( String number){
		// stuff to handle a phone call start
		// check to make sure something was entered
		if(number.length() > 0){
			// disable contact list actions
			callList.setDisable(true);
			deleteList.setDisable(true);
			// on call, turn radio volume down to 0.
			radioVol = curUser.radio.getVolume();
			updateVolume(0);

			// construct call string for phone state
			String phoneStateText = "In call with: ";
			// search if phone number is a contact
			int contactIndex = curUser.findContact(number);
			// if it is a contact, set the string as the name
			if(contactIndex != -1)
				phoneStateText += curUser.contacts.get(contactIndex).getName();
			// otherwise, keep as number
			else
				phoneStateText += number;
			// set phone state
			phoneState.setText(phoneStateText);
		}
	}

	public void endCall(){
		// enable contact functions
		callList.setDisable(false);
		deleteList.setDisable(false);
		// return radio volume
		updateVolume(radioVol);
		clearCallString();
		phoneState.setText("Idle");
	}

	public void updateNavMap(){
		double tripPerc = 1-CarModel.fuel;
		map.setValue(tripPerc);
	}

}
