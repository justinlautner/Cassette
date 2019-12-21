package AddFileDirectory;

import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v24Frames;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Genres implements Runnable {

    private File chosenDirectory;
    private VBox vBox;
    private ArrayList<String> genres = new ArrayList<>();

    public Genres(File file, VBox vbox){
        this.chosenDirectory = file;
        this.vBox = vbox;
    }

    @Override
    public void run() {
        try {
            getGenres(chosenDirectory);
            setGenres();
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

    private void getGenres(File file) throws TagException, ReadOnlyFileException, CannotReadException, InvalidAudioFrameException, IOException {
        File[] listOfFiles = file.listFiles();

        if (listOfFiles != null){
            for (File fil : listOfFiles)
            {
                if (fil.isDirectory())
                {
                    getGenres(fil);
                }
                // || fil.toString().endsWith(".mp3")  || fil.toString().endsWith(".m4a")  || fil.toString().endsWith(".m4p") ||fil.toString().endsWith(".mp4") || fil.toString().endsWith(".ogg")  || fil.toString().endsWith(".wav")  || fil.toString().endsWith(".aif")  || fil.toString().endsWith(".dsf")  || fil.toString().endsWith(".wma"))
                if (fil.toString().endsWith(".flac") || fil.toString().endsWith(".mp3") || fil.toString().endsWith(".m4a")  || fil.toString().endsWith(".m4p") ||fil.toString().endsWith(".mp4") || fil.toString().endsWith(".ogg")  || fil.toString().endsWith(".wav")  || fil.toString().endsWith(".aif")  || fil.toString().endsWith(".dsf")  || fil.toString().endsWith(".wma")){
                    try{
                        AudioFile f = AudioFileIO.read(fil);
                        Tag tag = f.getTag();
                        String s = tag.getFirst(FieldKey.GENRE);
                        System.out.println(genres);
                        if (!genres.contains(s) & !s.equals("")){
                            genres.add(s);
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
                    } catch (KeyNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                /*if(fil.toString().endsWith(".mp3")){
                    MP3File f = (MP3File) AudioFileIO.read(fil);
                    AbstractID3v2Tag tag = f.getID3v2Tag();
                    String s = tag.getFirst(ID3v24Frames.FRAME_ID_GENRE);
                    System.out.println(genres);
                    if (!genres.contains(s)){
                        genres.add(s);
                    }
                }
*/
            }
        }

    }

    private void setGenres(){

        Collections.sort(genres);
        for (String str: genres) {
            javafx.scene.control.ToggleButton toggleButton = new javafx.scene.control.ToggleButton();
            toggleButton.setText(str);
            toggleButton.getStyleClass().add("styleClass.css");
            toggleButton.setAlignment(Pos.CENTER);
            vBox.getChildren().add(toggleButton);
        }

    }

}
