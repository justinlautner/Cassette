package playlistscene;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import music.Song;
import musicplayer.Playlist;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

import java.io.*;
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
    @FXML private TableColumn<PlaylistSongDisplay, String> bitRateColumn;
    private ObservableList<PlaylistSongDisplay> playlistList = FXCollections.observableArrayList();
    private HashMap<PlaylistSongDisplay, Song> songHashMap = new HashMap<>();
    private HashMap<Song, PlaylistSongDisplay> songHashMapRev = new HashMap<>();
    private Playlist playlist;
    private ContextMenu playlistContextMenu = new ContextMenu();
    private MenuItem removeSelected = new MenuItem();
    private MenuItem clearPlaylist = new MenuItem();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        trackColumn.setCellValueFactory(new PropertyValueFactory<>("track"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        artistColumn.setCellValueFactory(new PropertyValueFactory<>("artist"));
        albumArtistColumn.setCellValueFactory(new PropertyValueFactory<>("albumArtist"));
        albumColumn.setCellValueFactory(new PropertyValueFactory<>("album"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        bitRateColumn.setCellValueFactory(new PropertyValueFactory<>("bitRate"));
        fileTypeColumn.setCellValueFactory(new PropertyValueFactory<>("fileType"));

        tableView.setItems(playlistList);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


        removeSelected.setText("Remove Selected");
        clearPlaylist.setText("Clear Playlist");
        playlistContextMenu.getItems().addAll(removeSelected, clearPlaylist);

    }

    public void setPlaylist(Playlist playlist){
        this.playlist = playlist;
    }

    public PlaylistScene(){

    }

    public ArrayList<Song> getCurrentPlaylist(){
        ArrayList<Song> currentPlaylist = new ArrayList<>();
        for (PlaylistSongDisplay psd: playlistList){
            currentPlaylist.add(songHashMap.get(psd));
        }
        return currentPlaylist;
    }

    public void setAlbumImage(byte[] contents) {

        Platform.runLater(() -> {
            if (contents != null){
                BackgroundImage backgroundImage = new BackgroundImage(new Image(new BufferedInputStream(new ByteArrayInputStream(contents))), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(1, 1, true, true, false, false));
                anchorPane.setBackground(new Background(backgroundImage));
            }
            else{
                anchorPane.setBackground(null);
            }
        });

    }

    public void addSongs(ArrayList<Song> songs){

        Platform.runLater(() -> {
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
                String bitRate = f.getAudioHeader().getBitRate() + " kbps";

                PlaylistSongDisplay display = new PlaylistSongDisplay(song.getTrack(), song.getTitle(), song.getArtist(), song.getAlbumArtist(), song.getAlbum(), realTrackLength, song.getYear(), bitRate, format);
                playlistList.add(display);
                songHashMap.put(display, song);
                songHashMapRev.put(song, display);
            }
        });
        createRowFactory();

    }// end addSongs method

    private void createRowFactory(){
        //Play selected song from playlist, to skip forward or back
        tableView.setRowFactory( tv -> {
            TableRow<PlaylistSongDisplay> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                //On double click of song, play it
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    playlist.playPlaylistSelection(songHashMap.get(row.getItem()));
                }
                //On right click open context menu
                if (event.getButton() == MouseButton.SECONDARY) {
                    ObservableList<PlaylistSongDisplay> selectedItems = tableView.getSelectionModel().getSelectedItems();

                    ArrayList<Song> selectedIDs = new ArrayList<>();
                    for (PlaylistSongDisplay rowSelected : selectedItems) {
                        selectedIDs.add(songHashMap.get(rowSelected));
                    }
                    //Allow for removal of selected songs from playlist
                    removeSelected.setOnAction((actionEvent -> {
                        playlist.removePlaylistSelection(selectedIDs);
                        for (Song s: selectedIDs){
                            playlistList.remove(songHashMapRev.get(s));
                        }
                    }));
                    //Allow for clearing entire playlist
                    clearPlaylist.setOnAction(actionEvent -> {
                        playlist.clearPlaylist();
                        emptyTable();
                    });
                    //Show context menu at location of click
                    playlistContextMenu.show(row, event.getScreenX(), event.getScreenY());
                }
                //Undo selection if user clicks away from focused row
                if (event.getButton() == MouseButton.PRIMARY && (row.isEmpty())){
                    tableView.getSelectionModel().clearSelection();
                }
            });
            return row;
        });
        //TODO: Add drag listener to move songs around as desired
    }

    private void emptyTable(){
        Platform.runLater(() -> {
            playlistList.clear();
        });
    }

}
