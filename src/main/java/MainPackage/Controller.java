package MainPackage;

import AddFileDirectory.AddFileDirectorys;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private AnchorPane anchorPane;
    @FXML
    private VBox vBox;
    @FXML
    private FlowPane tilePane;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Button playButton, previousTrackButton, nextTrackButton;

    public Controller(){}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //this.progressBar.setVisible(false);
        /*this.playButton.setGraphic(new ImageView(new Image("images/playButton.png", 50, 50, true, true)));
        this.previousTrackButton.setGraphic(new ImageView(new Image("images/previousTrack.png", 50, 50, true, true)));
        this.nextTrackButton.setGraphic(new ImageView(new Image("images/nextTrack.png", 50, 50, true, true)));*/

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
            AddFileDirectorys addFileDirectorys = new AddFileDirectorys(chosenFolder, vBox, tilePane, progressBar, scrollPane);
            addFileDirectorys.start();
        }

    }

}
