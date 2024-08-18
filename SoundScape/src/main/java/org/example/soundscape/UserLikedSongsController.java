package org.example.soundscape;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.example.soundscape.Models.SongModel;
import org.example.soundscape.Models.UserModel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserLikedSongsController implements Initializable {
    @FXML
    private ImageView likeIcon;

    @FXML
    private Button playAll;

    @FXML
    private VBox songsBox;

    @FXML
    private Label username;
    public UserDashboardController userDashboardController;
    public UserModel user = UserDashboardController.user;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        username.setText(user.getUserName());
        ObservableList<SongModel> songs = user.getLikedSongs();
        if(songs!=null && songs.size()>0){
            playAll.setOnAction(e->{
                try {
                    userDashboardController.setPlayerVisible(true);
                    userDashboardController.musicPlayerController.setSong(songs,false);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            songsBox.getChildren().clear();
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
        }else {
            songsBox.getChildren().clear();
            songsBox.getChildren().add(new Label("Nothing to show here"));
        }
    }
    public void setParentController(UserDashboardController userDashboardController){
        this.userDashboardController = userDashboardController;
    }
}
