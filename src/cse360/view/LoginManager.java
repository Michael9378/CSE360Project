package cse360.view;

import java.io.IOException;

import javafx.fxml.FXMLLoader;
import javafx.scene.*;

public class LoginManager {
	private Scene scene;
	public LoginManager(Scene scene){
		this.scene = scene;
	}
	public void showDashScreen(String userName){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("dashoverview.fxml"));
			scene.setRoot((Parent) loader.load());
			DashController controller = loader.<DashController>getController();
			controller.initDash(this, userName);
		} catch (IOException e){
			e.printStackTrace();
		}
	}
	public void showLoginScreen(){
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("loginscreen.fxml"));
			scene.setRoot((Parent) loader.load());
			LoginController controller = loader.<LoginController>getController();
			controller.initManager(this);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
