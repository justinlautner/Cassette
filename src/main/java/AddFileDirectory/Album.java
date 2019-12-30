package AddFileDirectory;

public class Album {

    /*private String[] titles;
    private String albumArtist;
    private String album;
    private String genre;*/
    private Song[] album;

    Album(Song[] album){
        /*this.titles = titles;
        this.albumArtist = albumArtist;
        this.album = album;
        this.genre = genre;*/
        this.album = album;
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

    public Song[] getAlbum(){
        return album;
    }

}
