package musicplayer;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import mainpackage.Controller;
import music.Song;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import playlistscene.PlaylistScene;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.media.MediaRef;
import uk.co.caprica.vlcj.medialist.MediaListRef;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayer;
import uk.co.caprica.vlcj.player.list.MediaListPlayerEventListener;
import uk.co.caprica.vlcj.player.list.PlaybackMode;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;

public class Playlist {

    private ArrayList<Song> playlist = new ArrayList<>();
    private Controller controller;
    private PlaylistScene playlistScene;
    private Stage primaryStage;
    private Slider volumeSlider;
    private Button playButton;
    private MediaPlayer mediaPlayer;
    private MediaListRef mediaList;
    private MediaListPlayer mediaListPlayer;
    private MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
    private int playlistCounter = -1;

    //All passed variables are from Application thread, this way they can be updated based on the song playing in this one
    public Playlist(Controller controller, PlaylistScene playlistScene, Stage primaryStage, Slider volumeSlider, Button playButton){
        this.controller = controller;
        this.playlistScene = playlistScene;
        this.primaryStage = primaryStage;
        this.volumeSlider = volumeSlider;
        this.playButton = playButton;

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                saveSettings();
            }
        });

    }

    private void loadPlaylist(){
        File file = new File("src/main/resources/saves/settingsCache/playlist.txt");
        try {

            if (Files.exists(file.toPath())){
                FileInputStream fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                ArrayList<Song> loadedPlaylist = (ArrayList<Song>) objectInputStream.readObject();
                System.out.println("LOADED PLAYLIST: ");
                for (Song s: loadedPlaylist){
                    System.out.print(s.getFilepath());
                }
                addSongs(loadedPlaylist);
                fileInputStream.close();
                objectInputStream.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addSongs(ArrayList<Song> playlist){
        /*Instantiate playlist and size, to use counter as a pointer variable to determine what song to play when
         *using control functions, as vlcj does not keep an observable list songs
         */
        this.playlist.addAll(playlist);
        String[] options = new String[]{};
        for (Song song: playlist){
            mediaListPlayer.list().media().add(song.getFilepath(), options);
        }
        playlistScene.addSongs(playlist);
    }

    private void createMediaPlayer(){
        mediaPlayer = mediaPlayerFactory.mediaPlayers().newEmbeddedMediaPlayer();
        mediaListPlayer = mediaPlayerFactory.mediaPlayers().newMediaListPlayer();
        mediaListPlayer.mediaPlayer().setMediaPlayer(mediaPlayer);
        mediaList = mediaPlayerFactory.media().newMediaListRef();
        mediaListPlayer.list().setMediaList(mediaList);
        mediaListPlayer.controls().setMode(PlaybackMode.DEFAULT);
        mediaListPlayerListener();
    }

    private void mediaListPlayerListener(){
        mediaListPlayer.events().addMediaListPlayerEventListener(new MediaListPlayerEventListener() {
            @Override
            public void mediaListPlayerFinished(MediaListPlayer mediaListPlayer) {

            }

            @Override
            public void nextItem(MediaListPlayer mediaListPlayer, MediaRef mediaRef) {
                playlistCounter++;
                //Upon reaching end of playlist, start back at the beginning
                //TODO: restarting playlist requires two clicks, should require one
                if (playlistCounter == playlist.size()){
                    System.out.println("END OF THE ROAD PAL");
                    System.out.println(playlistCounter);
                    playlistCounter = 0;
                }
                updateNowPlayingInfo();
            }

            @Override
            public void stopped(MediaListPlayer mediaListPlayer) {

            }
        });
    }

    private void updateNowPlayingInfo(){
        //Update now playing contents and playlist background
        try {
            File file = new File(playlist.get(playlistCounter).getFilepath());
            AudioFile f;
            f = AudioFileIO.read(file);
            Tag tag = f.getTag();
            byte[] contents;
            if (tag.hasField(FieldKey.COVER_ART)){
                contents = tag.getFirstArtwork().getBinaryData();
                playlistScene.setAlbumImage(contents);
                controller.setNowPlaying(playlist.get(playlistCounter).getTitle(), playlist.get(playlistCounter).getArtist(), playlist.get(playlistCounter).getAlbum(), new Image(new BufferedInputStream(new ByteArrayInputStream(contents)), 150, 150, true, true));
            }
            else {
                controller.setNowPlaying(playlist.get(playlistCounter).getTitle(), playlist.get(playlistCounter).getArtist(), playlist.get(playlistCounter).getAlbum(),new Image("/images/empty.jpeg"));
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

        //Change stage title to reflect now playing song
        Platform.runLater(() -> {
            primaryStage.setTitle(playlist.get(playlistCounter).getArtist() + " - " + playlist.get(playlistCounter).getTitle());
        });
    }

    private void emptyNowPlaying(){
        //empty background and now playing to reflect stoppage
        playlistScene.setAlbumImage(null);
        controller.setNowPlaying("", "", "", null);

        //Change stage title to reflect now playing song
        Platform.runLater(() -> {
            primaryStage.setTitle("Cassette");
        });
    }

    private void setVolumeSlider(){
        //Allow slider to control volume level of music
        //TODO: vlcj saves the volume value upon close, the slider should too (save value upon exit, cannot retrieve at start)
        //volumeSlider.valueProperty().set(100);
        //mediaPlayer.audio().setVolume(100);
        File file = new File("src/main/resources/saves/settingsCache/volume.txt");
        try {

            if (Files.exists(file.toPath())){
                FileInputStream fileInputStream = new FileInputStream(file);
                int vol = fileInputStream.read();
                volumeSlider.valueProperty().set(vol);
            }
            else{
                volumeSlider.valueProperty().set(100);
                mediaPlayer.audio().setVolume(100);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        volumeSlider.valueProperty().addListener((observableValue, number, t1) -> mediaPlayer.audio().setVolume(t1.intValue()));
    }

    public void initializePlaylist() {
        createMediaPlayer();

        setVolumeSlider();

        loadPlaylist();

        //Try to implement this later
        /*primaryStage.getScene().setOnKeyTyped(keyEvent -> {
            switch (keyEvent.getCode()){
                case PLAY: pauseMusic(); break;
                case STOP: stopMusic(); break;
                case TRACK_NEXT: nextTrack(); break;
                case TRACK_PREV: previousTrack(); break;
            }
        });*/

    }

    public void pauseMusic(){
        if (playlist.isEmpty()){
            return;
        }
        if (mediaListPlayer.status().isPlaying()){
            mediaListPlayer.controls().pause();
            playButton.setGraphic(new ImageView(new Image("images/playButton.png", 50, 58, true, true)));
        }
        else{
            playButton.setGraphic(new ImageView(new Image("images/pauseButton.png", 50, 58, true, true)));
            mediaListPlayer.controls().play();
        }
    }

    public void stopMusic(){
        //Stopping the music resets the queue to the start of the playlist
        playButton.setGraphic(new ImageView(new Image("images/playButton.png", 50, 58, true, true)));
        playlistCounter = -1;
        mediaListPlayer.controls().stop();
        emptyNowPlaying();
    }

    public void nextTrack(){
        //No counter here or now playing updates, as the listener already does this
        mediaListPlayer.controls().playNext();
    }

    public void previousTrack(){
        //Listener still adds one for playing the previous track, so subtract by two to compensate
        playlistCounter = playlistCounter - 2;
        mediaListPlayer.controls().playPrevious();
        updateNowPlayingInfo();
    }

    public void playPlaylistSelection(Song song){
        playButton.setGraphic(new ImageView(new Image("images/pauseButton.png", 50, 58, true, true)));
        playlistCounter = playlist.indexOf(song) - 1;
        mediaListPlayer.controls().play(playlist.indexOf(song));
        updateNowPlayingInfo();
    }

    public void removePlaylistSelection(ArrayList<Song> selected){
        for (Song s: selected){
            mediaListPlayer.list().media().remove(playlist.indexOf(s));
            //playlistCounter = playlist.indexOf(s) - 1;
            playlist.remove(s);
        }
        //updateNowPlayingInfo();
    }

    public void clearPlaylist(){
        playlist.clear();
        mediaListPlayer.list().media().clear();
        emptyNowPlaying();
    }

    private void saveSettings(){
        try {

            //Save volume setting
            File file = new File("src/main/resources/saves/settingsCache/volume.txt");
            Files.deleteIfExists(file.toPath());
            FileOutputStream fileOutputStream= new FileOutputStream(file, false);
            fileOutputStream.write((int) volumeSlider.getValue());

            //Save playlist
            file = new File("src/main/resources/saves/settingsCache/playlist.txt");
            fileOutputStream = new FileOutputStream(file, false);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

            objectOutputStream.writeObject(playlistScene.getCurrentPlaylist());
            System.out.println("SAVED PLAYLIST: ");
            for (Song s: playlistScene.getCurrentPlaylist()){
                System.out.print(s.getFilepath());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
