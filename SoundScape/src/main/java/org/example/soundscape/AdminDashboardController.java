package org.example.soundscape;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.Pane;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {
    @FXML
    private Pane centerDisplay;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadPage("artists");
    }
    public  void loadPage(String pageName){
        String loadString="";
        switch (pageName){
            case "artists":
                loadString = "/org/example/soundscape/admin pages/artists.fxml";
                break;
            case "albums":
                loadString = "/org/example/soundscape/admin pages/albums.fxml";
                break;
            case "songs":
                loadString = "/org/example/soundscape/admin pages/songs.fxml";
                break;
            default:
                System.out.println("Page not found");
                return;
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource(loadString));
        try {
            Parent root = loader.load();

            centerDisplay.getChildren().clear();
            centerDisplay.getChildren().add(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void LoadArtists(ActionEvent event){
        loadPage("artists");
    }
    public void LoadAlbums(ActionEvent event){
        loadPage("albums");
    }
    public void LoadSongs(ActionEvent event){
        loadPage("songs");
    }

}
