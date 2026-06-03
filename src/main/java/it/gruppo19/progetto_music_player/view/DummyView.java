package it.gruppo19.progetto_music_player.view;

import it.gruppo19.progetto_music_player.MusicPlayerApplication;
import it.gruppo19.progetto_music_player.controller.MainViewController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class DummyView {

    /** Il controller creato da FXMLLoader: lo teniamo per poterci iniettare il model. */
    private final MainViewController controller;

    public DummyView() throws IOException {
        Stage stage = MusicPlayerApplication.GetStage();
        FXMLLoader fxmlLoader = new FXMLLoader(MusicPlayerApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);

        // <-- il pezzo chiave: recupero l'istanza del controller appena creata
        controller = fxmlLoader.getController();

        stage.setTitle("Unisafy");
        stage.setScene(scene);
    }

    public MainViewController getController() {
        return controller;
    }
}
