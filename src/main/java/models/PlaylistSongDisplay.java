package models;

public class PlaylistSongDisplay {

    private String title, length, artist, albumArtist, album, year, bitRate, fileType;
    private int track;

    public PlaylistSongDisplay(int track, String title, String artist, String albumArtist, String album, String length, String year,String bitRate, String fileType){

        this.track = track;
        this.title = title;
        this.length = length;
        this.artist = artist;
        this.albumArtist = albumArtist;
        this.album = album;
        this.year = year;
        this.bitRate = bitRate;
        this.fileType = fileType;

    }

    public int getTrack() {
        return track;
    }

    public String getTitle() {
        return title;
    }

    public String getLength() {
        return length;
    }

    public void setLength(String length) {
        this.length = length;
    }

    public void setTrack(int track) {
        this.track = track;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbumArtist() {
        return albumArtist;
    }

    public void setAlbumArtist(String albumArtist) {
        this.albumArtist = albumArtist;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getAlbum(){
        return album;
    }

    public void setAlbum(String album){
        this.album = album;
    }

    public void setBitRate(String bitRate) {
        this.bitRate = bitRate;
    }

    public String getBitRate() {
        return bitRate;
    }
}
