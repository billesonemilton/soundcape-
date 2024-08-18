package org.example.soundscape;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.example.soundscape.Models.UserModel;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class UserDashboardController implements Initializable {

    public static UserModel user;
    public MusicPlayerController musicPlayerController;
    private Parent musicPlayer;
    private Boolean isMusicPlayerVisible=false;
    public Boolean getMusicPlayerVisible() {
        return isMusicPlayerVisible;
    }

    public void setMusicPlayerVisible(Boolean musicPlayerVisible) {
        isMusicPlayerVisible = musicPlayerVisible;
    }

    @FXML
    private VBox bottomView;

    @FXML
    private ScrollPane centerView;

    @FXML
    private ScrollPane searchView;

    @FXML
    private Label userInitials;

    @FXML
    private Label username;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //loadPlayer();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/soundscape/user pages/searchpage.fxml"));
        try {
            Parent node  = loader.load();
            UserSearchController searchController = loader.getController();
            searchController.setParentController(this);
            searchView.setContent(node);
            centerView.setVisible(false);
            searchView.setVisible(true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void setPlayerVisible(Boolean visible){
        if (true) {
            if(visible&&!isMusicPlayerVisible){
                loadPlayer();
            }else {
                //musicPlayer.setVisible(false);
            }
        }
    }
    public void loadPlayer(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/soundscape/components/musicplayer.fxml"));
        try {
            musicPlayer = loader.load();
            musicPlayerController = loader.getController();
            //setPlayerVisible(false);
            bottomView.getChildren().add(musicPlayer);
            //musicPlayerController.setSong(FXCollections.observableArrayList(),true);
            setMusicPlayerVisible(true);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void navigate(String page,String param){
        switch (page){
            case "albumpage":
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/soundscape/user pages/albumpage.fxml"));
                try {
                    Parent node = loader.load();
                    UserAlbumPageController userAlbumPageController = loader.getController();
                    userAlbumPageController.setParentController(this);
                    userAlbumPageController.setAlbum(param);
                    centerView.setContent(node);
                    centerView.setVisible(true);
                    searchView.setVisible(false);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "artistpage":
                loader = new FXMLLoader(getClass().getResource("/org/example/soundscape/user pages/artistpage.fxml"));
                try {
                    Parent node  = loader.load();
                    UserArtistPageController userArtistPageController = loader.getController();
                    userArtistPageController.setParentController(this);
                    userArtistPageController.setArtist(param);
                    centerView.setContent(node);
                    centerView.setVisible(true);
                    searchView.setVisible(false);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case "searchpage":
                centerView.setVisible(false);
                searchView.setVisible(true);
                break;
            case "likespage":
                loader = new FXMLLoader(getClass().getResource("/org/example/soundscape/user pages/likespage.fxml"));
                try {
                    Parent node  = loader.load();
                    UserLikedSongsController userLikedSongsController = loader.getController();
                    userLikedSongsController.setParentController(this);
                    centerView.setContent(node);
                    centerView.setVisible(true);
                    searchView.setVisible(false);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            default:
                return;
        }
    }
    public void navigateToSearch(ActionEvent event){
        navigate("searchpage",null);
    }
    public void navigateToLikes(ActionEvent event){
        navigate("likespage",null);
    }
    public void setUser(UserModel userFromParent){
        user = userFromParent;
        username.setText(user.getUserName());
        userInitials.setText(user.getUserName().substring(0,1));
        userInitials.setStyle("-fx-background-color:"+randomColor()+" ;-fx-background-radius:125; ");
    }
    public String randomColor(){
        Random color = new Random();
        int r = color.nextInt(256);
        int g = color.nextInt(256);
        int b = color.nextInt(256);
        return String.format("#%02x%02x%02x",r,g,b);
    }
}

