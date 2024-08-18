package org.example.soundscape;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.soundscape.Models.AlbumModel;
import org.example.soundscape.Models.ArtistModel;
import org.example.soundscape.Models.SongModel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserAlbumPageController implements Initializable {
    @FXML
    private Label albumName;

    @FXML
    private ImageView albumCover;

    @FXML
    private Label artistName;

    @FXML
    private Label releaseDate;

    @FXML
    private Button playAll;

    @FXML
    private VBox songsBox;
    public UserDashboardController userDashboardController;
    ObservableList<SongModel> songs = null;
    AlbumModel album = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playAll.setOnAction(e->{
            try {
                if (songs.size()>0) {
                    userDashboardController.setPlayerVisible(true);
                    userDashboardController.musicPlayerController.setSong(songs,false);
                }
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    public void setParentController(UserDashboardController userDashboardController){
        this.userDashboardController = userDashboardController;
    }
    public void setAlbum(String albumID){
        album = AlbumModel.getByIDFromDB(albumID);
        songs = AlbumModel.getSongsFromDB(albumID);
        if(album != null){
            Image albumImage = new Image(new ByteArrayInputStream(album.getAlbumCover()));
            albumCover.setImage(albumImage);
            albumName.setText(album.getAlbumName());
            artistName.setText(ArtistModel.getArtistByID(album.getArtistID()).getArtistName());
            releaseDate.setText(Integer.toString(album.getAlbumYear()));
            songsBox.getChildren().clear();
            if(songs.size()<=0||songs==null){
                songsBox.getChildren().clear();
                songsBox.getChildren().add(new Label("No songs"));
            }
            for(SongModel song: songs){
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/soundscape/components/songDisplay.fxml"));
                try {
                    Parent node = loader.load();
                    SongDisplayController songDisplayController = loader.getController();
                    songDisplayController.setGrandParentController(this.userDashboardController);
                    songDisplayController.SetSong(song);
                    node.setOnMouseClicked(e->{
                        try {
                            userDashboardController.setPlayerVisible(true);
                            userDashboardController.musicPlayerController.setSong(FXCollections.observableArrayList(songDisplayController.getSong()),false);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    songsBox.getChildren().add(node);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
    public ObservableList<SongModel> getAlbum(){
        return songs;
    }
}
