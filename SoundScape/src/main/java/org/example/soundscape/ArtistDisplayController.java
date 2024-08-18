package org.example.soundscape;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.soundscape.Models.ArtistModel;
import org.example.soundscape.Models.SongModel;

import java.io.ByteArrayInputStream;

public class ArtistDisplayController {
    @FXML
    private Label artistName;

    @FXML
    private ImageView artistPicture;

    private ArtistModel artist;
    public void setArtist(ArtistModel artistFromParent){
        artist = artistFromParent;
        artistName.setText(artist.getArtistName());
        artistPicture.setImage(new Image(new ByteArrayInputStream(artist.getImageBytes())));
    }
    public ArtistModel getArtist(){
        return artist;
    }
}
