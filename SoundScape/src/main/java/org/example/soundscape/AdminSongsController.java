package org.example.soundscape;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.soundscape.Models.AlbumModel;
import org.example.soundscape.Models.SongModel;

import javax.sound.sampled.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class AdminSongsController implements Initializable {
    @FXML
    private TextField songNameInput;

    @FXML
    private TextField songIDInput;

    @FXML
    private ComboBox<String> albumIDComboBox;

    @FXML
    private TextField albumSearchInput;

    @FXML
    private Label selectedAlbumID;

    @FXML
    private TextField songSearchInput;

    @FXML
    private TableView<SongModel> songTable;

    @FXML
    private TableColumn<SongModel, String> columnSongID;

    @FXML
    private TableColumn<SongModel, String> columnAlbumID;

    @FXML
    private TableColumn<SongModel, String> columnSongName;

    @FXML
    private Pane playerPane;

    private Parent musicPlayer;
    private MusicPlayerController controller;
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

    private byte[] audioFromUser = null;
    private SongModel selectedSong = null;
    ObservableList<SongModel> songs = SongModel.getAllFromDB();
    ObservableList<AlbumModel> albums = AlbumModel.getAllFromDB();

    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnSongName.setCellValueFactory(new PropertyValueFactory<>("songName"));
        columnSongID.setCellValueFactory(new PropertyValueFactory<>("songID"));
        columnAlbumID.setCellValueFactory(new PropertyValueFactory<>("albumID"));

        songTable.setItems(songs);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/soundscape/components/musicplayer.fxml"));
        try {
            musicPlayer = loader.load();
            controller = loader.getController();
            //controller.setSong(FXCollections.observableArrayList(selectedSong,selectedSong));
            playerPane.getChildren().add(musicPlayer);
            musicPlayer.setVisible(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        songTable.sceneProperty().addListener(((observableValue, scene, t1) -> {
            if(t1==null){
                System.out.println("Left page");
                playerPane.getChildren().clear();
            }
        }));

        songTable.setOnMouseClicked(event -> {
            if (songTable.getSelectionModel().getSelectedItem() != null) {
                selectedSong = songTable.getSelectionModel().getSelectedItem();
                songNameInput.setText(selectedSong.getSongName());
                songIDInput.setText(selectedSong.getSongID());
                albumIDComboBox.setValue(selectedSong.getAlbumID());
                selectedAlbumID.setText(selectedSong.getAlbumID());
                audioFromUser = selectedSong.getSongAudio();
                System.out.println(selectedSong.getArtistName());
                try {
                    musicPlayer.setVisible(true);
                    controller.setSong(FXCollections.observableArrayList(selectedSong),true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        albumIDComboBox.setItems(FXCollections.observableArrayList(albums.stream().map(AlbumModel::getAlbumID).toList()));

        albumSearchInput.setOnKeyTyped(keyEvent -> {
            ObservableList<AlbumModel> filteredAlbums = AlbumModel.getFilteredFromDB(albumSearchInput.getText());
            albumIDComboBox.setItems(FXCollections.observableArrayList(filteredAlbums.stream().map(AlbumModel::getAlbumID).toList()));
        });

        songSearchInput.setOnKeyTyped(keyEvent -> {
            ObservableList<SongModel> filteredSongs = SongModel.getFilteredFromDB(songSearchInput.getText());
            songs.clear();
            clearFields();
            for (SongModel song : filteredSongs) {
                songs.add(song);
            }
        });
    }

    public Boolean validate() {
        if (songNameInput.getText().isEmpty() || albumIDComboBox.getValue() == null || audioFromUser == null) {
            displayAlert("ERROR", "Validation Error", "Please make sure all fields are completed");
            return false;
        }
        return true;
    }

    public void clearFields() {
        songNameInput.setText("");
        songIDInput.setText("");
        albumIDComboBox.setValue(null);
        audioFromUser = null;
        selectedSong = null;
        selectedAlbumID.setText("");
    }

    public void setInputtedAlbumID(ActionEvent event) {
        selectedAlbumID.setText(albumIDComboBox.getValue());
    }

    public void addButtonHandle(ActionEvent event) {
        if (selectedSong == null) {
            if (validate()) {
                SongModel newSong = new SongModel(songIDInput.getText(), songNameInput.getText(), audioFromUser, albumIDComboBox.getValue());
                newSong = newSong.saveToDB();
                if (newSong.getSuccessStatus()) {
                    displayAlert("SUCCESS", "Success", "Record successfully added");
                    songs.add(newSong);
                } else {
                    displayAlert("ERROR", "Insertion Error", "Couldn't add record!");
                }
                clearFields();
            }
        } else {
            if (validate()) {
                int selectedSongIndex = songs.indexOf(selectedSong);
                String selectedSongFormerID = selectedSong.getSongID();
                selectedSong.setAlbumID(albumIDComboBox.getValue());
                selectedSong.setSongName(songNameInput.getText());
                selectedSong.setSongID(songIDInput.getText());
                selectedSong.setSongAudio(audioFromUser);
                selectedSong = selectedSong.updateToDB(selectedSongFormerID);
                if (selectedSong.getSuccessStatus()) {
                    displayAlert("SUCCESS", "Success", "Record successfully updated");
                    songs.set(selectedSongIndex, selectedSong);
                } else {
                    displayAlert("ERROR", "Update Error", "Couldn't update record!");
                }
                clearFields();
            }
        }
    }

    public void deleteButtonHandle(ActionEvent event) {
        if (selectedSong != null && validate()) {
            int selectedSongIndex = songs.indexOf(selectedSong);
            selectedSong = selectedSong.deleteFromDB();
            if (selectedSong.getSuccessStatus()) {
                displayAlert("SUCCESS", "Success", "Record successfully deleted");
                songs.remove(selectedSongIndex);
            } else {
                displayAlert("ERROR", "Delete Error", "Couldn't delete record!");
            }
            selectedSong = null;
            clearFields();
        }
    }

    public void getAudioFromUser(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage) songNameInput.getScene().getWindow();
        fileChooser.setTitle("Choose an Audio File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Audio Files", "*.mp3"));
        File audioFile = fileChooser.showOpenDialog(stage);
        if (audioFile != null) {
            try {
                audioFromUser = Files.readAllBytes(audioFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}