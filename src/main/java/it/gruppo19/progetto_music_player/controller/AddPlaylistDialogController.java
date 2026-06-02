package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.model.PlaylistModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Controller del dialog "Nuova playlist".
 * Alla conferma costruisce una {@link PlaylistModel} vuota.
 */
public class AddPlaylistDialogController {

    @FXML private TextField nomeField;
    @FXML private TextArea descrizioneArea;
    @FXML private TextField fotoPathField;
    @FXML private Label errorLabel;

    private boolean confirmed = false;
    private PlaylistModel result;

    public boolean isConfirmed() { return confirmed; }
    public PlaylistModel getResult() { return result; }

    @FXML
    private void onBrowsePhoto(ActionEvent e) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleziona copertina");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
        File f = fc.showOpenDialog(window(e));
        if (f != null) {
            fotoPathField.setText(f.getAbsolutePath());
        }
    }

    @FXML
    private void onConfirm(ActionEvent e) {
        String nome = trim(nomeField.getText());
        if (nome.isEmpty()) {
            errorLabel.setText("Il nome della playlist è obbligatorio.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return;
        }

        result = new PlaylistModel(
                UUID.randomUUID().toString(),
                nome,
                trim(descrizioneArea.getText()),
                trim(fotoPathField.getText()),
                new ArrayList<>());
        confirmed = true;
        close(e);
    }

    @FXML
    private void onCancel(ActionEvent e) {
        confirmed = false;
        close(e);
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }

    private Stage window(ActionEvent e) {
        return (Stage) ((Node) e.getSource()).getScene().getWindow();
    }

    private void close(ActionEvent e) { window(e).close(); }
}
