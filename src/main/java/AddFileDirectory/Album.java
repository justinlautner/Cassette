package AddFileDirectory;

import java.util.ArrayList;

public class Album {

    /*private String[] titles;
    private String albumArtist;
    private String album;
    private String genre;*/
    private ArrayList<Song> album;
    private int numberOfAlbums = 0;

    Album(ArrayList<Song> album){
        /*this.titles = titles;
        this.albumArtist = albumArtist;
        this.album = album;
        this.genre = genre;*/
        this.album = new ArrayList<>(album);
        this.numberOfAlbums++;
    }

    /*public String[] getTitles(){
        return titles;
    }
    public String getAlbumArtist(){
        return albumArtist;
    }
    public String getAlbum(){
        return album;
    }
    public String getGenre(){
        return genre;
    }*/

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
