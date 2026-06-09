package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.model.observerPattern.Observer;
import it.gruppo19.progetto_music_player.model.PlaylistModel;
import it.gruppo19.progetto_music_player.storage.Storage;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Controller per la schermata principale (Melodia - 01 Homepage).
 * Per ora gli handler sono stub: l'fxml e' stato generato dal design Figma
 * e collega gia' tutti i nodi tramite fx:id.
 */
public class MainViewController implements Observer {


    // ATTRIBUTI GENERICI =============================================
    private DataModel model;
    private  Storage storage;

    // ATTRIBUTI LEFT PANE ============================================
    @FXML private ToggleButton tabBrani;
    @FXML private ToggleButton tabPlaylist;
    @FXML private Label sectionLabel;
    @FXML private VBox songList;
    @FXML private VBox emptyLibrary;


    @FXML public Label titleLabel;
    @FXML public Label subtitleLabel;

    @FXML private StackPane braniSidebar;
    @FXML private VBox playerCard;

    @FXML private ScrollPane playlistSidebar;
    @FXML private VBox playlistSidebarList;

    @FXML private Button addButton;



    //ATTRIBUTI RIGHT PANE ================================================
    // to do

    // METODI GENERICI ===================================================
    public void setModel(DataModel model) {
        System.out.println("[DEBUG] setModel() chiamato. model=" + model);
        this.model = model;
        // appena ho il model posso disegnare le liste iniziali
        refreshLibrary();
        refreshPlaylists();
        //model.Attach(this);
    }

    public void setStorage(Storage storage){
        this.storage = storage;
    }

    private void refreshLibrary() {
        // SEZIONE 1: Validazione e Inizializzazione ---------------------------------------------------------
        System.out.println("[DEBUG] refreshLibrary() INIZIO. model=" + model
                + ", songList=" + songList);

        if (model == null) {
            System.out.println("[DEBUG] refreshLibrary: model NULL -> esco");
            return;
        }
        if (songList == null) {
            // se songList e' null l'fx:id non e' stato iniettato: NPE silenziosa piu' avanti
            System.out.println("[DEBUG] refreshLibrary: songList NULL (fx:id non iniettato!) -> esco");
            return;
        }

        // SEZIONE 2: Reset UI e Gestione Visibilità --------------------------------------------------------------
        System.out.println("[DEBUG] refreshLibrary: numero brani = " + model.getBrani().size());

        songList.getChildren().clear();// Bug C: ricostruisco da zero

        boolean hasBrani = !model.getBrani().isEmpty();
        setShown(songList, hasBrani);
        setShown(emptyLibrary,!hasBrani);

        // SEZIONE 3: Rendering Dinamico dei Brani -----------------------------------------------------------------
        for (BranoModel brano : model.getBrani()) {
            System.out.println("[DEBUG] -- render brano: titolo=" + brano.getTitolo()
                    + ", artista=" + brano.getArtista());
            try {
                // --- SEZIONE 3.1: Caricamento Layout Card ---
                FXMLLoader loader = new FXMLLoader( // Bug B: un loader per card
                        getClass().getResource("/it/gruppo19/progetto_music_player/track-info-card.fxml")
                );
                System.out.println("[DEBUG]    URL track-card.fxml = " + loader.getLocation());
                Node card = loader.load();
                System.out.println("[DEBUG]    card caricata = " + card);

                // --- SEZIONE 3.2: Popolamento Dati Interfaccia ---
                Label title    = (Label) loader.getNamespace().get("titleLabel");
                title.setText(brano.getTitolo());

                Label subtitle = (Label) loader.getNamespace().get("subtitleLabel");
                subtitle.setText(brano.getArtista());

                ImageView image = (ImageView) loader.getNamespace().get("cardImage");
                if(brano.getPathImmaggine() != null && brano.getPathImmaggine().toFile().exists())
                    image.setImage(new Image(brano.getPathImmaggine().toUri().toString()));
                System.out.println("[DEBUG]    namespace -> title=" + title + ", subtitle=" + subtitle);

                // --- SEZIONE 3.3: Configurazione Eventi (Listener) ---
                Button editButton = (Button) loader.getNamespace().get("editButton");
                editButton.setOnAction(e ->modifyBrano(brano));

                Button deleteButton = (Button) loader.getNamespace().get("deleteButton");
                deleteButton.setOnAction(e ->eliminaBrano(brano));

                // --- SEZIONE 3.4: Aggiornamento Scena ---
                songList.getChildren().add(card);
                System.out.println("[DEBUG]    card aggiunta. songList children = "
                        + songList.getChildren().size());
            } catch (Exception e) {
                // --- SEZIONE 3.5: Gestione Errori Rendering ---
                // Intercetta eccezioni isolate per singola card, impedendo che l'intero ciclo di render si blocchi.
                System.out.println("[DEBUG]    !!! ECCEZIONE durante il render:");
                e.printStackTrace();               // così gli errori NON spariscono più
            }
        }
        System.out.println("[DEBUG] refreshLibrary() FINE");
    }


