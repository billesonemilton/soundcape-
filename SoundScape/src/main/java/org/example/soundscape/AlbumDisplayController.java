package org.example.soundscape;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.example.soundscape.Models.AlbumModel;

import java.io.ByteArrayInputStream;

public class AlbumDisplayController {
    @FXML
    private ImageView albumCover;

    @FXML
    private Label albumName;

    @FXML
    private Label releaseDate;

    private AlbumModel album = null;

    public void setAlbum(AlbumModel albumFromParent){
        album = albumFromParent;
        albumName.setText(album.getAlbumName());
        releaseDate.setText(Integer.toString(album.getAlbumYear()));
        albumCover.setImage(new Image(new ByteArrayInputStream(album.getAlbumCover())));
    }
    public AlbumModel getAlbum() {
        return album;
    }
}
