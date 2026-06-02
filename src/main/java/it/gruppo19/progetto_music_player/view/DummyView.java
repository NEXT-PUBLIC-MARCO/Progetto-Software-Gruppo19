package it.gruppo19.progetto_music_player.view;

import it.gruppo19.progetto_music_player.MusicPlayerApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DummyView {

    public DummyView() throws IOException {
        Stage stage = MusicPlayerApplication.GetStage();
        FXMLLoader fxmlLoader = new FXMLLoader(MusicPlayerApplication.class.getResource("main-view.fxml"));
        Scene scene= new Scene(fxmlLoader.load(), 1280, 800);
        stage.setTitle("Unisafy");
        stage.setScene(scene);
    }

}
