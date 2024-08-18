package org.example.soundscape;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.util.Duration;
import org.example.soundscape.Models.SongModel;
import org.example.soundscape.Models.UserModel;

import java.awt.event.ActionEvent;
import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MusicPlayerController implements Initializable {
    private UserModel user = UserDashboardController.user;
    private final SimpleIntegerProperty songPos= new SimpleIntegerProperty(0);
    private  int getSongPos() {
        return this.songPos.get();
    }
    public void setSongPos(int songPos){
        this.songPos.set(songPos);
    }
    @FXML
    private FontAwesomeIcon heartIcon,pauseResumeIcon,loopIcon;
    @FXML
    private ImageView albumCover;

    @FXML
    private Label artistName;

    @FXML
    private Label startDuration;

    @FXML
    private Label endDuration;

    @FXML
    private MediaView songAudioView;

    @FXML
    private Slider songProgress;

    @FXML
    private Label songTitle;
    @FXML
    private Button loopSong;
    @FXML
    private Button likeSong;

    @FXML
    private Button nextSong;

    @FXML
    private Button pauseResume;

    @FXML
    private Button previousSong;
    ObservableList<SongModel> playlist;
    MediaPlayer songPlayer;
    Media songAudio;
    Boolean isPlaying = false;
    Boolean isLooping = false;


    public void setSong(ObservableList<SongModel> songlist,Boolean isAdmin) throws IOException {

        initInterface();

        if(isAdmin)likeSong.setVisible(false);
        loopSong.setOnAction(e->{
            // add logic to change classes to align to respective styles
            if(isLooping){
                isLooping = false;
                //loopSong.setText("Loop");
                //loopIcon.setGlyphName("REPEAT");
                loopIcon.setId("");
            }else {
                isLooping = true;
                //loopSong.setText("!Loop");
                //loopIcon.setGlyphName("BAN");
                loopIcon.setId("loop-icon");
            }
        });

        pauseResume.setOnAction(e->{
            pauseResumeSong();
        });
        nextSong.setOnAction(e->{
            next();
        });
        previousSong.setOnAction(e->{
            previous();
        });
        if (songlist!=null) {
            if (!songlist.isEmpty()) {
                playlist = songlist;
                songPlayer = initAudio(playlist.get(songPos.get()));
                songPlayer.setAutoPlay(true);
                isPlaying = true;
            }
        }
    }

    private File createTempAudioFile(byte[] audioBytes) throws IOException {
        File tempFile = File.createTempFile("temp_song",".tmp");
        tempFile.deleteOnExit();
        try(FileOutputStream fos = new FileOutputStream(tempFile)){
            fos.write(audioBytes);
        }
        return tempFile;
    }

    private MediaPlayer initAudio(SongModel song) throws IOException {
        if (user!=null) {
            if(user.checkLike(song.getSongID())){
                //likeSong.setText("unlike");
                heartIcon.setGlyphName("HEART");
                likeSong.setOnMouseClicked(e->{
                    user.removeLike(song.getSongID());
                    //likeSong.setText("like");
                    heartIcon.setGlyphName("HEART_ALT");
                });
            }else {
                //likeSong.setText("like");
                heartIcon.setGlyphName("HEART_ALT");
                likeSong.setOnMouseClicked(e->{
                    user.addLike(song.getSongID());
                    //likeSong.setText("unlike");
                    heartIcon.setGlyphName("HEART");
                });
            }
        }
        if(songPlayer!=null){
            songPlayer.stop();
            //songPlayer.dispose();
        }
        songTitle.setText(song.getSongName());
        artistName.setText(song.getArtistName());
        albumCover.setImage(new Image(new ByteArrayInputStream(song.getAlbumCover())));
        songAudio = new Media(createTempAudioFile(song.getSongAudio()).toURI().toString());
        MediaPlayer songplaying =  new MediaPlayer(songAudio);

        songplaying.currentTimeProperty().addListener(((observableValue, duration, t1) -> {
            if(!songProgress.isValueChanging())songProgress.setValue(t1.toSeconds());
            startDuration.setText(formatSecs(t1.toSeconds()));
            //songplaying.setOnReady(()->{songProgress.setMax(t1.toMillis());});
        }));
        songplaying.setOnReady(()->{
            //System.out.println(songAudio.getDuration());
            startDuration.setText("00:00");
            endDuration.setText(formatSecs(songAudio.getDuration().toSeconds()));
            songProgress.setMax(songAudio.getDuration().toSeconds());
        });
        songProgress.valueProperty().addListener(((observableValue1, number, t11) -> {
            if(songProgress.isValueChanging()){
                songplaying.seek(Duration.seconds(t11.doubleValue()));

            }
        }));
        songplaying.setOnEndOfMedia(()->{
            if(!isLooping){
                next();
            }else {
                songplaying.seek(Duration.seconds(0));
            }
        });
        return songplaying;
    }

    private void pauseResumeSong() {
        //add logic to update icons
        if(isPlaying){
            songPlayer.pause();
            isPlaying = false;
            //pauseResume.setText("Resume");
            pauseResumeIcon.setGlyphName("PLAY");
        }else {
            songPlayer.play();
            isPlaying = true;
            //pauseResume.setText("Pause");
            pauseResumeIcon.setGlyphName("PAUSE");
        }
    }
    private String formatSecs(double seconds){
        long mins = (long)seconds/60;
        long secs = (long)seconds%60;
        return String.format("%02d:%02d",mins,secs);
    }

    private void next() {
        if (playlist.size()>1) {
            if(songPos.get()<(playlist.size()-1)){
                songPos.set(songPos.get()+1);
                try {
                    songPlayer = initAudio(playlist.get(songPos.get()));
                    if(isPlaying)songPlayer.setAutoPlay(true);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }else {
                songPos.set(0);
                try {
                    songPlayer = initAudio(playlist.get(songPos.get()));
                    if(isPlaying)songPlayer.setAutoPlay(true);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private void previous() {
        if (playlist.size()>1) {
            if(songPos.get()>0){
                songPos.set(songPos.get()-1);
                try {
                    songPlayer = initAudio(playlist.get(songPos.get()));
                    if(isPlaying) songPlayer.setAutoPlay(true);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }else {
                songPos.set(playlist.size()-1);
                try {
                    songPlayer = initAudio(playlist.get(songPos.get()));
                    if(isPlaying) songPlayer.setAutoPlay(true);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private void initInterface() {
        songPos.set(0);
        //pauseResume.setText("Pause");
        pauseResumeIcon.setGlyphName("PAUSE");
        //loopSong.setText("Loop");
        loopIcon.setId("");
        loopIcon.setGlyphName("REPEAT");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        albumCover.sceneProperty().addListener(((observableValue, scene, t1) -> {
            if(t1==null){
                System.out.println("Left player");
                if(songPlayer!=null){
                    songPlayer.stop();
                    songPlayer.dispose();
                }
            }
        }));
    }
}
