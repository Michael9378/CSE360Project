package cse360;

import cse360.view.LoginManager;
import javafx.application.Application;
import javafx.scene.Scene;

import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainApp extends Application {
	@Override
	public void start(Stage stage) {
		Scene scene = new Scene(new StackPane());
		LoginManager loginManager = new LoginManager(scene);
		loginManager.showLoginScreen();
		stage.setScene(scene);
		stage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}
}
