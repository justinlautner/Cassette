package playlistscene;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import music.Song;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

public class PlaylistScene implements Initializable{

    @FXML private AnchorPane anchorPane;
    @FXML private TableView<PlaylistSongDisplay> tableView;
    @FXML private TableColumn<PlaylistSongDisplay, Integer> trackColumn;
    @FXML private TableColumn<PlaylistSongDisplay, String> titleColumn;
    @FXML private TableColumn<PlaylistSongDisplay, String> artistColumn;
    @FXML private TableColumn<PlaylistSongDisplay, String> albumArtistColumn;
    @FXML private TableColumn<PlaylistSongDisplay, String> albumColumn;
    @FXML private TableColumn<PlaylistSongDisplay, String> lengthColumn;
    @FXML private TableColumn<PlaylistSongDisplay, String> yearColumn;
    @FXML private TableColumn<PlaylistSongDisplay, String> fileTypeColumn;
    private ObservableList<PlaylistSongDisplay> playlistList = FXCollections.observableArrayList();
    private HashMap<PlaylistSongDisplay, String> songFilePaths = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        trackColumn.setCellValueFactory(new PropertyValueFactory<>("track"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        albumArtistColumn.setCellValueFactory(new PropertyValueFactory<>("albumArtist"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        fileTypeColumn.setCellValueFactory(new PropertyValueFactory<>("fileType"));

        tableView.setItems(playlistList);
    }

    public PlaylistScene(){ }

    public void setAlbumImage(byte[] contents) {

        Platform.runLater(() -> {
            BackgroundImage backgroundImage = new BackgroundImage(new Image(new BufferedInputStream(new ByteArrayInputStream(contents))), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1, 1, true, true, false, false));
            anchorPane.setBackground(new Background(backgroundImage));
        });

    }

    public void addSongs(ArrayList<Song> songs){

        Platform.runLater(() -> {
            playlistList.clear();
            for (Song song: songs){
                AudioFile f = null;
                try {
                    f = AudioFileIO.read(new File(song.getFilepath()));
                } catch (CannotReadException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TagException e) {
                    e.printStackTrace();
                } catch (ReadOnlyFileException e) {
                    e.printStackTrace();
                } catch (InvalidAudioFrameException e) {
                    e.printStackTrace();
                }
                //TODO: add bitrate as column of playlist tableview
                String format = f.getAudioHeader().getFormat();
                String realTrackLength = f.getAudioHeader().getTrackLength() / 60 + ":" + f.getAudioHeader().getTrackLength() % 60;

                //In instances where track length returns a single digit in seconds, add the 0 in front. e.g. 4:08 instead of 4:8
                String temp = realTrackLength.substring(realTrackLength.indexOf(":"));
                if (temp.length() < 3){
                    realTrackLength = realTrackLength.substring(0, realTrackLength.length() - 1) + "0" + realTrackLength.substring(realTrackLength.length() - 1);
                }
                PlaylistSongDisplay display = new PlaylistSongDisplay(song.getTrack(), song.getTitle(), song.getArtist(), song.getAlbumArtist(), song.getAlbum(), realTrackLength, song.getYear(), format);
                playlistList.add(display);
                songFilePaths.put(display, song.getFilepath());
            }
        });

        //This will be used later
        /*tableView.setRowFactory( tv -> {
            TableRow<PlaylistSongDisplay> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                        *//*if (playSong != null){
                            System.out.println("IS SONG BEING KILL?");
                            //playSong.kill();
                            //playSong.kill();
                            *//**//*PCMProcessors pcmProcessors = new PCMProcessors();
                            pcmProcessors.stop();*//**//*
                        }*//*
                    //playSong = new PlaySong(songFilePaths.get(row.getItem()));

                    //songLabel.setText(row.getItem().title);

                    playlist.addSongs(new ArrayList<>(row.get));
                    //Platform.runLater(playlist);

                    if (!playlist.isAlive()){
                        playlist.start();
                    }
                }
            });
            return row;
        });*/

    }// end addSongs method

}
