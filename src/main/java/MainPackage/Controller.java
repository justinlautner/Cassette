package MainPackage;

import AddFileDirectory.AddFileDirectorys;
import AddFileDirectory.AlbumArtwork;
import AddFileDirectory.Genres;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
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
    private AnchorPane anchorPane, leftAnchorPane;
    @FXML
    private VBox vBox;
    @FXML
    private TilePane tilePane;

    public Controller(){}

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


    }

    @FXML
    private void addFiles() throws IOException, ReadOnlyFileException, TagException, InvalidAudioFrameException, CannotReadException {

        /*FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose a Directory to add");
        File defaultDirectory = new File(System.getProperty("user.home"));
        chooser.setInitialDirectory(defaultDirectory);
        File chosenFolder = chooser.showOpenDialog(anchorPane.getScene().getWindow());

        Thread thread = new Thread(new PlaySong(chosenFolder.toString()));
        thread.start();*/

        //Open directory chooser to add files
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose a Directory to add");
        File defaultDirectory = new File(System.getProperty("user.home"));
        chooser.setInitialDirectory(defaultDirectory);
        File chosenFolder = chooser.showDialog(anchorPane.getScene().getWindow());

        //listOfFiles = chosenFolder.listFiles();


        /*if (chosenFolder != null){
            Genres genres = new Genres(chosenFolder, vBox);
            AlbumArtwork albumArtwork = new AlbumArtwork(chosenFolder, tilePane);
            genres.run();
            albumArtwork.run();
        }*/
        //If user chose a folder, commence search for music
        if (chosenFolder != null){
            AddFileDirectorys addFileDirectorys = new AddFileDirectorys(chosenFolder, vBox, tilePane);
            addFileDirectorys.populatePlayer();
        }


        /*getGenres(chosenFolder);
        getGenreList();*/

    }

}