    boolean DeletePopup(String mainLabel, String messageLabel){
        Window owner = addButton.getScene().getWindow(); //Non è sempre lo stesso l'owner?
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/gruppo19/progetto_music_player/dialog-delete.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DeleteTrackDialogController controller = loader.getController();
        controller.setMainLabel(mainLabel);
        controller.setMessageLabel(messageLabel);

        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.setScene(new Scene(root));
        dialog.showAndWait();

        return controller.hasDeleted();
    }

    private void eliminaBrano(BranoModel brano){
        if(DeletePopup(
                "Elimina brano",
                "Vuoi eliminare definitivamente il brano " + brano.getTitolo() + "? Questa azione non può essere annullata."
        )) model.removeBrani(brano);
        storage.SaveBrani(new ArrayList<>( model.getBrani()) );
    }

    private void modifyBrano(BranoModel brano){
        Window owner = addButton.getScene().getWindow(); //Non è sempre lo stesso l'owner?
        AddTrackDialogController controller = Dialogs.openModal1(owner, "dialog-add-track.fxml", "Modifica brano", (AddTrackDialogController c) -> c.setBrano(brano));
        if(controller.isConfirmed()) model.updateBrani(brano);
    }

    private void eliminaPlaylist(PlaylistModel playlist){
        if(DeletePopup(
                "Elimina playlist",
                "Vuoi eliminare definitivamente la playlist " + playlist.getTitolo() + "? Questa azione non può essere annullata."
        )) model.removePlaylist(playlist);
        storage.SavePlaylist( new ArrayList<>( model.getPlaylists()));
    }

    private void modifyPlaylist(PlaylistModel playlist){
        // TODO: Aggiungere logica di modifica per le playlist analoga a modifyBrano,
        // aprendo il AddPlaylistDialogController e creando un metodo setPlaylist(...)
        System.out.println("[DEBUG] Apertura pannello di modifica per la playlist: " + playlist.getTitolo());
        Window owner = addButton.getScene().getWindow();
        AddPlaylistDialogController controller = Dialogs.openModal1(owner, "dialog-add-playlist.fxml", "Modifica playlist", (AddPlaylistDialogController c) -> c.setPlaylist(playlist));
        if(controller.isConfirmed()) model.updatePlaylist(playlist);
    }

    private void infoPlaylist(PlaylistModel playlist){
        // TODO: Logica per aprire le info / vista dettaglio della playlist
        System.out.println("[DEBUG] Apri PopUp Info per la playlist: " + playlist.getTitolo());
    }

