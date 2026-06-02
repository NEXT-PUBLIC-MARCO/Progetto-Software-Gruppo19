package it.gruppo19.progetto_music_player;

import it.gruppo19.progetto_music_player.controller.RealMainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MusicPlayerApplication extends Application {

    private static Stage stage;

    public static Stage GetStage(){return stage;}

    @Override
    public void start(Stage stage) throws IOException {
        MusicPlayerApplication.stage = stage;
        RealMainController controller = new RealMainController();
        /*
        FXMLLoader fxmlLoader = new FXMLLoader(MusicPlayerApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);
        stage.setTitle("Unisafy");
        stage.setScene(scene);
         */
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}