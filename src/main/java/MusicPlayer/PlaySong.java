package MusicPlayer;

import org.kc7bfi.jflac.apps.Player;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class PlaySong extends Thread {

    private String song;

    PlaySong(String song){
        this.song = song;
    }

    public void run(){

        Player player = new Player();
        try {
            player.decode(song);
        } catch (IOException | LineUnavailableException e) {
            e.printStackTrace();
        }

    }

}
