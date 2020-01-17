package AddFileDirectory;

public class SongDisplay {

    String title, length;
    int track;

    public SongDisplay(int track, String title, String length){

        this.track = track;
        this.title = title;
        this.length = length;

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
}
