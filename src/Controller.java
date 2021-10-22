package application;

import java.net.URL;
import java.io.File;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.event.EventHandler;
import javafx.application.Application;

public class Controller implements Initializable {
	@FXML
	private Pane pane;
	@FXML
	private Label songLabel;
	@FXML
	private Button PLAYBUTTON, PAUSEBUTTON, RESETBUTTON, PREVIOUSEBUTTON, NEXTBUTTON;
	@FXML
	private ComboBox<String> SPEEDBOX;
	@FXML
	private Slider VOLUMESLIDER;
	@FXML
	private ProgressBar songProgressBar;
	
	private Media media;
	private MediaPlayer mediaPlayer;
	
	private File directory;
	private File[] files;
	
	private ArrayList<File> songs;
	
	private int songNumber;
	private int[] speeds = {25, 50, 75, 100, 125, 150, 175, 200};
	
	private Timer timer;
	private TimerTask task;
	
	private boolean running;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		songs = new ArrayList<File>();
		
		directory = new File("music");
		
		files = directory.listFiles();
		
		if(files != null) {
			
			for(File file : files) {
				
				songs.add(file);
			}
		}
		
		media = new Media(songs.get(songNumber).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		
		songLabel.setText(songs.get(songNumber).getName());
		
		for(int i = 0; i < speeds.length; i++) {
			
			SPEEDBOX.getItems().add(Integer.toString(speeds[i])+"%");
		}
		
		SPEEDBOX.setOnAction(this::CHANGESPEED);
		
		VOLUMESLIDER.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				
				mediaPlayer.setVolume(VOLUMESLIDER.getValue() * 0.01);			
			}
		});
		
		songProgressBar.setStyle("-fx-accent: #00FF00;");
	}
public void PLAYMEDIA() {
		
		beginTimer();
		CHANGESPEED(null);
		mediaPlayer.setVolume(VOLUMESLIDER.getValue() * 0.01);
		mediaPlayer.play();
	}
	
	public void PAUSEMEDIA() {
		
		cancelTimer();
		mediaPlayer.pause();
	}
	
	public void RESETMEDIA() {
		
		songProgressBar.setProgress(0);
		mediaPlayer.seek(Duration.seconds(0));
	}
	
	public void PREVIOUSEMEDIA() {
		
		if(songNumber > 0) {
			
			songNumber--;
			
			mediaPlayer.stop();
			
			if(running) {
				
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			
			PLAYMEDIA();
		}
		else {
			
			songNumber = songs.size() - 1;
			
			mediaPlayer.stop();
			
			if(running) {
				
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			
			PLAYMEDIA();
		}
	}
	
	public void NEXTMEDIA() {
		
		if(songNumber < songs.size() - 1) {
			
			songNumber++;
			
			mediaPlayer.stop();
			
			if(running) {
				
				cancelTimer();
			}
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			
			PLAYMEDIA();
		}
		else {
			
			songNumber = 0;
			
			mediaPlayer.stop();
			
			media = new Media(songs.get(songNumber).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songLabel.setText(songs.get(songNumber).getName());
			
			PLAYMEDIA();
		}
	}
	
	public void CHANGESPEED(ActionEvent event) {
		
		if(SPEEDBOX.getValue() == null) {
			
			mediaPlayer.setRate(1);
		}
		else {
			
			//mediaPlayer.setRate(Integer.parseInt(speedBox.getValue()) * 0.01);
			mediaPlayer.setRate(Integer.parseInt(SPEEDBOX.getValue().substring(0, SPEEDBOX.getValue().length() - 1)) * 0.01);
		}
	}
	
	public void beginTimer() {
		
		timer = new Timer();
		
		task = new TimerTask() {
			
			public void run() {
				
				running = true;
				double current = mediaPlayer.getCurrentTime().toSeconds();
				double end = media.getDuration().toSeconds();
				songProgressBar.setProgress(current/end);
				
				if(current/end == 1) {
					
					cancelTimer();
				}
			}
		};
		
		timer.scheduleAtFixedRate(task, 0, 1000);
	}
	
	public void cancelTimer() {
		
		running = false;
		timer.cancel();
	}
	
}

