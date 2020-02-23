package musicplayer;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import mainpackage.Controller;
import music.Song;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import playlistscene.PlaylistScene;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Playlist extends Thread {

    private ArrayList<Song> playlist = new ArrayList<>();
    private Controller controller;
    private PlaylistScene playlistScene;
    private Stage primaryStage;

    //All passed variables are from Application thread, this way they can be updated based on the song playing in this one
    public Playlist(Controller controller, PlaylistScene playlistScene, Stage primaryStage){
        this.controller = controller;
        this.playlistScene = playlistScene;
        this.primaryStage = primaryStage;
    }

    public void addSongs(ArrayList<Song> playlist){
        this.playlist.addAll(playlist);
    }

    @Override
    public void run() {

        //Iterate through the playlist, playing each song and updating playlist scene and now playing along the way
        for (int i = 0; i < playlist.size(); i++){

            try {
                File file = new File(playlist.get(i).getFilepath());
                AudioFile f;
                f = AudioFileIO.read(file);
                Tag tag = f.getTag();
                byte[] contents = tag.getFirstArtwork().getBinaryData();
                playlistScene.addSongs(playlist);
                playlistScene.setAlbumImage(contents);
                controller.setNowPlaying(playlist.get(i).getTitle(), playlist.get(i).getArtist(), playlist.get(i).getAlbum(), new Image(new BufferedInputStream(new ByteArrayInputStream(contents)), 150, 150, true, true));
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

            //Change stage title
            int finalI = i;
            Platform.runLater(() -> {
                primaryStage.setTitle(playlist.get(finalI).getArtist() + " - " + playlist.get(finalI).getTitle());
            });

            PlaySong playSong = new PlaySong();
            playSong.setSong(playlist.get(i).getFilepath());
            playSong.start();

            while (playSong.isAlive()){

            }
        }

    }

}
