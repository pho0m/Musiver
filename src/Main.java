/*
* Project Name: Musiver 
* Team:
* 1. Kiattiphoom Poonketkit 6402041520031
* 2. Sasithon Saelao        6402041520162
* Section : TCT 1 RA
*/

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {

  @Override
  public void start(Stage stage) throws Exception {
    Parent root = FXMLLoader.load(getClass().getResource("Screen.fxml"));
    Scene scene = new Scene(root);
    stage.setScene(scene);
    stage.show();
    stage.setResizable(false);
    stage.setTitle("Mini Project Musiver Music Player");
  }

  public static void main(String[] args) {
    launch(args);
  }
}
