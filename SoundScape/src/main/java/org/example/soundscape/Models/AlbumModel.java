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

public class AlbumModel {
    private final SimpleStringProperty albumName;
    private final SimpleStringProperty albumID;
    private final SimpleIntegerProperty albumYear;
    private final SimpleStringProperty artistID;
    private byte[] albumCover;
    private Boolean successStatus;

    public AlbumModel(String albumID, String albumName, int albumYear, byte[] albumCover,String artistID) {
        this.albumCover = albumCover;
        this.albumName = new SimpleStringProperty(albumName);
        this.albumID = new SimpleStringProperty(albumID);
        this.albumYear = new SimpleIntegerProperty(albumYear);
        this.artistID = new SimpleStringProperty(artistID);
    }

    public void setAlbumID(String albumID) {
        this.albumID.set(albumID);
    }

    public void setAlbumName(String albumName) {
        this.albumName.set(albumName);
    }

    public void setAlbumYear(int albumYear) {
        this.albumYear.set(albumYear);
    }

    public void setAlbumCover(byte[] albumCover) {
        this.albumCover = albumCover;
    }

    public  void setArtistID(String artistID){
        this.artistID.set(artistID);
    }

    public String getAlbumName() {
        return this.albumName.get();
    }

    public String getArtistID(){
        return this.artistID.get();
    }
    public int getAlbumYear() {
        return this.albumYear.get();
    }

    public byte[] getAlbumCover() {
        return albumCover;
    }

    public String getAlbumID() {
        return this.albumID.get();
    }

    public Boolean getSuccessStatus() {
        return successStatus;
    }

    public void setSuccessStatus(Boolean successStatus) {
        this.successStatus = successStatus;
    }

    public AlbumModel saveToDB() {
        String sqlStatement = "INSERT INTO albums(albumID, albumName, albumYear, albumCover, artistID) VALUES(?, ?, ?, ?, ?)";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            this.setSuccessStatus(false);
            preparedStatement.setString(1, this.albumID.get());
            preparedStatement.setString(2, this.albumName.get());
            preparedStatement.setInt(3, this.albumYear.get());
            preparedStatement.setBytes(4, this.albumCover);
            preparedStatement.setString(5, this.artistID.get());
            int modiRows = preparedStatement.executeUpdate();
            if (modiRows > 0) this.setSuccessStatus(true);
            return this;
        } catch (SQLException e) {
            return this;
        }
    }

    public static ObservableList<AlbumModel> getAllFromDB() {
        ObservableList<AlbumModel> list = FXCollections.observableArrayList();
        String sqlStatement = "SELECT * FROM albums";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
             ResultSet resSet = preparedStatement.executeQuery()) {
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

    public static ObservableList<AlbumModel> getFilteredFromDB(String search) {
        ObservableList<AlbumModel> list = FXCollections.observableArrayList();
        String sqlStatement = "SELECT * FROM albums WHERE albumName LIKE ?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, "%" + search + "%");
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

    public AlbumModel updateToDB(String formerAlbumID) {
        String sqlStatement = "UPDATE albums SET albumID = ?, albumName = ?, albumYear = ?, albumCover = ?, artistID = ? WHERE albumID = ?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            this.setSuccessStatus(false);
            preparedStatement.setString(1, this.albumID.get());
            preparedStatement.setString(2, this.albumName.get());
            preparedStatement.setInt(3, this.albumYear.get());
            preparedStatement.setBytes(4, this.albumCover);
            preparedStatement.setString(5, this.artistID.get());
            preparedStatement.setString(6, formerAlbumID);
            int modiRows = preparedStatement.executeUpdate();
            if (modiRows > 0) this.setSuccessStatus(true);
            return this;
        } catch (SQLException e) {
            return this;
        }
    }

    public AlbumModel deleteFromDB() {
        String sqlStatement = "DELETE FROM albums WHERE albumID = ?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            this.setSuccessStatus(false);
            preparedStatement.setString(1, this.albumID.get());
            int modiRows = preparedStatement.executeUpdate();
            if (modiRows > 0) this.setSuccessStatus(true);
            return this;
        } catch (SQLException e) {
            return this;
        }
    }


    public static AlbumModel getByIDFromDB(String albumID) {
        String sqlStatement = "SELECT * FROM albums WHERE albumID = ?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, albumID);
            ResultSet resSet = preparedStatement.executeQuery();
            if (resSet.next()) {
                AlbumModel currentAlbum = new AlbumModel(
                        resSet.getString("albumID"),
                        resSet.getString("albumName"),
                        resSet.getInt("albumYear"),
                        resSet.getBytes("albumCover"),
                        resSet.getString("artistID")
                );
                return currentAlbum;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public static ObservableList<SongModel> getSongsFromDB(String albumID) {
        ObservableList<SongModel> list = FXCollections.observableArrayList();
        String sqlStatement = "SELECT * FROM songs WHERE albumID = ?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, albumID);
            ResultSet resSet = preparedStatement.executeQuery();
            while (resSet.next()) {
                SongModel currentSong = new SongModel(
                        resSet.getString("songID"),
                        resSet.getString("songName"),
                        resSet.getBytes("songAudio"),
                        resSet.getString("albumID")
                );
                list.add(currentSong);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}