package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.model.BranoModel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
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
    @FXML private TextField annoField;
    @FXML private TextField audioPathField;
    @FXML private TextField fotoPathField;
    @FXML private Label errorLabel;
    @FXML private CheckBox favouriteCheck;
    @FXML private CheckBox newReleaseCheck;
    @FXML private CheckBox explicitCheck;

    @FXML private Label changeActionText;
    @FXML private Label changeActionDesc;
    @FXML private Button changeButtonName;




    private BranoModel editing;
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

        //-- Anno --
        int anno = 0;
        String annoTxt = annoField.getText().trim();

        if (!annoTxt.isEmpty()) {
            anno = Integer.parseInt(annoTxt);
        }
        //----------

        Path musica = null;
        Path foto = null;
        if(audio != null) musica = audio.toPath();
        if(image != null) foto = image.toPath();

        if (titolo.isEmpty() || artista.isEmpty() || audio == null || !audio.exists()) {
            showError("Titolo, artista e file audio sono obbligatori.");
            return;
        }
        if(editing != null){

            editing.setTitolo(titolo);
            editing.setArtista(artista);
            editing.setGenere(genere);
            editing.setAnno(anno);
            editing.setPathAudio(musica);
            editing.setPathImmaggine(foto);
            editing.setFavourite(favouriteCheck.isSelected());
            editing.setNewRelease(newReleaseCheck.isSelected());
            editing.setExplicit(explicitCheck.isSelected());
            result = editing;
            confirmed = true;
            close(e);
        }else{
            result = new BranoModel(
                    UUID.randomUUID().toString(),
                    titolo,
                    "",          // descrizione: non richiesta in questo dialog
                    artista,
                    genere,
                    anno,
                    foto,
                    musica);
            result.setFavourite(favouriteCheck.isSelected());
            result.setNewRelease(newReleaseCheck.isSelected());
            result.setExplicit(explicitCheck.isSelected());
            confirmed = true;
            close(e);
        }
        //System.out.println("[DEBUG] anno inserito: " + anno);

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

    public void setBrano(BranoModel b) {
        System.out.println("[DEBUG] Entrato in setBrano(). Oggetto ricevuto: " + b);

        // Controllo di sicurezza se l'oggetto b è null
        if (b == null) {
            System.out.println("[DEBUG] ATTENZIONE: Il BranoModel passato è NULL! Esco dalla funzione.");
            return;
        }

        this.editing = b;
        changeActionText.setText("Modifica Brano");
        changeActionDesc.setText("Modifica i campi del brano");
        changeButtonName.setText("Salva");
        System.out.println("[DEBUG] Impostato questo.editing con il brano ID: " + b.getId()); // Assumendo che ci sia un getId()

        // Caricamento testi e combo
        titoloField.setText(b.getTitolo());
        artistaField.setText(b.getArtista());
        genereCombo.setValue(b.getGenere());
        favouriteCheck.setSelected(b.isFavourite());
        newReleaseCheck.setSelected(b.isNewRelease());
        explicitCheck.setSelected(b.isExplicit());
        System.out.println("[DEBUG] Campi testo impostati -> Titolo: " + b.getTitolo() + " | Artista: " + b.getArtista() + " | Genere: " + b.getGenere());

        // Debug della sezione Audio
        if (b.getPathAudio() != null) {
            this.audio = b.getPathAudio().toFile();
            audioPathField.setText(this.audio.getAbsolutePath());
            System.out.println("[DEBUG] Audio trovato: " + this.audio.getAbsolutePath());
        } else {
            this.audio = null;
            audioPathField.setText("");
            System.out.println("[DEBUG] Audio NON presente (null). Campo di testo svuotato.");
        }

        // Debug della sezione Immagine (corretto typo 'Immaggine')
        if (b.getPathImmaggine() != null) {
            this.image = b.getPathImmaggine().toFile();
            fotoPathField.setText(this.image.getAbsolutePath());
            System.out.println("[DEBUG] Immagine trovata: " + this.image.getAbsolutePath());
        } else {
            this.image = null;
            fotoPathField.setText("");
            System.out.println("[DEBUG] Immagine NON presente (null). Campo di testo svuotato.");
        }

        System.out.println("[DEBUG] Esecuzione setBrano() completata con successo.\n");
    }

    private static String trim(String s) { return s == null ? "" : s.trim(); }

    private Stage window(ActionEvent e) {
        return (Stage) ((Node) e.getSource()).getScene().getWindow();
    }

    private void close(ActionEvent e) { window(e).close(); }
}
