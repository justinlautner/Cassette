package AddFileDirectory;

import org.jaudiotagger.tag.images.Artwork;

public class Album {

    private String[] titles;
    private String albumArtist;
    private String album;
    private String genre;
    private Artwork artwork;

    Album(String[] titles, String albumArtist, String album, String genre, Artwork artwork){
        this.titles = titles;
        this.albumArtist = albumArtist;
        this.album = album;
        this.genre = genre;
        this.artwork = artwork;
    }

}
