package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.model.BranoModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
import java.util.UUID;

/**
 * Controller del dialog "Aggiungi brano".
 * Raccoglie titolo, artista, genere, file audio e file foto e, alla conferma,
 * costruisce un {@link BranoModel}. L'esito si legge con {@link #isConfirmed()}
 * e {@link #getResult()}.
 */
public class AddTrackDialogController {

    @FXML private TextField titoloField;
    @FXML private TextField artistaField;
    @FXML private ComboBox<String> genereCombo;
    @FXML private TextField audioPathField;
    @FXML private TextField fotoPathField;
    @FXML private Label errorLabel;

    private boolean confirmed = false;
    private BranoModel result;
    private File image;
    private File audio;

    public boolean isConfirmed() { return confirmed; }
    public BranoModel getResult() { return result; }

    @FXML
    private void onBrowseAudio(ActionEvent e) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleziona file audio");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Audio", "*.mp3", "*.wav", "*.flac", "*.m4a", "*.ogg", "*.mpeg"));
        audio = fc.showOpenDialog(window(e));
        if (audio != null) {
            audioPathField.setText(audio.getAbsolutePath());
        }
    }

    @FXML
    private void onBrowsePhoto(ActionEvent e) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleziona copertina");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
        image = fc.showOpenDialog(window(e));
        if (image != null) {
            fotoPathField.setText(image.getAbsolutePath());
        }
    }

    @FXML
    private void onConfirm(ActionEvent e) {
        String titolo = trim(titoloField.getText());
        String artista = trim(artistaField.getText());
        String genere = genereCombo.getValue() != null ? genereCombo.getValue()
                : trim(genereCombo.getEditor().getText());
        Path musica = null;
        Path foto = null;
        if(audio != null) musica = audio.toPath();
        if(image != null) foto = image.toPath();

        if (titolo.isEmpty() || artista.isEmpty() || audio == null || !audio.exists()) {
            showError("Titolo, artista e file audio sono obbligatori.");
            return;
        }

        result = new BranoModel(
                UUID.randomUUID().toString(),
                titolo,
                "",          // descrizione: non richiesta in questo dialog
                artista,
                genere,
                foto,
                musica);
        confirmed = true;
        close(e);
    }

    @FXML
    private void onCancel(ActionEvent e) {
        confirmed = false;
        close(e);
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }

    private Stage window(ActionEvent e) {
        return (Stage) ((Node) e.getSource()).getScene().getWindow();
    }

    private void close(ActionEvent e) { window(e).close(); }
}
