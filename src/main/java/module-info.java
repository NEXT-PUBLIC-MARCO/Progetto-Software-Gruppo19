module it.gruppo19.progetto_music_player {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;


    opens it.gruppo19.progetto_music_player to javafx.fxml;
    exports it.gruppo19.progetto_music_player;
    exports it.gruppo19.progetto_music_player.controller;
    opens it.gruppo19.progetto_music_player.controller to javafx.fxml;
}