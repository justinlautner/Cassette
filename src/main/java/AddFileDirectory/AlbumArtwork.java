package AddFileDirectory;

import javafx.beans.binding.Bindings;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.TilePane;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.images.Artwork;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class AlbumArtwork implements Runnable {

    private File chosenDirectory;
    private TilePane tilePane;
    private ArrayList<Artwork> albumArtwork = new ArrayList<>();

    public AlbumArtwork(File file, TilePane tilePane){
        this.chosenDirectory = file;
        this.tilePane = tilePane;
    }

    @Override
    public void run() {

        try {
            getAlbumArtwork(chosenDirectory);
            setAlbumArtwork();
        } catch (TagException e) {
            e.printStackTrace();
        } catch (ReadOnlyFileException e) {
            e.printStackTrace();
        } catch (CannotReadException e) {
            e.printStackTrace();
        } catch (InvalidAudioFrameException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void getAlbumArtwork(File file) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {

        File[] listOfFiles = file.listFiles();

        if (listOfFiles != null){
            for (File fil : listOfFiles)
            {
                if (fil.isDirectory())
                {
                    getAlbumArtwork(fil);
                }
                else if (fil.toString().endsWith(".flac") || fil.toString().endsWith(".mp3") || fil.toString().endsWith(".m4a")  || fil.toString().endsWith(".m4p") ||fil.toString().endsWith(".mp4") || fil.toString().endsWith(".ogg")  || fil.toString().endsWith(".wav")  || fil.toString().endsWith(".aif")  || fil.toString().endsWith(".dsf")  || fil.toString().endsWith(".wma"))
                {
                    try{
                        AudioFile f = AudioFileIO.read(fil);
                        Tag tag = f.getTag();
                        //String s = tag.getFirst(FieldKey.COVER_ART);
                        //TagField binaryField = tag.getFirstField(FieldKey.COVER_ART);
                        Artwork artwork = tag.getFirstArtwork();
                        if (!albumArtwork.contains(artwork)){
                            albumArtwork.add(artwork);
                        }
                    } catch (CannotReadException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (TagException e) {
                        e.printStackTrace();
                    } catch (ReadOnlyFileException e) {
                        e.printStackTrace();
                    } catch (InvalidAudioFrameException e) {
                        e.printStackTrace();
                    }


                }
            }
        }

    }

    public void setAlbumArtwork() throws IOException {

        for (Artwork artwork: albumArtwork){
            ToggleButton toggleButton = new ToggleButton();
            Image image = new Image(new ByteArrayInputStream(artwork.getBinaryData()));
            ImageView imageView = new ImageView();
            toggleButton.setGraphic(imageView);
            imageView.setFitHeight(150);
            imageView.setFitWidth(150);
            imageView.imageProperty().bind(Bindings.when(toggleButton.selectedProperty()).then(image).otherwise(image));
            tilePane.getChildren().add(toggleButton);
        }



    }
}
