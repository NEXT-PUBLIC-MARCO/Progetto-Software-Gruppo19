package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.model.DataModel;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import javafx.scene.control.Label;

import java.awt.*;

public class DeleteTrackDialogController {

    @FXML private Label messageLabel;
    private boolean delete;

    public boolean hasDeleted(){ return delete; }

    @FXML
    private void onCancel(){
        delete = false;
        close();
    }

    @FXML
    private void onConfirm(){
        delete = true;
        close();
    }

    private void close(){
        Stage stage = (Stage) messageLabel.getScene().getWindow();
        stage.close();
    }

}
