package musicplayer;

import music.Song;

import java.util.ArrayList;

public class Playlist extends Thread {

    private ArrayList<Song> playlist = new ArrayList<>();

    public Playlist(){ }

    public void addSongs(ArrayList<Song> playlist){
        this.playlist.addAll(playlist);
    }

    public void run(){
        for (Song song: playlist){
            PlaySong playSong = new PlaySong();
            playSong.setSong(song.getFilepath());
            playSong.start();
            while (playSong.isAlive()){

            }
        }
    }

}
