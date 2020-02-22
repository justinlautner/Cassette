package mainpackage;

import albuminfopane.AlbumInfoPane;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import music.Album;
import music.Song;
import musicplayer.PlaySong;
import musicplayer.Playlist;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MusicScene {

    private VBox vBox;
    private FlowPane flowPane;
    private ScrollPane scrollPane;
    private ProgressBar progressBar;
    private LinkedList<Song> songLinkedList = new LinkedList<>();
    private LinkedList<Album> albumLinkedList = new LinkedList<>();
    //private LinkedList<Genre> genreLinkedList = new LinkedList<>();
    private ArrayList<String> genres = new ArrayList<>();
    private HashMap<Button, Integer> buttonLocation = new HashMap<>();
    private int locationNumber = 1, lastButtonChosen = 0;
    private Pane pane;
    private boolean albumIsOpen;
    //private PlaySong playSong = new PlaySong();
    //TODO: Program takes 21 seconds to start, loading 10k plus songs. I can do better!
    //TODO: Opening an album has a slight delay, this can be improved

    public MusicScene(VBox vBox, FlowPane flowPane, ProgressBar progressBar, ScrollPane scrollPane){

        this.vBox = vBox;
        this.flowPane = flowPane;
        this.progressBar = progressBar;
        this.scrollPane = scrollPane;

        System.out.println("PROCESS BEGIN");
        try{
            Path pathToDirectories = Paths.get("src/main/resources/saves/directories.txt");
            if (Files.exists(pathToDirectories)){

                Path savedAlbums = Paths.get("src/main/resources/saves/albums.txt");
                Path savedSongs = Paths.get("src/main/resources/saves/songs.txt");

                if (Files.exists(savedAlbums) && Files.exists(savedSongs)){
                    System.out.println("SAVED ALBUMS AND SONGS WERE FOUND");

                    FileInputStream albumsIn = new FileInputStream(savedAlbums.toString());
                    ObjectInputStream albumObjIn = new ObjectInputStream(albumsIn);
                    try{
                        while (true){
                            albumLinkedList.add((Album) albumObjIn.readObject());
                        }
                    } catch (EOFException ignored){

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        albumObjIn.close();
                        albumsIn.close();
                    }

                    FileInputStream songsIn = new FileInputStream(savedSongs.toString());
                    ObjectInputStream songObjIn = new ObjectInputStream(songsIn);
                    try{
                        while (true){
                            songLinkedList.add((Song) songObjIn.readObject());
                        }
                    } catch (EOFException ignored){

                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        songObjIn.close();
                        songsIn.close();
                    }

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void setMusicScene(){
        //Sort songs by album artist, as is my preference
        //TODO: Edit this sort to allow user to change comparators, e.g. album or year

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
                //TODO: Tables are uneven by a factor of one when even number of songs
                //TODO: Diversify classes better, less code on each class
                button.setOnMouseClicked(mouseEvent -> {
                    if (mouseEvent.getClickCount() == 2){
                        Playlist playlist = new Playlist();
                        playlist.addSongs(album.getAlbum());
                        playlist.start();
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
                                scrollPane.widthProperty().addListener((observableValue, number, t1) -> {
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
    }

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

        while (width > (buttonWidth * placement)){
            placement++;
        }
        placement -= 1;

        int multiplyer = 1;
        while (location > placement * multiplyer){
            multiplyer++;
        }

        return placement * multiplyer;

    }//end getPanePlacement() method

}