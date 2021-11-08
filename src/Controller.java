import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Controller implements Initializable {
    @FXML
    private Pane pane;
    @FXML
    private Label musicLabel, artistLabel, musicStartDuration, musicStopDuration;
    @FXML
    private Button playButton, previousButton, nextButton, shuffleButton, loopButton;
    @FXML
    private ComboBox<String> speedBox;
    @FXML
    private Slider volumeSlider;
    @FXML
    private ProgressBar songProgressBar;
    @FXML
    private ListView<String> musicList;
    @FXML
    private ImageView musicImage;

    private Media media;
    private MediaPlayer mediaPlayer;

    private File directory;
    private File[] files;

    private ArrayList<File> music;

    private int musicNumber;
    private int[] speeds = { 25, 50, 75, 100, 125, 150, 175, 200 };

    private Timer timer;
    private TimerTask task;

    private boolean running, loop;
    private String title, artist;
    private double fitWidth, fitHeight;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        music = new ArrayList<File>();
        directory = new File("music");
        files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                music.add(file);
            }
        }

        List<String> musicName = new ArrayList<String>();
        for (File file : files) {
            if (file.isFile()) {
                musicName.add(file.getName());
            }
        }
        musicList.getItems().addAll(musicName);

        for (int i = 0; i < speeds.length; i++) {
            speedBox.getItems().add(Integer.toString(speeds[i]) + "%");
        }
        speedBox.setOnAction(this::changeSpeed);
        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {

            @Override
            public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
                mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
            }
        });

        fitWidth = 30;
        fitHeight = 30;
        loop = false;
        songProgressBar.setStyle("-fx-accent: #000080;");
    }

    public void handleMouseClick(MouseEvent e) {

        if (e.getClickCount() == 2) {
            musicNumber = musicList.getSelectionModel().getSelectedIndex();

            if (mediaPlayer != null) {
                mediaPlayer.stop();

                if (running) {
                    cancelTimer();
                }
            }
            musicDetails(musicNumber);

            media = new Media(music.get(musicNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            musicLabel.setText(title);
            artistLabel.setText(artist);
            playMedia();

            System.out.println("clicked on " + musicList.getSelectionModel().getSelectedItem());
        }
    }

    public void playMedia() {
        try {
            if (mediaPlayer != null) {
                if (running == false) {
                    javafx.scene.image.Image image = new javafx.scene.image.Image(
                            getClass().getResource("icons/pause.png").toExternalForm());
                    ImageView iv = new ImageView(image);
                    iv.setFitHeight(40);
                    iv.setFitWidth(40);
                    playButton.setGraphic(iv);

                    beginTimer();
                    changeSpeed(null);
                    mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
                    mediaPlayer.play();

                    mediaPlayer.setOnEndOfMedia(new Runnable() {
                        public void run() {
                            if (loop == true) {
                                songProgressBar.setProgress(0);
                                mediaPlayer.seek(Duration.seconds(0));
                                mediaPlayer.play();

                            } else {
                                nextMedia();
                            }
                        }
                    });

                } else {
                    javafx.scene.image.Image image = new javafx.scene.image.Image(
                            getClass().getResource("icons/play.png").toExternalForm());
                    ImageView iv = new ImageView(image);
                    iv.setFitHeight(40);
                    iv.setFitWidth(40);
                    playButton.setGraphic(iv);

                    cancelTimer();
                    mediaPlayer.pause();
                }

            } else {
                alertError("No Music in Queue", "Please double click to select in list of music !");
            }

        } catch (Exception e) {
            alertError("No Music in Queue", "Please double click to select in list of music !");
        }
    }

    public void previousMedia() {
        if (mediaPlayer != null) {
            if (musicNumber > 0) {
                musicNumber--;

                mediaPlayer.stop();

                if (running) {
                    cancelTimer();
                }
                media = new Media(music.get(musicNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);

                musicDetails(musicNumber);
                setSelection(musicNumber);

                musicLabel.setText(title);
                artistLabel.setText(artist);

                playMedia();

            } else {
                musicNumber = music.size() - 1;
                mediaPlayer.stop();

                if (running) {
                    cancelTimer();
                }
                media = new Media(music.get(musicNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);

                musicDetails(musicNumber);
                setSelection(musicNumber);

                musicLabel.setText(title);
                artistLabel.setText(artist);

                playMedia();
            }
        } else {
            alertError("No Music in Queue", "Please double click to select in list of music !");
        }
    }

    public void nextMedia() {
        if (mediaPlayer != null) {
            if (musicNumber < music.size() - 1) {
                musicNumber++;
                mediaPlayer.stop();

                if (running) {
                    cancelTimer();
                }
                media = new Media(music.get(musicNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);

                musicDetails(musicNumber);
                setSelection(musicNumber);

                musicLabel.setText(title);
                artistLabel.setText(artist);

                playMedia();

            } else {
                musicNumber = 0;
                mediaPlayer.stop();
                media = new Media(music.get(musicNumber).toURI().toString());
                mediaPlayer = new MediaPlayer(media);

                musicDetails(musicNumber);
                setSelection(musicNumber);

                musicLabel.setText(title);
                artistLabel.setText(artist);

                playMedia();
            }
        } else {
            alertError("No Music in Queue", "Please double click to select in list of music !");
        }
    }

    public void shuffleMedia() {
        int rnd = new Random().nextInt(music.size());
        musicNumber = rnd;

        if (mediaPlayer != null) {
            mediaPlayer.stop();

            cancelTimer();

            media = new Media(music.get(musicNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);

            musicDetails(musicNumber);
            setSelection(musicNumber);

            musicLabel.setText(title);
            artistLabel.setText(artist);
            playMedia();

        } else {
            alertError("No Music in Queue", "Please double click to select in list of music !");
        }
    }

    public void loopMedia() {
        if (mediaPlayer != null) {
            if (loop == false) {
                loop = true;
                javafx.scene.image.Image image = new javafx.scene.image.Image(
                        getClass().getResource("icons/refresh.png").toExternalForm());
                ImageView iv = new ImageView(image);
                iv.setFitHeight(fitHeight);
                iv.setFitWidth(fitWidth);
                iv.setOpacity(0.25);
                loopButton.setGraphic(iv);
            } else {
                loop = false;
                javafx.scene.image.Image image = new javafx.scene.image.Image(
                        getClass().getResource("icons/refresh.png").toExternalForm());
                ImageView iv = new ImageView(image);
                iv.setFitHeight(fitHeight);
                iv.setFitWidth(fitWidth);
                loopButton.setGraphic(iv);
            }

        } else {
            alertError("No Music in Queue", "Please double click to select in list of music !");
        }
    }

    public void stopMedia() {
        songProgressBar.setProgress(0);
        mediaPlayer.seek(Duration.seconds(0));
    }

    public void setSelection(int musicNumber) {
        musicList.getSelectionModel().select(musicNumber);
        musicList.getFocusModel().focus(musicNumber);
        musicList.scrollTo(musicNumber);
    }

    public void changeSpeed(ActionEvent event) {

        if (speedBox.getValue() == null) {
            mediaPlayer.setRate(1);

        } else {
            mediaPlayer.setRate(
                    Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
        }
    }

    public void beginTimer() {
        timer = new Timer();
        task = new TimerTask() {

            public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                songProgressBar.setProgress(current / end);

                // set duration time
                // FIXME

                if (current / end == 1) {
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

    public void musicDetails(int musicNumber) {
        System.out.println("music number : " + musicNumber);
        Mp3File mp3file;
        ID3v2 id3v2Tag;

        try {
            mp3file = new Mp3File(files[musicNumber]);
            if (mp3file.hasId3v2Tag()) {
                id3v2Tag = mp3file.getId3v2Tag();

                title = id3v2Tag.getTitle();
                artist = id3v2Tag.getArtist();

                System.out.println("========================================");
                System.out.println("Artist: " + id3v2Tag.getArtist());
                System.out.println("Title: " + id3v2Tag.getTitle());
                System.out.println("Album: " + id3v2Tag.getAlbum());
                System.out.println("Year: " + id3v2Tag.getYear());
                System.out.println("========================================");

                byte[] albumImageData = id3v2Tag.getAlbumImage();
                if (albumImageData != null) {
                    Image img = new Image(new ByteArrayInputStream(albumImageData));
                    musicImage.setImage(img);
                    musicImage.setFitWidth(166);
                    musicImage.setFitHeight(167);

                    System.out.println("Have album image data, length: " + albumImageData.length + " bytes");
                    System.out.println("Album image mime type: " + id3v2Tag.getAlbumImageMimeType());
                } else {
                    javafx.scene.image.Image image = new javafx.scene.image.Image(
                            getClass().getResource("icons/music-placeholder.png").toExternalForm());
                    musicImage.setFitWidth(166);
                    musicImage.setFitHeight(167);
                    musicImage.setPreserveRatio(true);
                    musicImage.setImage(image);
                }

            }

        } catch (UnsupportedTagException | InvalidDataException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public void alertError(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);

        alert.showAndWait();
    }

}