package AddFileDirectory;

import org.jaudiotagger.tag.images.Artwork;

public class Song {

    private int track;
    private String title;
    private String artist;
    private String albumArtist;
    private String album;
    private String genre;
    private int year;
    private String lyrics;
    private int discNum;
    private Artwork artwork;

    Song(int track, String title, String artist, String albumArtist, String album, int year, String lyrics, String genre, int discNum, Artwork artwork){
        this.track = track;
        this.title = title;
        this.artist = artist;
        this.albumArtist = albumArtist;
        this.album = album;
        this.genre = genre;
        this.year = year;
        this.lyrics = lyrics;
        this.artwork = artwork;
    }

}
