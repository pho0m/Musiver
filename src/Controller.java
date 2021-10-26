
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class Controller implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    public void play(ActionEvent event) {
        System.out.println("play");
    }

    @FXML
    public void pause(ActionEvent event) {
        System.out.println("pause");
    }

    @FXML
    public void stop(ActionEvent event) {
        System.out.println("stop");
    }

    @FXML
    public void next(ActionEvent event) {
        System.out.println("next");
    }

    @FXML
    public void previous(ActionEvent event) {
        System.out.println("previous");
    }
}
