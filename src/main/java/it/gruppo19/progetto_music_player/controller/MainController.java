package it.gruppo19.progetto_music_player.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;

/**
 * Controller per la schermata principale (Melodia - 01 Homepage).
 * Per ora gli handler sono stub: l'fxml e' stato generato dal design Figma
 * e collega gia' tutti i nodi tramite fx:id.
 */
public class MainController {

    @FXML private ToggleButton tabBrani;
    @FXML private ToggleButton tabPlaylist;

    @FXML private VBox songList;

    @FXML private VBox playerCard;
    @FXML private Label nowTitle;
    @FXML private Label nowArtist;
    @FXML private ProgressBar progress;
    @FXML private Label elapsed;
    @FXML private Label total;

    @FXML private Button playButton;
    @FXML private Button addButton;

    @FXML
    private void onAdd() {
        // TODO: aprire il dialog "Aggiungi brano"
    }

    @FXML
    private void onPlayPause() {
        // TODO: play/pausa del brano corrente
    }

    @FXML
    private void onPrev() {
        // TODO: brano precedente
    }

    @FXML
    private void onNext() {
        // TODO: brano successivo
    }

    @FXML
    private void onShuffle() {
        // TODO: attiva/disattiva riproduzione casuale
    }

    @FXML
    private void onRepeat() {
        // TODO: attiva/disattiva ripeti
    }

    @FXML
    private void onAddToPlaylist() {
        // TODO: aprire il popup "Aggiungi a playlist"
    }

    @FXML
    private void onDelete() {
        // TODO: aprire il popup "Elimina brano"
    }
}
