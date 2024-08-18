package org.example.soundscape;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConnection {
    private String connString = "jdbc:sqlite:./soundscape.db";
    public Connection connection(){
        try {
            return DriverManager.getConnection(connString);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
