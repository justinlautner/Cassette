package musicplayer;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.kc7bfi.jflac.apps.Player;

import javax.sound.sampled.LineUnavailableException;
import java.io.*;

public class PlaySong extends Thread {

    //TODO: Add ability to pause, play, and seek songs
    private String song;
    Player player;
    private MediaPlayer mediaPlayer;

    public PlaySong(){
    }

    public void setSong(String song) {
        this.song = song;
    }

    public void run(){

        if (song.endsWith(".flac")){
            try {
                System.out.println("YOU ARE HERE");
                /*Controller controller = new Controller();
                controller.setNowPlaying(song);*/
                player = new Player();
                player.decode(song);
                /*FileInputStream is = new FileInputStream(song);
                FLACDecoder decoder = new FLACDecoder(is);
                decoder.decode();
                decoder.*/
                System.out.println("BUT NOW YOU ARE HERE");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (LineUnavailableException e) {
                e.printStackTrace();
            }
        }
        else{
            Media media = new Media(new File(song).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        }

    }

}
