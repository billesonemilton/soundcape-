package org.example.soundscape;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import org.example.soundscape.Models.AlbumModel;
import org.example.soundscape.Models.ArtistModel;
import org.example.soundscape.Models.SongModel;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserSearchController implements Initializable {
    public UserDashboardController userDashboardController;
    @FXML
    private FlowPane albumSearchPage;

    @FXML
    private FlowPane artistSearchPage;

    @FXML
    private TextField searchInput;

    @FXML
    private VBox songSearchPage;

    public void showArtistSearchPage(){
        albumSearchPage.setVisible(false);
        songSearchPage.setVisible(false);
        artistSearchPage.setVisible(true);
    }
    public void showAlbumSearchPage(){
        albumSearchPage.setVisible(true);
        songSearchPage.setVisible(false);
        artistSearchPage.setVisible(false);
    }
    public void showSongSearchPage(){
        albumSearchPage.setVisible(false);
        songSearchPage.setVisible(true);
        artistSearchPage.setVisible(false);
    }
    public void setParentController(UserDashboardController userDashboardController){
        this.userDashboardController = userDashboardController;
    }
    public void loadSongs(ObservableList<SongModel> songs){
        songSearchPage.getChildren().clear();
        if(songs.size()<=0){
            Label noresults = new Label("No results");
            songSearchPage.getChildren().add(noresults);
        }
        for(SongModel song: songs){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/soundscape/components/songDisplay.fxml"));
            try {
                Parent node  = loader.load();
                SongDisplayController songDisplayController = loader.getController();
                songDisplayController.setGrandParentController(userDashboardController);
                songDisplayController.SetSong(song);
                node.setOnMouseClicked(e->{
                    try {
                        userDashboardController.setPlayerVisible(true);
                        userDashboardController.musicPlayerController.setSong(FXCollections.observableArrayList(songDisplayController.getSong()),false);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                songSearchPage.getChildren().add(node);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void loadAlbums(ObservableList<AlbumModel> albums){
        albumSearchPage.getChildren().clear();
        if(albums.size()<=0){
            Label noresults = new Label("No results");
            albumSearchPage.getChildren().add(noresults);
        }
        for(AlbumModel album: albums){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/soundscape/components/albumDisplay.fxml"));
            try {
                Parent node  = loader.load();
                AlbumDisplayController albumDisplayController = loader.getController();
                albumDisplayController.setAlbum(album);
                node.setOnMouseClicked(e->{
                    userDashboardController.navigate("albumpage",albumDisplayController.getAlbum().getAlbumID());
                });
                albumSearchPage.getChildren().add(node);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void loadArtists(ObservableList<ArtistModel> artists){
        artistSearchPage.getChildren().clear();
        if(artists.size()<=0){
            Label noresults = new Label("No results");
            artistSearchPage.getChildren().add(noresults);
        }
        for(ArtistModel artist: artists){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/soundscape/components/artistDisplay.fxml"));
            try {
                Parent node  = loader.load();
                ArtistDisplayController artistDisplayController = loader.getController();
                artistDisplayController.setArtist(artist);
                node.setOnMouseClicked(e->{
                    userDashboardController.navigate("artistpage",artistDisplayController.getArtist().getArtistID());
                });
                artistSearchPage.getChildren().add(node);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    public void loadPages(){
        loadAlbums(albums);
        loadSongs(songs);
        loadArtists(artists);
    }
    ObservableList<SongModel> songs = SongModel.getFilteredFromDB("");
    ObservableList<AlbumModel> albums = AlbumModel.getFilteredFromDB("");
    ObservableList<ArtistModel> artists = ArtistModel.getFilteredFromDB("");
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        loadPages();
        songSearchPage.setVisible(true);
        searchInput.setOnKeyTyped(e->{
            songs = SongModel.getFilteredFromDB(searchInput.getText());
            albums = AlbumModel.getFilteredFromDB(searchInput.getText());
            artists = ArtistModel.getFilteredFromDB(searchInput.getText());
            loadPages();
        });
    }
}


