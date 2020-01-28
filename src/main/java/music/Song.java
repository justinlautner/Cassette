package music;

import java.io.Serializable;

public class Song implements Serializable {

    private String filepath;
    private int track;
    private String title;
    private String artist;
    private String albumArtist;
    private String album;
    private String genre;
    private String year;
    private String lyrics;
    private String discNum;
    private int numberOfSongs = 0;

    public Song(String filepath, int track, String title, String artist, String albumArtist, String album, String year, String lyrics, String genre, String discNum){
        this.filepath = filepath;
        this.track = track;
        this.title = title;
        this.artist = artist;
        this.albumArtist = albumArtist;
        this.album = album;
        this.genre = genre;
        this.discNum = discNum;
        this.year = year;
        this.lyrics = lyrics;
        this.numberOfSongs++;
    }

    public String getFilepath(){
        return filepath;
    }

    public int getTrack(){
        return track;
    }

    public String getTitle(){
        return title;
    }

    public String getArtist(){
        return artist;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public String getAlbum() {
        return album;
    }

    public String getYear() {
        return year;
    }

    public String getLyrics() {
        return lyrics;
    }

    public String getGenre() {
        return genre;
    }

    public String getDiscNum() {
        return discNum;
    }

    public int getNumberOfSongs(){
        return numberOfSongs;
    }
}
