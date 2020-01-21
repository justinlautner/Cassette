package AddFileDirectory;

import MusicPlayer.PlaySong;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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
    private FlowPane flowPane;
    private ScrollPane scrollPane;
    private ProgressBar progressBar;
    private LinkedList<Song> songLinkedList = new LinkedList<>();
    private LinkedList<Album> albumLinkedList = new LinkedList<>();
    //private LinkedList<Genre> genreLinkedList = new LinkedList<>();
    private ArrayList<String> genres = new ArrayList<>();
    private ArrayList<String> albums = new ArrayList<>();
    private ArrayList<Song> singleAlbum = new ArrayList<>();
    private HashMap<Button, Integer> buttonLocation = new HashMap<>();
    private int locationNumber = 1, lastButtonChosen = 0;
    private Pane pane;
    private boolean albumIsOpen;
    private PlaySong playSong = new PlaySong();

    public AddFileDirectorys(File file, VBox vBox, FlowPane flowPane, ProgressBar progressBar, ScrollPane scrollPane){

        this.chosenDirectory = file;
        this.vBox = vBox;
        this.flowPane = flowPane;
        this.progressBar = progressBar;
        this.scrollPane = scrollPane;

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

            //Create song object with relevant tags embedded
            //TODO: replace track Integer with String to prevent empty string issue
            Song song;
            if (track.equals("")){
                song = new Song(filePath, -1, title, artist, albumArtist, album, year, lyrics, genre, discNum);
            }
            else{
                song = new Song(filePath, Integer.parseInt(track), title, artist, albumArtist, album, year, lyrics, genre, discNum);
            }
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

        //Sort songs by album artist, as is my preference
        //TODO: Edit this sort to allow user to change comparators, e.g. album or year
        songLinkedList.sort((song, t1) -> Collator.getInstance().compare(song.getAlbumArtist(), t1.getAlbumArtist()));

        for (int i = 0; i < songLinkedList.size(); i++){
            String curr = songLinkedList.get(i).getAlbum();
            //System.out.println(songLinkedList.get(i).getTitle());
            //System.out.println("current: " + curr);
            if (i != 0){
                String prev = songLinkedList.get(i - 1).getAlbum();
                //System.out.println("previous: " + prev);
                if (!(curr.equals(prev))) {
                    /*for (Song song: singleAlbum){
                        System.out.println(song.getAlbum());
                    }*/
                    System.out.println(singleAlbum);
                    //Album album = new Album(singleAlbum);
                    singleAlbum.sort(new Comparator<Song>() {
                        @Override
                        public int compare(Song song, Song t1) {
                            return Integer.compare(song.getTrack(), t1.getTrack());
                        }
                    });
                    albumLinkedList.add(new Album(singleAlbum));
                    singleAlbum.clear();
                    for (Album temp : albumLinkedList) {
                        System.out.println(temp.getAlbum());
                    }
                    System.out.println("is this thing on?");
                }
            }

            singleAlbum.add(songLinkedList.get(i));
        }//end for loop
        albumLinkedList.add(new Album(singleAlbum));

        //sort genres for alphabetical ordering
        Collections.sort(genres);

        //Create buttons for each genre
        Accordion accordion = new Accordion();
        for (String s: genres){
            /*ToggleButton toggleButton = new ToggleButton();
            Image check = new Image("images/check.png", 10, 10, true, true);
            Image unCheck = new Image("images/uncheck.png", 10, 10, true, true);
            ImageView imageView = new ImageView();
            imageView.imageProperty().bind(Bindings.when(toggleButton.selectedProperty()).then(check).otherwise(unCheck));
            toggleButton.setGraphic(imageView);
            toggleButton.setText(s);
            toggleButton.getStyleClass().add("generalStyle.css");
            toggleButton.setAlignment(Pos.CENTER);
            vBox.getChildren().add(toggleButton);*/

            TitledPane titledPane = new TitledPane();
            Image check = new Image("images/check.png", 10, 10, true, true);
            Image unCheck = new Image("images/uncheck.png", 10, 10, true, true);
            ImageView imageView = new ImageView();
            imageView.imageProperty().bind(Bindings.when(titledPane.expandedProperty()).then(check).otherwise(unCheck));
            titledPane.setGraphic(imageView);
            titledPane.setText(s);
            titledPane.getStyleClass().add("generalStyle.css");
            titledPane.setAlignment(Pos.CENTER);
            accordion.getPanes().add(titledPane);
        }
        vBox.getChildren().add(accordion);

        //Add album buttons from which to search through and choose songs to play
        for (Album album : albumLinkedList) {
            try{

                //Get file information from which to return tags
                //Assuming that all songs in an album have the artwork on file, only the first is needed to get it
                //If all songs do not have the cover, it can be added to the whole album later on
                Song song = album.getSong(0);
                File file = new File(song.getFilepath());
                AudioFile f = AudioFileIO.read(file);
                Tag tag = f.getTag();

                //If the album has a cover art, use it, otherwise provide a static image
                Image image;
                if (!tag.hasField(FieldKey.COVER_ART)){
                    image = new Image("images/empty.jpeg", 150, 150, true, true);
                }
                else{
                    byte[] contents = tag.getFirstArtwork().getBinaryData();
                    image = new Image(new BufferedInputStream(new ByteArrayInputStream(contents)), 150, 150, true, true);
                }
                ImageView imageView = new ImageView(image);
                imageView.setCache(true);
                imageView.setCacheHint(CacheHint.SPEED);

                //Create the button by which to place the image
                Button button = new Button(song.getAlbumArtist() + "\n" + song.getAlbum(), imageView);
                button.setContentDisplay(ContentDisplay.TOP);
                button.setGraphic(imageView);
                button.setMaxWidth(150);
                flowPane.getChildren().add(button);
                //This map allows for returning the position of each album, useful for placing dropdown and closing them
                buttonLocation.put(button, locationNumber++);

                //Create listener for button to ensure desired actions upon number of mouse clicks
                //One mouse clicks makes a drop down of songs in that album, and two place the album in its entirety
                //TODO: Change listener does not work with maximizing the window, fix this
                //TODO: Remove hardcoded button width
                //TODO: Tables are uneven by a factor of one when even number of songs
                //TODO: Diversify classes better, less code on each class
                button.setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getClickCount() == 2){
                        //playSong = new PlaySong(song.getFilepath());
                        //playSong.start();
                        playSong.setSong(song.getFilepath());
                        playSong.start();
                    }
                    if (mouseEvent.getClickCount() == 1){
                        if (lastButtonChosen == buttonLocation.get(button) && albumIsOpen){
                            flowPane.getChildren().remove(pane);
                            albumIsOpen = false;
                        }
                        else{
                            albumIsOpen = true;
                            flowPane.getChildren().remove(pane);
                            try {
                                lastButtonChosen = buttonLocation.get(button);
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AlbumInfoPane.fxml"));
                                pane = loader.load();
                                AlbumInfoPane controller = loader.getController();

                                Image tempImage;
                                if (!tag.hasField(FieldKey.COVER_ART)){
                                    controller.setAlbumImage(new Image("images/empty.jpeg", 150, 150, true, true));
                                    tempImage = new Image("images/empty.jpeg", 1, 1, true, true);
                                }
                                else{
                                    controller.setAlbumImage(new Image(new BufferedInputStream(new ByteArrayInputStream(tag.getFirstArtwork().getBinaryData())), 300, 300, true, true));
                                    tempImage = new Image(new BufferedInputStream(new ByteArrayInputStream(tag.getFirstArtwork().getBinaryData())), 1, 1, true, true);
                                }
                                pane.setStyle("-fx-background-color: " + convertRGBToHex(tempImage));
                                controller.setStyle(convertRGBToHex(tempImage));

                                controller.setFlowPane(album.getAlbum());
                                pane.setPrefWidth(flowPane.getWidth());
                                scrollPane.widthProperty().addListener(new ChangeListener<Number>() {
                                    @Override
                                    public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                                        pane.setPrefWidth((Double) number);
                                        if (albumIsOpen){
                                            flowPane.getChildren().remove(pane);
                                            int panePlacement = getPanePlacement(buttonLocation.get(button), 168);
                                            //This prevents attempts at adding the pane at a higher number than the flowPane has
                                            if (panePlacement > flowPane.getChildren().size()){
                                                flowPane.getChildren().add(flowPane.getChildren().size(), pane);
                                            }
                                            else{
                                                flowPane.getChildren().add(panePlacement, pane);
                                            }
                                        }
                                    }
                                });//end ChangeListener

                                int panePlacement = getPanePlacement(buttonLocation.get(button), 168);
                                //This prevents attempts at adding the pane at a higher number than the flowPane has
                                if (panePlacement > flowPane.getChildren().size()){
                                    flowPane.getChildren().add(flowPane.getChildren().size(), pane);
                                }
                                else{
                                    flowPane.getChildren().add(panePlacement, pane);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                });//end setOnMouseClicked() listener

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
        }//end album for loop

        System.out.println("ALL DONE :)");
        //progressBar.setVisible(false);
    }//end setMusicScene() method

    private String convertRGBToHex(Image image){

        Color color = image.getPixelReader().getColor(0,0);

        return String.format("#%02x%02x%02x", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
    }

    private int getPanePlacement(int location, double buttonWidth){
        /*
        * This method determines where to place the dropdown album pane in the graph so that it is under the chosen object
        * By judging the width of the pane, you can tell how many album covers will fit inside the window
        * Using the location number (which shows which album was chosen, you can estimate which row is needed to place the dropdown under
        * e.g. if there are 6 albums in a row, and you choose the 20th album, you place the dropdown after that row which would be 4th row (6*4=24)
        */
        double width = flowPane.getWidth();
        int placement = 6;

        System.out.println("location: " + location);
        System.out.println("button width: " + buttonWidth);

        while (width > (buttonWidth * placement)){
            placement++;
        }
        placement -= 1;
        System.out.println("PLACEMENT IS: " + placement);

        int multiplyer = 1;
        while (location > placement * multiplyer){
            multiplyer++;
        }
        System.out.println("MULTIPLYER IS: " + multiplyer);
        return placement * multiplyer;

    }//end getPanePlacement() method


}//end AddFileDirectorys class
