package addfiledirectory;

import mainpackage.MusicScene;
import music.Album;
import music.Song;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
import java.text.Collator;
import java.util.*;

public class AddFileDirectorys extends Thread {

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
        System.out.println("PROCESS BEGIN");
        try{
            Path pathToDirectories = Paths.get("src/main/resources/saves/directories.txt");
            if (Files.exists(pathToDirectories)){
                List<String> listOfDirectories = Files.readAllLines(pathToDirectories);
                if (!listOfDirectories.contains(chosenDirectory.toString())){
                    System.out.println("DIRECTORY WAS FOUND IN FILE");
                    PrintWriter out = new PrintWriter(pathToDirectories.toString());
                    out.println(pathToDirectories.toString());

                    out.flush();
                    out.close();
                }
            }
            else{
                System.out.println("ELSE HAS BEEN TRIGGERED");
                File directory = new File(pathToDirectories.toString());
                PrintWriter out = new PrintWriter(directory);
                out.println(chosenDirectory.toString());
                out.flush();
                out.close();
            }

            Path savedAlbums = Paths.get("src/main/resources/saves/albums.txt");
            Path savedSongs = Paths.get("src/main/resources/saves/songs.txt");
            if (!Files.exists(savedAlbums) && !Files.exists(savedSongs)){
                List<String> listOfDirectories = Files.readAllLines(pathToDirectories);
                System.out.println("NO ALBUM OR SONG SAVE FILE FOUND");
                for (String string: listOfDirectories){
                    getMusic(new File(string));
                }
                setMusic();

                File albumsFile = new File(savedAlbums.toString());
                File songsFile = new File(savedSongs.toString());
                FileOutputStream songsOutputStream = new FileOutputStream(songsFile);
                FileOutputStream albumsOutputStream = new FileOutputStream(albumsFile);
                ObjectOutputStream albumsOut = new ObjectOutputStream(albumsOutputStream);
                ObjectOutputStream songsOut = new ObjectOutputStream(songsOutputStream);

                songsOut.writeObject(songLinkedList);
                albumsOut.writeObject(albumLinkedList);

                albumsOut.flush();
                songsOut.flush();
                albumsOut.close();
                songsOut.close();
                songsOutputStream.flush();
                songsOutputStream.close();
                albumsOutputStream.flush();
                albumsOutputStream.close();
            }
            else{
                return;
            }

        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        MusicScene musicScene = new MusicScene(vBox, flowPane, progressBar, scrollPane);
        musicScene.setMusicScene();

    }

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

    private void setMusic(){
        songLinkedList.sort((song, t1) -> Collator.getInstance().compare(song.getAlbumArtist(), t1.getAlbumArtist()));

        for (int i = 0; i < songLinkedList.size(); i++){
            String curr = songLinkedList.get(i).getAlbum();
            //System.out.println(songLinkedList.get(i).getTitle());
            //System.out.println("current: " + curr);
            if (i != 0){
                String prev = songLinkedList.get(i - 1).getAlbum();
                //System.out.println("previous: " + prev);
                if (!(curr.equals(prev))) {
                    //Album album = new Album(singleAlbum);
                    singleAlbum.sort(Comparator.comparingInt(Song::getTrack));
                    albumLinkedList.add(new Album(singleAlbum));
                    singleAlbum.clear();
                    System.out.println("is this thing on?");
                }
            }

            singleAlbum.add(songLinkedList.get(i));
        }//end for loop
        albumLinkedList.add(new Album(singleAlbum));
    }

}//end AddFileDirectorys class
