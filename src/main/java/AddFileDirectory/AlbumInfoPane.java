package AddFileDirectory;

import MusicPlayer.PlaySong;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class AlbumInfoPane implements Initializable {

    @FXML private ImageView albumImage;
    @FXML private GridPane gridPane;
    @FXML private AnchorPane anchorPane;
    @FXML private TableView<SongDisplay> tableViewLeft;
    @FXML private TableColumn<SongDisplay, String> trackLeft;
    @FXML private TableColumn<SongDisplay, String> titleLeft;
    @FXML private TableColumn<SongDisplay, String> lengthLeft;
    @FXML private TableView<SongDisplay> tableViewRight;
    @FXML private TableColumn<SongDisplay, String> trackRight;
    @FXML private TableColumn<SongDisplay, String> titleRight;
    @FXML private TableColumn<SongDisplay, String> lengthRight;
    private ObservableList<SongDisplay> listLeft = FXCollections.observableArrayList();
    private ObservableList<SongDisplay> listRight = FXCollections.observableArrayList();
    private HashMap<SongDisplay, String> songFilePaths = new HashMap<>();
    private PlaySong playSong = new PlaySong();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Instantiate TableView and set style
        trackLeft.setCellValueFactory(new PropertyValueFactory<>("track"));
        titleLeft.setCellValueFactory(new PropertyValueFactory<>("title"));
        lengthLeft.setCellValueFactory(new PropertyValueFactory<>("length"));

        tableViewLeft.setItems(listLeft);

        trackRight.setCellValueFactory(new PropertyValueFactory<>("track"));
        titleRight.setCellValueFactory(new PropertyValueFactory<>("title"));
        lengthRight.setCellValueFactory(new PropertyValueFactory<>("length"));

        tableViewRight.setItems(listRight);

        /*tableViewLeft.setStyle("-fx-selection-bar: #7a330d; -fx-selection-bar-non-focused: #454545;");
        tableViewRight.setStyle("-fx-selection-bar: #7a330d; -fx-selection-bar-non-focused: #454545;");*/
    }

    public AlbumInfoPane(){

    }

    public void setAlbumImage(Image image) {
        albumImage.setImage(image);
    }

    public void setStyle(String colorHex){

        gridPane.setStyle("-fx-background-color: " + colorHex);
        anchorPane.setStyle("-fx-background-color: " + colorHex);

        trackLeft.setStyle("-fx-background-color: " + colorHex);
        titleLeft.setStyle("-fx-background-color: " + colorHex);
        lengthLeft.setStyle("-fx-background-color: " + colorHex);
        trackRight.setStyle("-fx-background-color: " + colorHex);
        titleRight.setStyle("-fx-background-color: " + colorHex);
        lengthRight.setStyle("-fx-background-color: " + colorHex);

        //-fx-selection-bar: #454545; for later?
        tableViewLeft.setStyle("-fx-selection-bar-non-focused: transparent; -fx-box-border: " + colorHex + ";");
        tableViewRight.setStyle("-fx-selection-bar-non-focused: transparent; -fx-box-border: " + colorHex + ";");

    }

    public void setFlowPane(ArrayList<Song> songs){

        try{
            for (Song song: songs){
                AudioFile f = AudioFileIO.read(new File(song.getFilepath()));

                //Conversion of time from seconds to minutes
                String realTrackLength = f.getAudioHeader().getTrackLength() / 60 + ":" + f.getAudioHeader().getTrackLength() % 60;

                //In instances where track length returns a single digit in seconds, add the 0 in front. e.g. 4:08 instead of 4:8
                String temp = realTrackLength.substring(realTrackLength.indexOf(":"));
                if (temp.length() < 3){
                    realTrackLength = realTrackLength.substring(0, realTrackLength.length() - 1) + "0" + realTrackLength.substring(realTrackLength.length() - 1);
                }
                if (songs.size() <= 5){
                    tableViewRight.setVisible(false);
                }
                //Distribute songs between two tables, in order to prevent one huge list
                SongDisplay songDisplay = new SongDisplay(song.getTrack(), song.getTitle(), realTrackLength);
                if (songs.indexOf(song) <= (songs.size()/2)){
                    listLeft.add(songDisplay);
                }
                else{
                    listRight.add(songDisplay);
                }
                songFilePaths.put(songDisplay, song.getFilepath());
            }
            //Play chosen song from tables
            tableViewLeft.setRowFactory( tv -> {
                TableRow<SongDisplay> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                        /*if (playSong != null){
                            System.out.println("IS SONG BEING KILL?");
                            //playSong.kill();
                            //playSong.kill();
                            *//*PCMProcessors pcmProcessors = new PCMProcessors();
                            pcmProcessors.stop();*//*
                        }*/
                        //playSong = new PlaySong(songFilePaths.get(row.getItem()));

                        //songLabel.setText(row.getItem().title);

                        playSong.setSong(songFilePaths.get(row.getItem()));
                        playSong.start();
                    }
                });
                return row ;
            });
            tableViewRight.setRowFactory( tv -> {
                TableRow<SongDisplay> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                        /*if (playSong != null){
                            System.out.println("IS SONG BEING KILL?");
                            //playSong.kill();
                            *//*PCMProcessors pcmProcessors = new PCMProcessors();
                            pcmProcessors.stop();*//*
                        }*/
                        //playSong = new PlaySong(songFilePaths.get(row.getItem()));

                        //songLabel.setText(row.getItem().title);

                        playSong.setSong(songFilePaths.get(row.getItem()));
                        playSong.start();
                    }
                });
                return row ;
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        }

    }//end setFlowPane() method

}//end Controller Class
