package it.gruppo19.progetto_music_player;

import it.gruppo19.progetto_music_player.controller.MainViewController;
import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.storage.Storage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;


public class MusicPlayerApplication extends Application {

    private static Stage stage;
    private Storage storage;
    private DataModel model;
    private MainViewController controller;
    //public static Stage GetStage(){ return stage; }

    @Override
    public void start(Stage stage) throws IOException {
        MusicPlayerApplication.stage = stage;
        storage = new Storage();
        model = new DataModel(storage.LoadBrani(), storage.LoadPlaylist());

        FXMLLoader fxmlLoader = new FXMLLoader(MusicPlayerApplication.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 800);
        controller = fxmlLoader.getController();

        controller.setModel(model);
        controller.setStorage(storage);
        model.Attach(controller);
        //controller.OnAppStartup();
        stage.setTitle("Unisafy");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}