package org.example.soundscape;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.example.soundscape.Models.AlbumModel;
import org.example.soundscape.Models.SongModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SongDisplayController {
    @FXML
    private Label artistName;

    @FXML
    private Label songDuration;

    @FXML
    private Label songName;

    @FXML
    private Label songRelease;

    private SongModel songOut = null;
    public   UserDashboardController userDashboardController;

    public void SetSong(SongModel song) throws IOException {
        songOut = song;
        artistName.setText(song.getArtistName());
        artistName.setOnMouseClicked(e->{
            //userDashboardController.navigate("artistpage",AlbumModel.getByIDFromDB(song.getAlbumID()).getArtistID());
        });
        songName.setText(song.getSongName());
        songRelease.setText(Integer.toString(AlbumModel.getByIDFromDB(song.getAlbumID()).getAlbumYear()));
        Media songAudio = new Media(createTempAudioFile(song.getSongAudio()).toURI().toString());
        MediaPlayer mediaPlayer = new MediaPlayer(songAudio);
        mediaPlayer.setOnReady(()->{
            songDuration.setText(formatSecs(songAudio.getDuration().toSeconds()));
        });
    }

    public SongModel getSong() {
        return songOut;
    }

    public void setGrandParentController(UserDashboardController userDashboardController){
        this.userDashboardController = userDashboardController;
    }

    private File createTempAudioFile(byte[] audioBytes) throws IOException {
        File tempFile = File.createTempFile("temp_song",".tmp");
        tempFile.deleteOnExit();
        try(FileOutputStream fos = new FileOutputStream(tempFile)){
            fos.write(audioBytes);
        }
        return tempFile;
    }
    private String formatSecs(double seconds){
        long mins = (long)seconds/60;
        long secs = (long)seconds%60;
        return String.format("%02d:%02d",mins,secs);
    }
}
