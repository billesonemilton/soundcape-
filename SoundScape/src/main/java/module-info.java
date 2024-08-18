module org.example.soundscape {
        requires javafx.controls;
        requires javafx.fxml;
        requires java.sql;
    requires java.desktop;
    requires javafx.media;
    requires fontawesomefx;

    opens org.example.soundscape to javafx.fxml;
        opens org.example.soundscape.Models to javafx.base;

        exports org.example.soundscape;
        exports org.example.soundscape.Models;
}
