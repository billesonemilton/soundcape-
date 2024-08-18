package org.example.soundscape;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

import java.io.IOException;

public class SoundscapeApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SoundscapeApplication.class.getResource("/org/example/soundscape/auth.fxml"));
        Parent root = fxmlLoader.load();
        stage.setTitle("Soundscape");
        ScrollPane scroller = new ScrollPane(root);
        Scene scene = new Scene(scroller);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        System.out.println(System.getProperty("java.library.path"));
        launch();
    }
}