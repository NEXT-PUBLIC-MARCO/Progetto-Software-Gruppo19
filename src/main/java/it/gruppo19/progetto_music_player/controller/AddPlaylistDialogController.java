package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.PlaylistModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Path;
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
    @FXML private Label changeActionText;
    @FXML private Label changeDescription;
    @FXML private Button changeButtonName;

    private boolean confirmed = false;
    private PlaylistModel result;
    private File foto;
    private PlaylistModel editing;

    public boolean isConfirmed() { return confirmed; }

    public PlaylistModel getResult() { return result; }

    @FXML
    private void onBrowsePhoto(ActionEvent e) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Seleziona copertina");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Immagini", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"));
        foto = fc.showOpenDialog(window(e));
        if (foto != null) {
            fotoPathField.setText(foto.getAbsolutePath());
        }
    }

    @FXML
    private void onConfirm(ActionEvent e) {
        String nome = trim(nomeField.getText());
        Path  f = null;
        if ( foto != null){
                f =  foto.toPath();

        }
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
                f,
                new ArrayList<>());
        confirmed = true;
        close(e);
    }

    @FXML
    private void onCancel(ActionEvent e) {
        confirmed = false;
        close(e);
    }

    public void setPlaylist(PlaylistModel p) {
        System.out.println("[DEBUG] Entrato in setPlaylist(). Oggetto ricevuto: " + p);

        // Controllo di sicurezza se l'oggetto p è null
        if (p == null) {
            System.out.println("[DEBUG] ATTENZIONE: Il PlaylistModel passato è NULL! Esco dalla funzione.");
            return;
        }

        this.editing = p;
        changeActionText.setText("Modifica Playlist");
        changeDescription.setText("Modifica i campi della playlist");
        changeButtonName.setText("Salva");
        System.out.println("[DEBUG] Impostato questo.editing con il playlist ID: " + p.getId()); // Assumendo che ci sia un getId()

        // Caricamento testi
        nomeField.setText(p.getTitolo());
        descrizioneArea.setText(p.getDescrizione());
        System.out.println("[DEBUG] Campi testo impostati -> Titolo: " + p.getTitolo() + " | Descrizione: " + p.getDescrizione());

        // Debug della sezione Immagine (corretto typo 'Immaggine')
        if (p.getPathImmagine() != null) {
            this.foto = p.getPathImmagine().toFile();
            fotoPathField.setText(this.foto.getAbsolutePath());
            System.out.println("[DEBUG] Immagine trovata: " + this.foto.getAbsolutePath());
        } else {
            this.foto = null;
            fotoPathField.setText("");
            System.out.println("[DEBUG] Immagine NON presente (null). Campo di testo svuotato.");
        }

        System.out.println("[DEBUG] Esecuzione setPlaylist() completata con successo.\n");
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }

    private Stage window(ActionEvent e) {
        return (Stage) ((Node) e.getSource()).getScene().getWindow();
    }

    private void close(ActionEvent e) { window(e).close(); }
}
