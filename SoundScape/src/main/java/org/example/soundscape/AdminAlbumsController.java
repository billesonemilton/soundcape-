package org.example.soundscape;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.soundscape.Models.AlbumModel;
import org.example.soundscape.Models.ArtistModel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class AdminAlbumsController implements Initializable {
    @FXML
    private TextField albumNameInput;

    @FXML
    private TextField albumIDInput;

    @FXML
    private TextField albumYearInput;

    @FXML
    private ComboBox<String> artistIDComboBox;

    @FXML
    private TextField artistSearchInput;

    @FXML
    private Label selectedArtistID;

    @FXML
    private TextField albumSearchInput;

    @FXML
    private ImageView albumCoverView;

    @FXML
    private TableView<AlbumModel> albumTable;

    @FXML
    private TableColumn<AlbumModel, String> columnAlbumID;

    @FXML
    private TableColumn<AlbumModel, String> columnArtistID;

    @FXML
    private TableColumn<AlbumModel, String> columnAlbumName;

    @FXML
    private TableColumn<AlbumModel, Integer> columnAlbumYear;

    private void displayAlert(String type, String title, String message) {
        Alert alert;
        if (type.equals("ERROR")) {
            alert = new Alert(Alert.AlertType.ERROR);
        } else if (type.equals("WARNING")) {
            alert = new Alert(Alert.AlertType.WARNING);
        } else {
            alert = new Alert(Alert.AlertType.INFORMATION);
        }
        alert.setContentText(message);
        alert.setHeaderText(title);
        alert.show();
    }

    private byte[] imageFromUser = null;
    private AlbumModel selectedAlbum = null;
    ObservableList<AlbumModel> albums = AlbumModel.getAllFromDB();
    ObservableList<ArtistModel> artists = ArtistModel.getAllFromDB();

    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnAlbumName.setCellValueFactory(new PropertyValueFactory<>("albumName"));
        columnAlbumID.setCellValueFactory(new PropertyValueFactory<>("albumID"));
        columnAlbumYear.setCellValueFactory(new PropertyValueFactory<>("albumYear"));
        columnArtistID.setCellValueFactory(new PropertyValueFactory<>("artistID"));

        albumTable.setItems(albums);

        albumTable.setOnMouseClicked(event -> {
            if (albumTable.getSelectionModel().getSelectedItem() != null) {
                selectedAlbum = albumTable.getSelectionModel().getSelectedItem();
                albumNameInput.setText(selectedAlbum.getAlbumName());
                albumIDInput.setText(selectedAlbum.getAlbumID());
                albumYearInput.setText(String.valueOf(selectedAlbum.getAlbumYear()));
                artistIDComboBox.setValue(selectedAlbum.getArtistID());
                selectedArtistID.setText(selectedAlbum.getArtistID());
                imageFromUser = selectedAlbum.getAlbumCover();
                Image image = new Image(new ByteArrayInputStream(imageFromUser));
                albumCoverView.setImage(image);
            }
        });

        artistIDComboBox.setItems(FXCollections.observableArrayList(artists.stream().map(ArtistModel::getArtistID).toList()));

        artistSearchInput.setOnKeyTyped(keyEvent -> {
            ObservableList<ArtistModel> filteredArtists = ArtistModel.getFilteredFromDB(artistSearchInput.getText());
            artistIDComboBox.setItems(FXCollections.observableArrayList(filteredArtists.stream().map(ArtistModel::getArtistID).toList()));
        });

        albumSearchInput.setOnKeyTyped(keyEvent -> {
            ObservableList<AlbumModel> filteredAlbum = AlbumModel.getFilteredFromDB(albumSearchInput.getText());
            albums.clear();
            clearFields();
            for(AlbumModel album: filteredAlbum){
                albums.add(album);
            }
        });

        //artistIDComboBox.setItems(FXCollections.observableArrayList(artists.stream().map(ArtistModel::getArtistID).toList()));
    }

    public Boolean validate() {
        if (albumNameInput.getText().isEmpty() || albumYearInput.getText().isEmpty() || artistIDComboBox.getValue() == null || imageFromUser == null) {
            displayAlert("ERROR", "Validation Error", "Please make sure all fields are completed");
            return false;
        }
        try {
            Integer.parseInt(albumYearInput.getText());
        } catch (NumberFormatException e) {
            displayAlert("ERROR", "Validation Error", "Album year must be a number");
            return false;
        }
        return true;
    }

    public void clearFields() {
        albumNameInput.setText("");
        albumIDInput.setText("");
        albumYearInput.setText("");
        artistIDComboBox.setValue(null);
        imageFromUser = null;
        albumCoverView.setImage(null);
        selectedAlbum = null;
        selectedArtistID.setText("");
    }

    public  void setInputtedArtistID(ActionEvent event){
        selectedArtistID.setText(artistIDComboBox.getValue());
    }

    public void addButtonHandle(ActionEvent event) {
        if (selectedAlbum == null) {
            if (validate()) {
                AlbumModel newAlbum = new AlbumModel(albumIDInput.getText(), albumNameInput.getText(), Integer.parseInt(albumYearInput.getText()), imageFromUser,artistIDComboBox.getValue());
                newAlbum = newAlbum.saveToDB();
                if (newAlbum.getSuccessStatus()) {
                    displayAlert("SUCCESS", "Success", "Record successfully added");
                    albums.add(newAlbum);
                } else {
                    displayAlert("ERROR", "Insertion Error", "Couldn't add record!");
                }
                clearFields();
            }
        } else {
            if (validate()) {
                int selectedAlbumIndex = albums.indexOf(selectedAlbum);
                String selectedAlbumFormerID = selectedAlbum.getAlbumID();
                selectedAlbum.setArtistID(artistIDComboBox.getValue());
                selectedAlbum.setAlbumName(albumNameInput.getText());
                selectedAlbum.setAlbumID(albumIDInput.getText());
                selectedAlbum.setAlbumYear(Integer.parseInt(albumYearInput.getText()));
                selectedAlbum.setAlbumCover(imageFromUser);
                selectedAlbum = selectedAlbum.updateToDB(selectedAlbumFormerID);
                if (selectedAlbum.getSuccessStatus()) {
                    displayAlert("SUCCESS", "Success", "Record successfully updated");
                    albums.set(selectedAlbumIndex, selectedAlbum);
                } else {
                    displayAlert("ERROR", "Update Error", "Couldn't update record!");
                }
                clearFields();
            }
        }
    }

    public void deleteButtonHandle(ActionEvent event) {
        if (selectedAlbum != null && validate()) {
            int selectedAlbumIndex = albums.indexOf(selectedAlbum);
            selectedAlbum = selectedAlbum.deleteFromDB();
            if (selectedAlbum.getSuccessStatus()) {
                displayAlert("SUCCESS", "Success", "Record successfully deleted");
                albums.remove(selectedAlbumIndex);
            } else {
                displayAlert("ERROR", "Delete Error", "Couldn't delete record!");
            }
            selectedAlbum = null;
            clearFields();
        }
    }

    public void getImageFromUser(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) albumNameInput.getScene().getWindow();
        fileChooser.setTitle("Choose an Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File imageFile = fileChooser.showOpenDialog(stage);
        if (imageFile != null) {
            try {
                imageFromUser = Files.readAllBytes(imageFile.toPath());
                Image image = new Image(new ByteArrayInputStream(imageFromUser));
                albumCoverView.setImage(image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
