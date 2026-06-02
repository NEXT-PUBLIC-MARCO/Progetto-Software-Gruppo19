package it.gruppo19.progetto_music_player;

import it.gruppo19.progetto_music_player.controller.Controller;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class MusicPlayerApplication extends Application {

    private static Stage stage;
    public static Stage GetStage(){ return stage; }

    @Override
    public void start(Stage stage) throws IOException {
        MusicPlayerApplication.stage = stage;
        Controller controller = new Controller();
        controller.OnAppStartup();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}