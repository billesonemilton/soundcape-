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

public class SongModel {
    private final SimpleStringProperty songID;
    private final SimpleStringProperty songName;
    private final SimpleStringProperty albumID;
    private byte[] songAudio;
    private Boolean successStatus;

    public SongModel(String songID, String songName, byte[] songAudio, String albumID) {
        this.songAudio = songAudio;
        this.songName = new SimpleStringProperty(songName);
        this.songID = new SimpleStringProperty(songID);
        this.albumID = new SimpleStringProperty(albumID);
    }

    public void setSongID(String songID) {
        this.songID.set(songID);
    }

    public void setSongName(String songName) {
        this.songName.set(songName);
    }

    public void setSongAudio(byte[] songAudio) {
        this.songAudio = songAudio;
    }

    public void setAlbumID(String albumID) {
        this.albumID.set(albumID);
    }

    public String getSongName() {
        return this.songName.get();
    }

    public String getAlbumID() {
        return this.albumID.get();
    }

    public byte[] getSongAudio() {
        return songAudio;
    }

    public String getSongID() {
        return this.songID.get();
    }

    public Boolean getSuccessStatus() {
        return successStatus;
    }

    public void setSuccessStatus(Boolean successStatus) {
        this.successStatus = successStatus;
    }

    public SongModel saveToDB() {
        String sqlStatement = "INSERT INTO songs(songID, songName, songAudio, albumID) VALUES(?, ?, ?, ?)";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            this.setSuccessStatus(false);
            preparedStatement.setString(1, this.getSongID());
            preparedStatement.setString(2, this.getSongName());
            preparedStatement.setBytes(3, this.getSongAudio());
            preparedStatement.setString(4, this.getAlbumID());
            int modiRows = preparedStatement.executeUpdate();
            if (modiRows > 0) this.setSuccessStatus(true);
            return this;
        } catch (SQLException e) {
            return this;
        }
    }

    public static ObservableList<SongModel> getAllFromDB() {
        ObservableList<SongModel> list = FXCollections.observableArrayList();
        String sqlStatement = "SELECT * FROM songs";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
             ResultSet resSet = preparedStatement.executeQuery()) {
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

    public static ObservableList<SongModel> getFilteredFromDB(String search) {
        ObservableList<SongModel> list = FXCollections.observableArrayList();
        String sqlStatement = "SELECT * FROM songs WHERE songName LIKE ?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, "%" + search + "%");
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

    public SongModel updateToDB(String formerSongID) {
        String sqlStatement = "UPDATE songs SET songID = ?, songName = ?, songAudio = ?, albumID = ? WHERE songID = ?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            this.setSuccessStatus(false);
            preparedStatement.setString(1, this.getSongID());
            preparedStatement.setString(2, this.getSongName());
            preparedStatement.setBytes(3, this.getSongAudio());
            preparedStatement.setString(4, this.getAlbumID());
            preparedStatement.setString(5, formerSongID);
            int modiRows = preparedStatement.executeUpdate();
            if (modiRows > 0) this.setSuccessStatus(true);
            return this;
        } catch (SQLException e) {
            return this;
        }
    }

    public SongModel deleteFromDB() {
        String sqlStatement = "DELETE FROM songs WHERE songID = ?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            this.setSuccessStatus(false);
            preparedStatement.setString(1, this.getSongID());
            int modiRows = preparedStatement.executeUpdate();
            if (modiRows > 0) this.setSuccessStatus(true);
            return this;
        } catch (SQLException e) {
            return this;
        }
    }

    public byte[] getAlbumCover() {
        String sqlStatement = "SELECT albumCover FROM albums WHERE albumID = ?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, this.getAlbumID());
            ResultSet resSet = preparedStatement.executeQuery();
            if (resSet.next()) {
                return resSet.getBytes("albumCover");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public int getAlbumYear() {
        String sqlStatement = "SELECT albumYear FROM albums WHERE albumID = ?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, this.getAlbumID());
            ResultSet resSet = preparedStatement.executeQuery();
            if (resSet.next()) {
                return resSet.getInt("albumYear");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return 0;
    }

    public String getArtistName() {
        String artistID, artistName;
        String sqlStatement = "SELECT artistID FROM albums WHERE albumID = ?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, this.getAlbumID());
            ResultSet resSet = preparedStatement.executeQuery();
            if (resSet.next()) {
                artistID = resSet.getString("artistID");
                String sqlStatement2 = "SELECT artistName FROM artists WHERE artistID = ?";
                try (PreparedStatement preparedStatement1 = connection.prepareStatement(sqlStatement2)) {
                    preparedStatement1.setString(1, artistID);
                    ResultSet resSet1 = preparedStatement1.executeQuery();
                    if (resSet1.next()) {
                        artistName = resSet1.getString("artistName");
                        return artistName;
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
