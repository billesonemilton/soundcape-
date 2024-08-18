package org.example.soundscape;

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
import org.example.soundscape.Models.ArtistModel;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class AdminArtistsController implements Initializable {
    @FXML
    private TextField artistNameInput;

    @FXML
    private TextField artistIDInput;

    @FXML
    private ImageView artistPictureView;

    @FXML
    private TableView<ArtistModel> artistTable;

    @FXML
    private TableColumn<ArtistModel, String> columnArtistID;

    @FXML
    private TableColumn<ArtistModel, String> columnartistName;
    @FXML
    private TextField search;

    private void displayAlert(String type,String title,String message) {
        if(type.equals("ERROR")){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText(message);
            alert.setHeaderText(title);
            alert.show();
        }else if(type.equals("WARNING")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText(message);
            alert.setHeaderText(title);
            alert.show();
        } else if(type.equals("SUCCESS")){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(message);
            alert.setHeaderText(title);
            alert.show();
        }
    }
    private byte[] imageFromUser=null;
    private ArtistModel selectedArtist = null;
    ObservableList<ArtistModel> artists = ArtistModel.getAllFromDB();
    public void initialize(URL url, ResourceBundle resourceBundle) {
        columnartistName.setCellValueFactory(new PropertyValueFactory<>("artistName"));
        columnArtistID.setCellValueFactory(new PropertyValueFactory<>("artistID"));
        artistTable.setItems(artists);


        artistTable.setOnMouseClicked(event->{
            if(artistTable.getSelectionModel().getSelectedItem()!=null){
                selectedArtist = artistTable.getSelectionModel().getSelectedItem();
                artistNameInput.setText(selectedArtist.getArtistName());
                artistIDInput.setText(selectedArtist.getArtistID());
                imageFromUser = selectedArtist.getImageBytes();
                Image image = new Image(new ByteArrayInputStream(imageFromUser));
                artistPictureView.setImage(image);
            }
        });

        search.setOnKeyTyped(keyEvent -> {
            //System.out.println("Key pressed "+search.getText());
            ObservableList<ArtistModel> filteredArtists = ArtistModel.getFilteredFromDB(search.getText());
            artists.clear();
            clearFields();
            for(ArtistModel artist: filteredArtists){
                artists.add(artist);
            }
        });
    }

    public  Boolean validate(){
        if(artistNameInput.getText().isEmpty()||imageFromUser==null){
            displayAlert("ERROR","Validation Error","Please make sure all fields are completed");
            return false;
        }
        return true;
    }

    public void clearFields() {
        artistNameInput.setText("");
        artistIDInput.setText("");
        imageFromUser = null;
        artistPictureView.setImage(null);
        selectedArtist = null;
    }

    public void addButtonHandle(ActionEvent event){
        if(selectedArtist==null){
            if(validate()){
                ArtistModel newArtist = new ArtistModel(artistIDInput.getText(),artistNameInput.getText(),imageFromUser);
                newArtist= newArtist.saveToDB();
                if(newArtist.getSuccessStatus()){
                    displayAlert("SUCCESS","Success","Record successfully added");
                    artists.add(newArtist);
                }else {
                    displayAlert("ERROR","Insertion Error","Couldn't add record!");
                }
                clearFields();
            }
        }else {
            if(validate()){
                int selectedArtistIndex = artists.indexOf(selectedArtist);
                String selectedArtistFormerID = selectedArtist.getArtistID();
                selectedArtist.setArtistName(artistNameInput.getText());
                selectedArtist.setArtistID(artistIDInput.getText());
                selectedArtist.setImageBytes(imageFromUser);
                selectedArtist = selectedArtist.updateToDB(selectedArtistFormerID);
                if(selectedArtist.getSuccessStatus()){
                    displayAlert("SUCCESS","Success","Record successfully updated");
                    artists.set(selectedArtistIndex,selectedArtist);

                }else {
                    displayAlert("ERROR","Update Error","Couldn't update record!");
                }
                clearFields();
            }
        }
    }
    public  void deleteButtonHandle(ActionEvent event){
        if(validate()){
            int selectedArtistIndex = artists.indexOf(selectedArtist);
            selectedArtist = selectedArtist.deleteFromDB();
            if(selectedArtist.getSuccessStatus()){
                displayAlert("SUCCESS","Success","Record successfully deleted");
                artists.remove(selectedArtistIndex);
            }else {
                displayAlert("ERROR","Delete Error","Couldn't delete record!");
            }
            selectedArtist = null;
            clearFields();
        }
    }
    public  void getImageFromUser(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        Stage stage = (Stage)artistNameInput.getScene().getWindow();
        fileChooser.setTitle("Choose an Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));
        File imageFile=fileChooser.showOpenDialog(stage);
        if(imageFile!=null){
            try {
                imageFromUser = Files.readAllBytes(imageFile.toPath());
                Image image = new Image(new ByteArrayInputStream(imageFromUser));
                artistPictureView.setImage(image);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}