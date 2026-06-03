package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.model.Observer;
import it.gruppo19.progetto_music_player.model.PlaylistModel;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.Objects;

/**
 * Controller per la schermata principale (Melodia - 01 Homepage).
 * Per ora gli handler sono stub: l'fxml e' stato generato dal design Figma
 * e collega gia' tutti i nodi tramite fx:id.
 */
public class MainViewController implements Observer {

    // ATTRIBUTI GENERICI =============================================
    private DataModel model;

    // ATTRIBUTI LEFT PANE ============================================
    @FXML private ToggleButton tabBrani;
    @FXML private ToggleButton tabPlaylist;
    @FXML private Label sectionLabel;
    @FXML private VBox songList;

    @FXML private StackPane braniSidebar;
    @FXML private VBox playerCard;

    @FXML private ScrollPane playlistSidebar;
    @FXML private VBox playlistSidebarList;

    @FXML private Button addButton;


    //ATTRIBUTI RIGHT PANE ================================================
   // to do

    // METODI GENERICI ===================================================
    public void setModel(DataModel model) {
        this.model = model;
        // appena ho il model posso disegnare le liste iniziali
        refreshLibrary();
        refreshPlaylists();
    }

    private void refreshLibrary() {

        if (model == null) return;
        // TODO: svuotare songList e ricostruire una riga per ogni model.getBrani()
        //       poi mostrare/nascondere l'empty-state di conseguenza.

        if (model == null)
            return;
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/it/gruppo19/progetto_music_player/track-card.fxml")
        );
        for(BranoModel brano : model.getBrani()){
            try{
                Node card = loader.load();

                Label title = (Label)card.lookup("titleLabel");
                Label subtitle = (Label)card.lookup("subtitleLabel");
                ImageView image = (ImageView)card.lookup("cardImage");

                title.setText(brano.getTitolo());
                subtitle.setText(brano.getArtista());

                songList.getChildren().add(card);
            }catch(Exception e){

            }
        }


    }
    private void refreshPlaylists() {
        // TODO: refresh dello playlist mostrate
    }

    // METODI LEFT PANE ===================================================
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
                model.addPlaylist(nuova);
                refreshPlaylists();
            }
        } else {
            // tab Brani -> dialog "Aggiungi brano"
            AddTrackDialogController dialog =
                    Dialogs.openModal(owner, "dialog-add-track.fxml", "Aggiungi brano");
            if (dialog.isConfirmed()) {
                BranoModel nuovo = dialog.getResult();
                model.addBrani(nuovo);
                refreshLibrary();
            }
        }

    }

    @FXML
    private void onTabChanged(){
        boolean playlist = tabPlaylist.isSelected();

        // sidebar
        setShown(braniSidebar, !playlist);
        setShown(playlistSidebar, playlist);

        // etichetta di sezione
        if (sectionLabel != null) {
            sectionLabel.setText(playlist ? "LE TUE PLAYLIST" : "LIBRERIA");
        }
    }

    private void setShown(Node node, boolean shown) {
        if (node == null) return;
        node.setVisible(shown);
        node.setManaged(shown);
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
    private void onRemoveFromPlaylist(){

    }

    @FXML
    private void onAddTrackToPlaylist(){

    }

    @FXML
    private void onNewPlaylist(){
        if (model == null || addButton == null) return;
        Window owner = addButton.getScene().getWindow();
        AddPlaylistDialogController dialog =
                Dialogs.openModal(owner, "dialog-add-playlist.fxml", "Nuova playlist");
        if (dialog.isConfirmed()) {
            PlaylistModel nuova = dialog.getResult();
            model.addPlaylist(nuova);
            refreshPlaylists();
        }
    }

    // METODI RIGHT PANE ===================================================
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


    @Override
    public void Update(String event, Object object) {
        if(!Objects.equals(event, "BraniChange")) return;
        refreshLibrary();
    }

}

