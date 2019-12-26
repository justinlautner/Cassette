package AddFileDirectory;

public class Song {

    private String filepath;
    private String track;
    private String title;
    private String artist;
    private String albumArtist;
    private String album;
    private String genre;
    private String year;
    private String lyrics;
    private String discNum;

    Song(String filepath, String track, String title, String artist, String albumArtist, String album, String year, String lyrics, String genre, String discNum){
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
    }

    public String getFilepath(){
        return filepath;
    }

    public String getTrack(){
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


}
