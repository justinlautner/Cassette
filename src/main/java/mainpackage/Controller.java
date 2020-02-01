package mainpackage;

import addfiledirectory.AddFileDirectorys;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import musicplayer.Playlist;

import java.awt.*;
import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML private AnchorPane anchorPane;
    @FXML private VBox vBox;
    @FXML private FlowPane flowPane;
    @FXML private ScrollPane scrollPane;
    @FXML private ProgressBar progressBar;
    @FXML private Button playButton, previousTrackButton, nextTrackButton, stopButton;
    @FXML private Label songLabel, artistLabel, albumLabel;
    @FXML private ToggleButton toggleViewButton;
    @FXML private ImageView nowPlayingCover;

    public Controller(){}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //this.progressBar.setVisible(false);
        this.playButton.setGraphic(new ImageView(new Image("images/playButton.png", 50, 58, true, true)));
        this.previousTrackButton.setGraphic(new ImageView(new Image("images/previousTrackButton.png", 50, 58, true, true)));
        this.nextTrackButton.setGraphic(new ImageView(new Image("images/nextTrackButton.png", 50, 58, true, true)));
        this.stopButton.setGraphic(new ImageView(new Image("images/stopButton.png", 50, 58, true, true)));

        this.toggleViewButton.setSelected(false);
        this.toggleViewButton.setText("Playlist View");

        //If directory saved load library upon program start
        Path savedAlbums = Paths.get("src/main/resources/saves/albums.txt");
        Path savedSongs = Paths.get("src/main/resources/saves/songs.txt");
        if (Files.exists(savedAlbums) & Files.exists(savedSongs)){
            MusicScene musicScene = new MusicScene(vBox, flowPane, progressBar, scrollPane);
            musicScene.setMusicScene();
        }

    }

    @FXML
    private void addFiles(){

        //Open directory chooser to add files
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose a Directory to add");
        File defaultDirectory = new File(System.getProperty("user.home"));
        chooser.setInitialDirectory(defaultDirectory);
        File chosenFolder = chooser.showDialog(anchorPane.getScene().getWindow());

        //If user chose a folder, commence search for music
        if (chosenFolder != null){
            AddFileDirectorys addFileDirectorys = new AddFileDirectorys(chosenFolder, vBox, flowPane, progressBar, scrollPane);
            addFileDirectorys.start();
        }


    }

    @FXML
    private void toggleViewButtonOnClick(){

        if (toggleViewButton.isSelected()){
            toggleViewButton.setText("Album Art View");
        }
        else{
            toggleViewButton.setText("Playlist View");
        }

    }

    @FXML
    private void aboutScene(){

        final Stage about = new Stage();
        about.initModality(Modality.APPLICATION_MODAL);
        about.setTitle("About Cassette...");
        VBox aboutVbox = new VBox(20);
        Text fill = new Text();
        fill.setText("Author: Justin Lautner <jlautner@protonmail.com>" + '\n' + '\n' + "A special thanks to..." + '\n' + "jaudiotagger <com.github.goxr3plus>" + '\n' +
                "JustFlac fork by drogatkin <https://github.com/drogatkin/JustFLAC>" + '\n' + '\n' +  "Inspiration from: " + '\n' +
                "Clementine Media Player <https://github.com/clementine-player/Clementine>" + '\n' + "MusicBee <https://getmusicbee.com>");
        fill.setFill(Paint.valueOf("#FFFFFF"));
        fill.setStyle("-fx-font-size: 16px;");
        aboutVbox.getChildren().add(fill);
        Scene aboutScene = new Scene(aboutVbox, 635, 180);
        aboutScene.setUserAgentStylesheet("styles/generalStyle.css");
        about.setScene(aboutScene);
        about.show();

    }

}
