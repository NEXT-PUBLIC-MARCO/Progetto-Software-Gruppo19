package it.gruppo19.progetto_music_player;

import it.gruppo19.progetto_music_player.controller.MainViewController;
import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.storage.Storage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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

        // Finestra senza barra di Windows (look "frameless").
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(scene);

        // Senza barra la finestra non sarebbe spostabile: la rendo trascinabile
        // dalla top bar (l'unica zona senza controlli interattivi).
        Node bar = ((BorderPane) scene.getRoot()).getTop();
        if (bar != null) {
            final double[] drag = new double[2];   // offset press → origine finestra
            bar.setOnMousePressed(e -> {
                drag[0] = e.getScreenX() - stage.getX();
                drag[1] = e.getScreenY() - stage.getY();
            });
            bar.setOnMouseDragged(e -> {
                stage.setX(e.getScreenX() - drag[0]);
                stage.setY(e.getScreenY() - drag[1]);
            });
        }

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}