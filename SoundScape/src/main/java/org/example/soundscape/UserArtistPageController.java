package org.example.soundscape;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import org.example.soundscape.Models.AlbumModel;
import org.example.soundscape.Models.ArtistModel;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserArtistPageController implements Initializable {

    private UserDashboardController userDashboardController;

    @FXML
    private FlowPane albumsContainer;

    @FXML
    private Label albumCount;

    @FXML
    private Label artistName;

    @FXML
    private ImageView artistPicture;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void setArtist(String artistID) throws IOException {
        ArtistModel artist = ArtistModel.getArtistByID(artistID);
        if(artist!=null){
            artistName.setText(artist.getArtistName());
            artistPicture.setImage(new Image(new ByteArrayInputStream(artist.getImageBytes())));
            ObservableList<AlbumModel> albums = ArtistModel.getAlbums(artistID);
            if(albums!=null){
                int albumsCount=0; int singlesCount=0;
                //albumCount.setText(Integer.toString(albums.size())+" Albums");
                for(AlbumModel album: albums){
                    if(AlbumModel.getSongsFromDB(album.getAlbumID()).size()>1){
                        albumsCount++;
                    }else if(AlbumModel.getSongsFromDB(album.getAlbumID()).size()==1) {
                        singlesCount++;
                    }
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/soundscape/components/albumDisplay.fxml"));
                    Parent node = loader.load();
                    AlbumDisplayController controller = loader.getController();
                    controller.setAlbum(album);
                    node.setOnMouseClicked(e->{
                        userDashboardController.navigate("albumpage",album.getAlbumID());
                    });
                    albumsContainer.getChildren().add(node);
                }
                albumCount.setText(String.format("%d Albums %d Singles",albumsCount,singlesCount));
            }
        }
    }
    public void setParentController(UserDashboardController userDashboardController){
        this.userDashboardController = userDashboardController;
    }
}
