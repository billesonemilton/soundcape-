package org.example.soundscape.Models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.soundscape.SQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ArtistModel {
    private final SimpleStringProperty artistName;
    private final SimpleStringProperty artistID;
    private byte[] imageBytes;
    private Boolean successStatus;

    public ArtistModel(String artistID, String artistName, byte[] imageBytes){
        this.imageBytes = imageBytes;
        this.artistName = new SimpleStringProperty(artistName);
        this.artistID = new SimpleStringProperty(artistID);
    }

    public void setArtistID(String artistID){
        this.artistID.set(artistID);
    }

    public void setArtistName(String artistName){
        this.artistName.set(artistName);
    }
    public  void setImageBytes(byte[] imageBytes){
        this.imageBytes = imageBytes;
    }
    public String getArtistName(){
        return this.artistName.get();
    }
    public byte[] getImageBytes(){
        return imageBytes;
    }
    public String getArtistID(){
        return this.artistID.get();
    }
    public Boolean getSuccessStatus() {
        return successStatus;
    }

    public void setSuccessStatus(Boolean successStatus) {
        this.successStatus = successStatus;
    }

    public ArtistModel saveToDB() {
        String sqlStatement = "INSERT INTO artists(artistID, artistName, artistPicture) VALUES(?, ?, ?)";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            this.setSuccessStatus(false);
            preparedStatement.setString(1, this.getArtistID());
            preparedStatement.setString(2, this.getArtistName());
            preparedStatement.setBytes(3, this.getImageBytes());
            int modiRows = preparedStatement.executeUpdate();
            if (modiRows > 0) this.setSuccessStatus(true);
            return this;
        } catch (SQLException e) {
            return this;
        }
    }

    public static ObservableList<ArtistModel> getAllFromDB() {
        ObservableList<ArtistModel> list = FXCollections.observableArrayList();
        String sqlStatement = "SELECT * FROM artists";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
             ResultSet resSet = preparedStatement.executeQuery()) {
            while (resSet.next()) {
                ArtistModel currentArtist = new ArtistModel(resSet.getString("artistID"), resSet.getString("artistName"), resSet.getBytes("artistPicture"));
                list.add(currentArtist);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static ObservableList<ArtistModel> getFilteredFromDB(String search) {
        ObservableList<ArtistModel> list = FXCollections.observableArrayList();
        String sqlStatement = "SELECT * FROM artists WHERE artistName LIKE ?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, "%" + search + "%");
            ResultSet resSet = preparedStatement.executeQuery();
            while (resSet.next()) {
                ArtistModel currentArtist = new ArtistModel(resSet.getString("artistID"), resSet.getString("artistName"), resSet.getBytes("artistPicture"));
                list.add(currentArtist);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArtistModel updateToDB(String formerArtistID) {
        String sqlStatement = "UPDATE artists SET artistID=?, artistName=?, artistPicture=? WHERE artistID=?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            this.setSuccessStatus(false);
            preparedStatement.setString(1, this.getArtistID());
            preparedStatement.setString(2, this.getArtistName());
            preparedStatement.setBytes(3, this.getImageBytes());
            preparedStatement.setString(4, formerArtistID);
            int modiRows = preparedStatement.executeUpdate();
            if (modiRows > 0) this.setSuccessStatus(true);
            return this;
        } catch (SQLException e) {
            return this;
        }
    }

    public ArtistModel deleteFromDB() {
        String sqlStatement = "DELETE FROM artists WHERE artistID=?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            this.setSuccessStatus(false);
            preparedStatement.setString(1, this.getArtistID());
            int modiRows = preparedStatement.executeUpdate();
            if (modiRows > 0) this.setSuccessStatus(true);
            return this;
        } catch (SQLException e) {
            return this;
        }
    }



    public static ArtistModel getArtistByID(String artistID) {
        String sqlStatement = "SELECT * FROM artists WHERE artistID=?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, artistID);
            ResultSet resSet = preparedStatement.executeQuery();
            if (resSet.next()) {
                ArtistModel currentArtist = new ArtistModel(resSet.getString("artistID"), resSet.getString("artistName"), resSet.getBytes("artistPicture"));
                return currentArtist;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static ObservableList<AlbumModel> getAlbums(String artistID) {
        ObservableList<AlbumModel> list = FXCollections.observableArrayList();
        String sqlStatement = "SELECT * FROM albums WHERE artistID = ?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, artistID);
            ResultSet resSet = preparedStatement.executeQuery();
            while (resSet.next()) {
                AlbumModel currentAlbum = new AlbumModel(
                        resSet.getString("albumID"),
                        resSet.getString("albumName"),
                        resSet.getInt("albumYear"),
                        resSet.getBytes("albumCover"),
                        resSet.getString("artistID")
                );
                list.add(currentAlbum);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}