package AddFileDirectory;

import MainPackage.AlbumInfoPane;
import MusicPlayer.PlaySong;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.*;
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

import java.io.*;
import java.text.Collator;
import java.util.*;

public class AddFileDirectorys extends Thread {

    //TODO: figure out save format for library for reuse
    private File chosenDirectory;
    private VBox vBox;
    private TilePane tilePane;
    private ProgressBar progressBar;
    private LinkedList<Song> songLinkedList = new LinkedList<>();
    private LinkedList<Album> albumLinkedList = new LinkedList<>();
    //private LinkedList<Genre> genreLinkedList = new LinkedList<>();
    private String compareString = "";
    private ArrayList<String> genres = new ArrayList<>();
    private ArrayList<String> albums = new ArrayList<>();

    public AddFileDirectorys(File file, VBox vBox, TilePane tilePane, ProgressBar progressBar){

        this.chosenDirectory = file;
        this.vBox = vBox;
        this.tilePane = tilePane;
        this.progressBar = progressBar;

    }

    public void run(){
        Platform.runLater(this::populatePlayer);
    }

    public void populatePlayer() {

        getMusic(chosenDirectory);
        setMusicScene();

    }

    //TODO: Decide what type of data structure to use to hold songs/albums [HashTable to allow for fast playing of song?/Finding of file?] [Possibly Queue and Hashtable?] [Use PriorityQueue to gather songs, but then a Hashtable of them to save and use beyond that?]
    //TODO: Should a song object inherit from album class? Or vice versa?
    //TODO: Figure out how to get metadata, such as file type and length
    //TODO: Save state of application for reload
    //TODO: Decide: Have class objects to hold pre-sorted groups? (album, artist, genre, etc) or sort on call?
    private void getMusic(File file) {
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
                //If it is not a directory, search through for music files
                else if (fil.toString().endsWith(".flac") || fil.toString().endsWith(".mp3") || fil.toString().endsWith(".m4a")  || fil.toString().endsWith(".m4p") ||fil.toString().endsWith(".mp4") || fil.toString().endsWith(".ogg")  || fil.toString().endsWith(".wav")  || fil.toString().endsWith(".aif")  || fil.toString().endsWith(".dsf")  || fil.toString().endsWith(".wma"))
                {
                    getMusicTags(fil);
                }
            }
        }

    }

    private void getMusicTags(File fil){

        //Instantiate audio file and tag objects, with a try catch block so that if a song is unruly it will not stop the entire execution
        try{
            AudioFile f = AudioFileIO.read(fil);
            Tag tag = f.getTag();

            //Get all relevant tags
            String filePath = fil.toString();
            String genre = tag.getFirst(FieldKey.GENRE);
            if (!tag.hasField(FieldKey.GENRE)){
                genre = "unknown";
            }
            String track = tag.getFirst(FieldKey.TRACK);
            String title = tag.getFirst(FieldKey.TITLE);
            String discNum = tag.getFirst(FieldKey.DISC_NO);
            String artist = tag.getFirst(FieldKey.ARTIST);
            String albumArtist = tag.getFirst(FieldKey.ALBUM_ARTIST);
            String album = tag.getFirst(FieldKey.ALBUM);
            String year = tag.getFirst(FieldKey.YEAR);
            String lyrics = tag.getFirst(FieldKey.LYRICS);

            if (!compareString.equals(album)){

            }

            //Create song object with relevant tags embedded
            Song song = new Song(filePath, track, title, artist, albumArtist, album, year, lyrics, genre, discNum);
            songLinkedList.add(song);

            //Divide songs and genres into their own groups
            if (!genres.contains(song.getGenre())) {
                genres.add(song.getGenre());
            }
            if (!albums.contains(song.getAlbum())) {
                albums.add(song.getAlbum());
            }
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

    }

    private void setMusicScene(){

        //Create arrays for genres and albums, in order to prevent printing duplicate album covers
        /*ArrayList<String> genres = new ArrayList<>();
        ArrayList<String> albums = new ArrayList<>();*/

        //Sort songs by album artist, as is my preferance
        //TODO: Edit this sort to allow user to change comparators, e.g. album or year
        //songLinkedList.sort((song, t1) -> Collator.getInstance().compare(song.getAlbumArtist(), t1.getAlbumArtist()));

        songLinkedList.sort((song, t1) -> Collator.getInstance().compare(song.getAlbumArtist(), t1.getAlbumArtist()));
        //Iterate through songs and instantiate arrays
        /*for (Song song : songLinkedList) {
            if (!genres.contains(song.getGenre())) {
                genres.add(song.getGenre());
            }
            if (!albums.contains(song.getAlbum())) {
                albums.add(song.getAlbum());
            }

        }*/

        //sort genres for alphabetical ordering
        Collections.sort(genres);
        //Create ToggleButtons for each genre
        //TODO: Allow genre ToggleButtons to be opened and used to find new music to add to playlist
        for (String s: genres){
            ToggleButton toggleButton = new ToggleButton();
            Image check = new Image("images/check.png", 10, 10, true, true);
            Image unCheck = new Image("images/uncheck.png", 10, 10, true, true);
            ImageView imageView = new ImageView();
            imageView.imageProperty().bind(Bindings.when(toggleButton.selectedProperty()).then(check).otherwise(unCheck));
            toggleButton.setGraphic(imageView);
            toggleButton.setText(s);
            toggleButton.getStyleClass().add("styleClass.css");
            toggleButton.setAlignment(Pos.CENTER);
            vBox.getChildren().add(toggleButton);
        }

        //Add toggle buttons for album artworks, to allow user to search and play music by them
        //TODO: Allow ToggleButtons to drop song list for that album, to choose from
        System.out.println(albums);
        /*double progress = 0;
        progressBar.setVisible(true);
        progressBar.setProgress(progress);*/
        for (Song song : songLinkedList) {
            if (albums.contains(song.getAlbum())) {
                try{

                    File file = new File(song.getFilepath());
                    AudioFile f = AudioFileIO.read(file);
                    Tag tag = f.getTag();

                    //If album does not have a cover, provide a default picture
                    //TODO: Change togglebutton to button
                    if (!tag.hasField(FieldKey.COVER_ART)){
                        Image image = new Image("images/empty.jpeg", 150, 150, true, true);
                        ImageView imageView = new ImageView(image);
                        imageView.setCache(true);
                        imageView.setCacheHint(CacheHint.SPEED);

                        Button toggleButton = new Button(song.getAlbumArtist() + "\n" + song.getAlbum(), imageView);
                        toggleButton.setContentDisplay(ContentDisplay.TOP);
                        toggleButton.setGraphic(imageView);
                        toggleButton.setMaxWidth(150);
                        toggleButton.setAlignment(Pos.BASELINE_LEFT);
                        //imageView.imageProperty().bind(Bindings.when(toggleButton.selectedProperty()).then(image).otherwise(image));

                        toggleButton.setOnMouseClicked(mouseEvent -> {
                            if (mouseEvent.getClickCount() == 2){
                                PlaySong play = new PlaySong(song.getFilepath());
                                play.start();
                            }
                        });

                        tilePane.getChildren().add(toggleButton);
                    }
                    //Get album cover and create its button
                    else{
                        byte[] contents = tag.getFirstArtwork().getBinaryData();
                        Image image = new Image(new BufferedInputStream(new ByteArrayInputStream(contents)), 150, 150, true, true);
                        ImageView imageView = new ImageView(image);
                        imageView.setCache(true);
                        imageView.setCacheHint(CacheHint.SPEED);

                        Button toggleButton = new Button(song.getAlbumArtist() + "\n" + song.getAlbum(), imageView);
                        toggleButton.setContentDisplay(ContentDisplay.TOP);
                        toggleButton.setGraphic(imageView);
                        toggleButton.setMaxWidth(150);

                        toggleButton.setOnMouseClicked(mouseEvent -> {
                            if (mouseEvent.getClickCount() == 2){
                                PlaySong play = new PlaySong(song.getFilepath());
                                play.start();
                            }
                            if (mouseEvent.getClickCount() == 1){
                                AlbumInfoPane albumInfoPane = new AlbumInfoPane();
                            }
                        });
                        //imageView.imageProperty().bind(Bindings.when(toggleButton.selectedProperty()).then(image).otherwise(image));

                        tilePane.getChildren().add(toggleButton);
                    }

                    /*progress = (double) songLinkedList.indexOf(song)/songLinkedList.size();
                    progressBar.setProgress(progress);*/

                    albums.remove(song.getAlbum());
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
                } catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }

        System.out.println("ALL DONE :)");
        //progressBar.setVisible(false);
    }


}
