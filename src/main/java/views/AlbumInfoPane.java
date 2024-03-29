package views;

import javafx.scene.paint.Color;
import models.Song;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import models.SongDisplay;
import models.Playlist;
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
    private HashMap<SongDisplay, Song> songHashMap = new HashMap<>();
    private Playlist playlist;

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

    }

    public void setPlaylist(Playlist playlist){
        this.playlist = playlist;
    }

    public AlbumInfoPane(){

    }

    public void setAlbumImage(Image image) {
        albumImage.setImage(image);
    }

    public void setPaneStyle(String colorHex){

        System.out.println(colorHex);

        tableViewLeft.setStyle("-fx-text-fill: " + colorHex);
        gridPane.setStyle("-fx-background-color: " + colorHex);
        anchorPane.setStyle("-fx-background-color: " + colorHex);

        trackLeft.setStyle("-fx-text-background-color: " + changeColorShade(colorHex) + ";" + " -fx-background-color: " + colorHex + ";");
        titleLeft.setStyle("-fx-text-background-color: " + changeColorShade(colorHex) + ";" + " -fx-background-color: " + colorHex + ";");
        lengthLeft.setStyle("-fx-text-background-color: " + changeColorShade(colorHex) + ";" + " -fx-background-color: " + colorHex + ";");
        trackRight.setStyle("-fx-text-background-color: " + changeColorShade(colorHex) + ";" + " -fx-background-color: " + colorHex + ";");
        titleRight.setStyle("-fx-text-background-color: " + changeColorShade(colorHex) + ";" + " -fx-background-color: " + colorHex + ";");
        lengthRight.setStyle("-fx-text-background-color: " + changeColorShade(colorHex) + ";" + " -fx-background-color: " + colorHex + ";");

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
                songHashMap.put(songDisplay, song);
            }

            setSongSelectionListener(tableViewLeft);
            setSongSelectionListener(tableViewRight);

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

    private void setSongSelectionListener(TableView<SongDisplay> tableView){
        //Play chosen song from tables
        tableView.setRowFactory( tv -> {
            TableRow<SongDisplay> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {

                    ArrayList<Song> list = new ArrayList<>();

                    list.add(songHashMap.get(row.getItem()));
                    playlist.addSongs(list);

                    playlist.playPlaylistSelection(songHashMap.get(row.getItem()));
                }
            });
            return row ;
        });
    }

    //TODO: Experiment with color values to get most readable palette for majority of albums
    private String changeColorShade(String colorHex){
        //Get lighter or darker shade of album color so that text is always readable
        Color color = Color.valueOf(colorHex);

        //Change shade of all color palettes: RGB
        ArrayList<Double> colorWheel = new ArrayList<>();
        colorWheel.add(color.getRed());
        colorWheel.add(color.getGreen());
        colorWheel.add(color.getBlue());

        //Ensure that brighter colors (>.5) get darker
        if (colorWheel.get(0) > .5){
            do {
                for (int j = 0; j < colorWheel.size(); j++){
                    //Darken red, green, and blue equally to ensure result is a different shade of the same color
                    double temp2 = colorWheel.get(j) - (colorWheel.get(j) * .5);
                    colorWheel.set(j, temp2);
                }
                //Catch and fix situations where color is STILL too bright and unreadable, darken another time
            } while (colorWheel.get(0) > .75 || colorWheel.get(1) > .75 || colorWheel.get(2) > .75);
        }
        //Ensure that darker colors (<.5) get brighter
        else if (colorWheel.get(0) < .5){
            do {
                //Brighten red, green, and blue equally to ensure result is a different shade of the same color
                for (int j = 0; j < colorWheel.size(); j++){
                    double temp2 = colorWheel.get(j) + (colorWheel.get(j) * .5);
                    colorWheel.set(j, temp2);
                }
                //Catch and fix situations where color is STILL too dark and unreadable, brighten another time
            } while (colorWheel.get(0) < .25 || colorWheel.get(1) < .25 || colorWheel.get(2) < .25);
        }

        return String.format("#%02x%02x%02x", (int) (colorWheel.get(0) * 255), (int) (colorWheel.get(1) * 255), (int) (colorWheel.get(2) * 255));
    }

}//end Controller Class
