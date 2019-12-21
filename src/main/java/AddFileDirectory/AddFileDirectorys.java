package AddFileDirectory;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.images.Artwork;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class AddFileDirectorys {

    //TODO: implement vertical scroll for splitpane
    //TODO: figure out save format for library for reuse
    private File chosenDirectory;
    private VBox vBox;
    private TilePane tilePane;
    private ArrayList<String> genres = new ArrayList<>();
    private ArrayList<Artwork> albumArtwork = new ArrayList<>();
    private ArrayList<String> albums = new ArrayList<>();
    private ArrayList<String[]> albumTracks = new ArrayList<>();
    private ArrayList<String> tracks = new ArrayList<>();
    private HashMap<String, String> map = new HashMap<>();

    public AddFileDirectorys(File file, VBox vBox, TilePane tilePane){

        this.chosenDirectory = file;
        this.vBox = vBox;
        this.tilePane = tilePane;

    }

    public void populatePlayer() throws ReadOnlyFileException, IOException, TagException, InvalidAudioFrameException, CannotReadException {

        /*Genres genres = new Genres(chosenDirectory, vBox);
        AlbumArtwork albumArtwork = new AlbumArtwork(chosenDirectory, tilePane);
        genres.run();
        albumArtwork.run();*/
        getMusic(chosenDirectory);
        setMusic();
    }

    //TODO: Decide what type of data structure to use to hold songs/albums
    //TODO: Should a song object inherit from album class? Or vice versa?
    //TODO: Add ScrollPanes to Splitpane
    //TODO:
    private void getMusic(File file) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        File[] listOfFiles = file.listFiles();

        //Recursively walk through directories to retrieve all music files the chosen one contains within
        if (listOfFiles != null){
            for (File fil : listOfFiles)
            {
                //If the file is a directory, open it and check for files
                if (fil.isDirectory())
                {
                    getMusic(fil);
                }
                //If it is not a directory, search files for music
                else if (fil.toString().endsWith(".flac") || fil.toString().endsWith(".mp3") || fil.toString().endsWith(".m4a")  || fil.toString().endsWith(".m4p") ||fil.toString().endsWith(".mp4") || fil.toString().endsWith(".ogg")  || fil.toString().endsWith(".wav")  || fil.toString().endsWith(".aif")  || fil.toString().endsWith(".dsf")  || fil.toString().endsWith(".wma"))
                {
                    //Get audio file and tag information
                    AudioFile f = AudioFileIO.read(fil);
                    Tag tag = f.getTag();
                    String genre = tag.getFirst(FieldKey.GENRE);
                    String track = tag.getFirst(FieldKey.TRACK);
                    String title = tag.getFirst(FieldKey.TITLE);
                    String discNum = tag.getFirst(FieldKey.DISC_NO);
                    String albumArtist = tag.getFirst(FieldKey.ALBUM_ARTIST);
                    String album = tag.getFirst(FieldKey.ALBUM);
                    String year = tag.getFirst(FieldKey.YEAR);
                    String length = tag.getFirst(FieldKey.LYRICS);
                    Artwork artwork = tag.getFirstArtwork();
                    System.out.println(genres);
                    if (!genres.contains(genre)){
                        genres.add(genre);
                    }
                    //Add first track info to lists
                    if (tracks.isEmpty()){
                        tracks.add(track);
                        albums.add(album);
                        albumArtwork.add(artwork);
                    }
                    //When new album enters queue, push tracks to album and start anew
                    if (!albums.contains(album)){
                        String[] temp = new String[tracks.size()];
                        for (int i = 0; i < tracks.size(); i++){
                            temp[i] = tracks.get(i);
                        }
                        albumTracks.add(temp);
                        tracks.clear();
                        tracks.add(track);
                        albums.add(album);
                        albumArtwork.add(artwork);
                    }

                }
            }
        }

    }

    private void setMusic(){

        //Sort genres for alphabetical ordering
        Collections.sort(genres);
        //Push genres to list in window
        for (String str: genres) {
            javafx.scene.control.ToggleButton toggleButton = new javafx.scene.control.ToggleButton();
            toggleButton.setText(str);
            toggleButton.getStyleClass().add("styleClass.css");
            toggleButton.setAlignment(Pos.CENTER);
            vBox.getChildren().add(toggleButton);
        }
        //Push album art to list in window
        for (Artwork artwork: albumArtwork){
            ToggleButton toggleButton = new ToggleButton();
            Image image = new Image(new ByteArrayInputStream(artwork.getBinaryData()));
            ImageView imageView = new ImageView();
            toggleButton.setGraphic(imageView);
            imageView.setFitHeight(150);
            imageView.setFitWidth(150);
            imageView.imageProperty().bind(Bindings.when(toggleButton.selectedProperty()).then(image).otherwise(image));
            tilePane.getChildren().add(toggleButton);
        }

    }


}
