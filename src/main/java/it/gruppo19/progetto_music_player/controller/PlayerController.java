package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.model.PlaylistModel;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

/**
 * Controller per la schermata principale (Melodia - 01 Homepage).
 * Per ora gli handler sono stub: l'fxml e' stato generato dal design Figma
 * e collega gia' tutti i nodi tramite fx:id.
 */
public class PlayerController {

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

    /** Il model condiviso, iniettato da RealMainController dopo il load dell'FXML. */
    private DataModel model;

    public void setModel(DataModel model) {
        this.model = model;
        // appena ho il model posso disegnare la lista iniziale
        refreshLibrary();
    }

    /** Ridisegna la lista in base al contenuto del model (stub da completare). */
    private void refreshLibrary() {
        if (model == null) return;
        // TODO: svuotare songList e ricostruire una riga per ogni model.getBrani()
        //       poi mostrare/nascondere l'empty-state di conseguenza.
    }

    @FXML
    private void onAdd() {
        // finestra "proprietaria" del dialog (quella corrente)
        Window owner = addButton.getScene().getWindow();

        if (tabPlaylist.isSelected()) {
            // tab Playlist -> dialog "Nuova playlist"
            AddPlaylistDialogController dialog =
                    Dialogs.openModal(owner, "dialog-add-playlist.fxml", "Nuova playlist");
            if (dialog.isConfirmed()) {
                PlaylistModel nuova = dialog.getResult();
                // TODO: model.addPlaylist(nuova) e poi aggiornare la lista
                model.addPlaylist(nuova);
            }
        } else {
            // tab Brani -> dialog "Aggiungi brano"
            AddTrackDialogController dialog =
                    Dialogs.openModal(owner, "dialog-add-track.fxml", "Aggiungi brano");
            if (dialog.isConfirmed()) {
                BranoModel nuovo = dialog.getResult();
                model.addBrani(nuovo);
            }
        }
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

    @FXML
    private void onTabChanged(){

    }

    @FXML
    private void onRemoveFromPlaylist(){

    }

    @FXML
    private void onAddTrackToPlaylist(){

    }

    @FXML
    private void onNewPlaylist(){

    }
}
