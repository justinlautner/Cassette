package music;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {

    private ArrayList<Song> album;
    private int numberOfAlbums = 0;

    public Album(ArrayList<Song> album){
        this.album = new ArrayList<>(album);
        this.numberOfAlbums++;
    }

    public ArrayList<Song> getAlbum(){
        return album;
    }

    public Song getSong(int i){
        return album.get(i);
    }

    public int getNumberOfAlbums() {
        return numberOfAlbums;
    }
}
