package Music;

import java.util.ArrayList;

public class Album {

    private ArrayList<Song> album;
    private int numberOfAlbums = 0;

    Album(ArrayList<Song> album){
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
