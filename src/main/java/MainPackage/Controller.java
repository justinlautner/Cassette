package MainPackage;

import AddFileDirectory.AddFileDirectorys;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
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
    private TilePane tilePane;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private ScrollPane scrollPaneLeft;
    @FXML
    private ScrollPane scrollPaneRight;
    @FXML
    private SplitPane splitPane;

    public Controller(){}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //this.progressBar.setVisible(false);

    }

    @FXML
    private void addFiles() throws IOException, ReadOnlyFileException, TagException, InvalidAudioFrameException, CannotReadException {

        //Open directory chooser to add files
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose a Directory to add");
        File defaultDirectory = new File(System.getProperty("user.home"));
        chooser.setInitialDirectory(defaultDirectory);
        File chosenFolder = chooser.showDialog(anchorPane.getScene().getWindow());

        //If user chose a folder, commence search for music
        if (chosenFolder != null){
            AddFileDirectorys addFileDirectorys = new AddFileDirectorys(chosenFolder, vBox, tilePane, progressBar);
            addFileDirectorys.start();
        }

    }

}
