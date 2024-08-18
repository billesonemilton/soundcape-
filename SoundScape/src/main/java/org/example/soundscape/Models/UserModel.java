package org.example.soundscape.Models;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.soundscape.SQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserModel {
    private final SimpleStringProperty userID;
    private final SimpleStringProperty userName;
    private final SimpleStringProperty password;
    private Boolean successStatus;

    public Boolean getSuccessStatus() {
        return successStatus;
    }

    public void setSuccessStatus(Boolean successStatus) {
        this.successStatus = successStatus;
    }

    public UserModel(String userID, String userName, String password) {
        this.userID = new SimpleStringProperty(userID);
        this.userName = new SimpleStringProperty(userName);
        this.password = new SimpleStringProperty(password);
    }

    public String getUserID() {
        return userID.get();
    }

    public void setUserID(String userID) {
        this.userID.set(userID);
    }
    public String getUserName() {
        return userName.get();
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public UserModel saveToDB() {
        String sqlStatement = "INSERT INTO users(userID, userName, password) VALUES(?, ?, ?)";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            this.setSuccessStatus(false);
            preparedStatement.setString(1, this.getUserID());
            preparedStatement.setString(2, this.getUserName());
            preparedStatement.setString(3, this.getPassword());
            int modiRows = preparedStatement.executeUpdate();
            if (modiRows > 0) this.setSuccessStatus(true);
            return this;
        } catch (SQLException e) {
            return this;
        }
    }

    public static UserModel getUserByID(String userID) {
        String sqlStatement = "SELECT * FROM users WHERE userID=?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, userID);
            ResultSet resSet = preparedStatement.executeQuery();
            if (resSet.next()) {
                UserModel currentUser = new UserModel(resSet.getString("userID"), resSet.getString("userName"), resSet.getString("password"));
                return currentUser;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static UserModel LoginUser(String userName, String password){
        String sqlStatement = "SELECT * FROM users WHERE userName=? AND password=?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, userName);
            preparedStatement.setString(2,password);
            ResultSet resSet = preparedStatement.executeQuery();
            if (resSet.next()) {
                UserModel currentUser = new UserModel(resSet.getString("userID"), resSet.getString("userName"), resSet.getString("password"));
                return currentUser;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ObservableList<SongModel> getLikedSongs() {
        ObservableList<SongModel> list = FXCollections.observableArrayList();
        String sqlStatement = "SELECT * FROM songs WHERE songID IN (SELECT songID FROM likes WHERE userID=?)";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, this.getUserID());
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

    public Boolean checkLike(String songID){
        String sqlStatement = "SELECT * FROM likes WHERE songID=? AND userID=?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1, songID);
            preparedStatement.setString(2,this.getUserID());
            ResultSet resSet = preparedStatement.executeQuery();
            if (resSet.next()) {
                return  true;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addLike(String songID){
        String sqlStatement = "INSERT INTO likes(userID,songID) VALUES (?,?)";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1,this.getUserID());
            preparedStatement.setString(2, songID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeLike(String songID){
        String sqlStatement = "DELETE FROM likes WHERE userID=? AND songId=?";
        try (Connection connection = new SQLConnection().connection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {
            preparedStatement.setString(1,this.getUserID());
            preparedStatement.setString(2, songID);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
