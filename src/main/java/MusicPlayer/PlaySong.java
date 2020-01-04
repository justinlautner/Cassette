package MusicPlayer;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.kc7bfi.jflac.apps.Player;

import javax.sound.sampled.LineUnavailableException;
import java.io.File;
import java.io.IOException;

public class PlaySong extends Thread {

    private String song;
    private volatile boolean running = true;

    public PlaySong(String song){
        this.song = song;
    }

    public void run(){

        running = true;

        while (running){
            try {
                if (song.endsWith(".flac")){
                    Player player = new Player();
                    player.decode(song);
                }
                else{
                    Media media = new Media(new File(song).toURI().toString());
                    MediaPlayer mediaPlayer = new MediaPlayer(media);
                    mediaPlayer.play();
                }
            } catch (IOException | LineUnavailableException e) {
                e.printStackTrace();
            }
        }

    }

    public void kill(){
        running = false;
    }

}
