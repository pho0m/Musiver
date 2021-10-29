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
    stage.setTitle("Musiver TCT31");
  }

  public static void main(String[] args) {
    launch(args);
  }
}
