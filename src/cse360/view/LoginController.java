package cse360.view;

import javafx.event.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;

public class LoginController {
	@FXML private Button loginButton;
	@FXML private PasswordField passwordField;

	public void initialize(){}

	public void initManager(final LoginManager loginManager){
		loginButton.setOnAction(new EventHandler<ActionEvent>() {
		      @Override public void handle(ActionEvent e) {
		    	  loginManager.showDashScreen(passwordField.getText());
		      }
		});
	}
}