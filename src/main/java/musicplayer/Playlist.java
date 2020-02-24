package musicplayer;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import mainpackage.Controller;
import music.Song;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
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
            byte[] contents = tag.getFirstArtwork().getBinaryData();
            playlistScene.setAlbumImage(contents);
            controller.setNowPlaying(playlist.get(playlistCounter).getTitle(), playlist.get(playlistCounter).getArtist(), playlist.get(playlistCounter).getAlbum(), new Image(new BufferedInputStream(new ByteArrayInputStream(contents)), 150, 150, true, true));
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

    private void setVolumeSlider(){
        //Allow slider to control volume level of music
        volumeSlider.valueProperty().addListener((observableValue, number, t1) -> mediaPlayer.audio().setVolume(t1.intValue()));
    }

    public void playMusic(){
        playButton.setGraphic(new ImageView(new Image("images/pauseButton.png", 50, 58, true, true)));
        mediaListPlayer.controls().play();
        updateNowPlayingInfo();
    }

    public void initializePlaylist() {
        createMediaPlayer();

        setVolumeSlider();

    }

    public void pauseMusic(){
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

}
