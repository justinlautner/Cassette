package mainpackage;

import addfiledirectory.AddFileDirectorys;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import musicplayer.Playlist;
import playlistscene.PlaylistScene;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML private AnchorPane anchorPane;
    @FXML private StackPane stackPane;
    @FXML private VBox vBox;
    private ScrollPane scrollPane;
    @FXML private ProgressBar progressBar;
    @FXML private Button playButton, previousTrackButton, nextTrackButton, stopButton;
    @FXML private Label songLabel, artistLabel, albumLabel, startSeekLabel, endSeekLabel;
    @FXML private ToggleButton toggleViewButton;
    @FXML private ImageView nowPlayingCover;
    @FXML private Slider volumeSlider;
    @FXML private Slider seekSlider;
    @FXML private ImageView loadingAnimation;
    private FlowPane flowPane;
    private PlaylistScene playlistScene;
    private Stage primaryStage;
    private Playlist playlist;

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

        try {

            //Load panes and add them to the stackPane, to immediately instantiate playlist and album discovery views
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PlaylistScene.fxml"));
            AnchorPane anchorPane;
            anchorPane = loader.load();
            stackPane.getChildren().add(anchorPane);
            playlistScene = loader.getController();
            loader = new FXMLLoader(getClass().getResource("/fxml/AlbumDiscoveryPane.fxml"));
            scrollPane = loader.load();
            stackPane.getChildren().add(scrollPane);
            AlbumDiscoveryPane albumDiscoveryPane = loader.getController();
            flowPane = albumDiscoveryPane.flowPane;

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadMusic(){

        playlist = new Playlist(Controller.this, playlistScene, primaryStage, volumeSlider, playButton, seekSlider, startSeekLabel, endSeekLabel);

        playlistScene.setPlaylist(playlist);

        //If directory saved load library upon program start
        Path savedAlbums = Paths.get("src/main/resources/saves/albums.txt");
        Path savedSongs = Paths.get("src/main/resources/saves/songs.txt");
        if (Files.exists(savedAlbums) & Files.exists(savedSongs)){
            //isMusicLoaded = true;
            MusicScene musicScene = new MusicScene(vBox, flowPane, progressBar, scrollPane, playlist);
            musicScene.setMusicScene();
        }
        /* Due to a bug in Java's codebase, if playlist is instantiated in initialize method or before adding songs,
        *  directorychooser will fail to open, with the message
        *  #
           # A fatal error has been detected by the Java Runtime Environment:
           #
           #  SIGSEGV (0xb) at pc=0x00007f2b078a7fa0, pid=7240, tid=7318
           #
           # JRE version: OpenJDK Runtime Environment (11.0.6+10) (build 11.0.6+10-post-Ubuntu-1ubuntu118.04.1)
           # Java VM: OpenJDK 64-Bit Server VM (11.0.6+10-post-Ubuntu-1ubuntu118.04.1, mixed mode, sharing, tiered, compressed oops, g1 gc, linux-amd64)
           # Problematic frame:
           # C  [libpthread.so.0+0x9fa0]  pthread_mutex_lock+0x0
           #
           # Core dump will be written. Default location: Core dumps may be processed with "/usr/share/apport/apport %p %s %c %d %P" (or dumping to /mnt/Entertainment/Libraries/Coding-Projects/IntelliJ/Cassette/core.7240)
           #
           # If you would like to submit a bug report, please visit:
           #   https://bugs.launchpad.net/ubuntu/+source/openjdk-lts
           # The crash happened outside the Java Virtual Machine in native code.
           # See problematic frame for where to report the bug.
           #
        *  thus, i have tested to see if music has been loaded first, and placed it after directory added if not.
        *  This is apparently not relevant in windows, but only on linux machines.
        * */
        /*if (isMusicLoaded){
            playlist = new Playlist(Controller.this, playlistScene, primaryStage, volumeSlider, playButton, seekSlider, startSeekLabel, endSeekLabel);

            playlistScene.setPlaylist(playlist);
        }*/
    }

    public void setStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    @FXML
    private void addFiles(){

        //Open directory chooser to add files
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Choose a Directory to add");

        File defaultDirectory;
        if (Files.exists(Paths.get("~/Music"))){
            defaultDirectory = new File("~/Music");
        }
        else{
            defaultDirectory = new File(System.getProperty("user.home"));
        }

        chooser.setInitialDirectory(defaultDirectory);

        File chosenFolder = chooser.showDialog(primaryStage);

        //If user chose a folder, commence music search
        if (chosenFolder != null){
            AddFileDirectorys addFileDirectorys = new AddFileDirectorys(chosenFolder, vBox, flowPane, progressBar, scrollPane);
            addFileDirectorys.start();
        }


    }

    public void setNowPlaying(String songName, String artistName, String albumName, Image image){

        Platform.runLater(() -> {
            songLabel.setText(songName);
            artistLabel.setText(artistName);
            albumLabel.setText(albumName);
            nowPlayingCover.setImage(image);
        });

    }

    @FXML
    private void toggleViewButtonOnClick(){

        //Change views on toggle, bringing proper pane to front of stack and hiding the other
        ObservableList<Node> childs = this.stackPane.getChildren();
        Node topNode = childs.get(childs.size()-1);

        //This node will be brought to the front
        Node newTopNode = childs.get(childs.size()-2);

        //Hide the node not in the front of the stack
        topNode.setVisible(false);
        topNode.toBack();

        newTopNode.setVisible(true);

        //Change toggle button according to which view is selected
        //Text allows for new users to realize that they can change views
        if (toggleViewButton.isSelected()){
            toggleViewButton.setText("Album Art View");
        }
        else{
            toggleViewButton.setText("Playlist View");
        }

    }

    @FXML
    private void aboutScene(){

        //Create a scene to show authors, dependencies and inspirations
        final Stage about = new Stage();
        about.initModality(Modality.APPLICATION_MODAL);
        about.setTitle("About Cassette...");
        VBox aboutVbox = new VBox(20);
        Text fill = new Text();
        fill.setText("Author: Justin Lautner <jlautner@protonmail.com>" + '\n' + '\n' + "A special thanks to:" + '\n' + "jaudiotagger <com.github.goxr3plus>" + '\n' +
                "vlcj <https://github.com/caprica/vlcj>" + '\n' + '\n' +  "Inspiration from: " + '\n' +
                "Clementine Media Player <https://github.com/clementine-player/Clementine>" + '\n' + "MusicBee <https://getmusicbee.com>");
        fill.setFill(Paint.valueOf("#FFFFFF"));
        fill.setStyle("-fx-font-size: 16px;");
        aboutVbox.getChildren().add(fill);
        Scene aboutScene = new Scene(aboutVbox, 635, 180);
        aboutScene.setUserAgentStylesheet("styles/generalStyle.css");
        about.setScene(aboutScene);
        about.show();

    }

    @FXML
    private void pressPreviousTrack(){
        playlist.previousTrack();
    }

    @FXML
    private void pressPlay(){
        playlist.pauseMusic();
    }

    @FXML
    private void pressStop(){
        playlist.stopMusic();
    }

    @FXML
    private void pressNextTrack(){
        playlist.nextTrack();
    }

    @FXML
    private void userSeek(){
        float pos = (float) seekSlider.getValue();
        playlist.seek(pos);
    }
}