    private void refreshPlaylists() {
        // SEZIONE 1: Validazione e Inizializzazione ---------------------------------------------------------
        System.out.println("[DEBUG] refreshPlaylist() INIZIO. model=" + model
                + ", playlistSidebarList=" + playlistSidebarList);

        if (model == null) {
            System.out.println("[DEBUG] refreshPlaylist: model NULL -> esco");
            return;
        }
        if (playlistSidebarList == null) {
            // se playlistSidebarList e' null l'fx:id non e' stato iniettato
            System.out.println("[DEBUG] refreshPlaylist: playlistSidebarList NULL (fx:id non iniettato!) -> esco");
            return;
        }

        // SEZIONE 2: Reset UI e Gestione Visibilità --------------------------------------------------------------
        System.out.println("[DEBUG] refreshPlaylist: numero playlist = " + model.getPlaylists().size());

        playlistSidebarList.getChildren().clear();// ricostruisco da zero

        // boolean hasPlaylist = !model.getPlaylists().isEmpty(); // Nel caso in cui metteremo emptyState

        // SEZIONE 3: Rendering Dinamico delle Playlist -----------------------------------------------------------------
        for (PlaylistModel playlist : model.getPlaylists()) {
            System.out.println("[DEBUG] -- render playlist: titolo=" + playlist.getTitolo());
            try {
                // --- SEZIONE 3.1: Caricamento Layout Card ---
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/it/gruppo19/progetto_music_player/playlist-info-card.fxml")
                );
                System.out.println("[DEBUG]    URL playlist-info-card.fxml = " + loader.getLocation());
                Node card = loader.load();
                System.out.println("[DEBUG]    card caricata = " + card);

                // --- SEZIONE 3.2: Popolamento Dati Interfaccia ---
                Label title = (Label) loader.getNamespace().get("titleLabel");
                title.setText(playlist.getTitolo());

                Label subtitle = (Label) loader.getNamespace().get("subtitleLabel");
                if (subtitle != null) {
                    // Mostriamo il numero di brani come sottotitolo o la descrizione
                    subtitle.setText(playlist.getBrani().size() + " brani");
                }

                ImageView image = (ImageView) loader.getNamespace().get("cardImage");
                if (playlist.getPathImmagine() != null && playlist.getPathImmagine().toFile().exists()) {
                    try {
                        image.setImage(new Image(playlist.getPathImmagine().toUri().toString()));
                    } catch (IllegalArgumentException ex) {
                        System.out.println("[DEBUG] Impossibile caricare l'immagine per: " + playlist.getTitolo());
                    }
                }
                System.out.println("[DEBUG]    namespace -> title=" + title + ", subtitle=" + subtitle);

                // --- SEZIONE 3.3: Configurazione Eventi (Listener) ---
                Button editButton = (Button) loader.getNamespace().get("editButton");
                editButton.setOnAction(e -> modifyPlaylist(playlist));

                Button infoButton = (Button) loader.getNamespace().get("infoButton");
                infoButton.setOnAction(e -> infoPlaylist(playlist));

                Button deleteButton = (Button) loader.getNamespace().get("deleteButton");
                deleteButton.setOnAction(e -> eliminaPlaylist(playlist));

                // --- SEZIONE 3.4: Aggiornamento Scena ---
                playlistSidebarList.getChildren().add(card);
                System.out.println("[DEBUG]    card aggiunta. playlistSidebarList children = "
                        + playlistSidebarList.getChildren().size());

            } catch (Exception e) {
                // --- SEZIONE 3.5: Gestione Errori Rendering ---
                System.out.println("[DEBUG]    !!! ECCEZIONE durante il render delle playlist:");
                e.printStackTrace();               // così gli errori NON spariscono più
            }
        }
        System.out.println("[DEBUG] refreshPlaylist() FINE");
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
                storage.SavePlaylist((ArrayList<PlaylistModel>) model.getPlaylists());
                //refreshPlaylists();
            }
        } else {
            // tab Brani -> dialog "Aggiungi brano"
            AddTrackDialogController dialog =
                    Dialogs.openModal(owner, "dialog-add-track.fxml", "Aggiungi brano");
            System.out.println("[DEBUG] onAdd (brano): confirmed=" + dialog.isConfirmed());
            if (dialog.isConfirmed()) {
                BranoModel nuovo = dialog.getResult();
                System.out.println("[DEBUG] onAdd: nuovo brano = " + nuovo
                        + (nuovo != null ? " (" + nuovo.getTitolo() + ")" : ""));
                model.addBrani(nuovo);
                storage.SaveBrani((ArrayList<BranoModel>) model.getBrani());

                //refreshLibrary();
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
            //refreshPlaylists();
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
        System.out.println("[DEBUG] Update() ricevuto. event=" + event);
        if(Objects.equals(event, "BraniChange")) refreshLibrary();
        if(Objects.equals(event, "PlaylistChange")) refreshPlaylists();
    }

}

