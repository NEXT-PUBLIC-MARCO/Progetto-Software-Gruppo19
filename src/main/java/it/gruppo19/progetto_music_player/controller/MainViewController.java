package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.model.Observer;
import it.gruppo19.progetto_music_player.model.PlaylistModel;
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

    private void refreshLibrary() {
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

        System.out.println("[DEBUG] refreshLibrary: numero brani = " + model.getBrani().size());

        songList.getChildren().clear();// Bug C: ricostruisco da zero

        boolean hasBrani = !model.getBrani().isEmpty();
        setShown(songList, hasBrani);
        setShown(emptyLibrary,!hasBrani);

        for (BranoModel brano : model.getBrani()) {
            System.out.println("[DEBUG] -- render brano: titolo=" + brano.getTitolo()
                    + ", artista=" + brano.getArtista());
            try {
                FXMLLoader loader = new FXMLLoader( // Bug B: un loader per card
                        getClass().getResource("/it/gruppo19/progetto_music_player/info-card.fxml")
                );
                System.out.println("[DEBUG]    URL track-card.fxml = " + loader.getLocation());
                Node card = loader.load();
                System.out.println("[DEBUG]    card caricata = " + card);

                // Bug A: leggo i nodi via namespace fx:id (niente lookup CSS
                Label title    = (Label) loader.getNamespace().get("titleLabel");
                title.setText(brano.getTitolo());

                Label subtitle = (Label) loader.getNamespace().get("subtitleLabel");
                subtitle.setText(brano.getArtista());

                ImageView image = (ImageView) loader.getNamespace().get("cardImage");
                if(brano.getPathImmaggine() != null && brano.getPathImmaggine().toFile().exists())
                    image.setImage(new Image(brano.getPathImmaggine().toUri().toString()));
                System.out.println("[DEBUG]    namespace -> title=" + title + ", subtitle=" + subtitle);


             /*   ToggleButton search = (ToggleButton) loader.getNamespace().get("searchToggle");
                VBox expandedSection = (VBox) loader.getNamespace().get("expandedSection");
                TextField editTitleField = (TextField) loader.getNamespace().get("editTitleField");
                search.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
                    expandedSection.setVisible(isSelected);
                    expandedSection.setManaged(isSelected);
                    title.setVisible(!isSelected);
                    subtitle.setVisible(!isSelected);
                });
                    */
                //Qui viene definito il contest menu del singolo brano nella view
                ContextMenu contextMenu = new ContextMenu();


                MenuItem modify = new MenuItem("Modifica");
                modify.setOnAction(e -> ContextMenuModifyBrano(brano));
                contextMenu.getItems().add(modify);

                Menu aggiungiPlaylist = new Menu("Aggiungi a playlist");
                for(PlaylistModel playlist : model.getPlaylists()){
                    MenuItem item = new MenuItem(playlist.getTitolo());
                    item.setOnAction(e -> {});
                    aggiungiPlaylist.getItems().add(item);
                }
                contextMenu.getItems().add(aggiungiPlaylist);

                MenuItem elimina = new MenuItem("Elimina Brano");
                elimina.setOnAction(e -> ContextMenuEliminaBrano(brano));
                contextMenu.getItems().add(elimina);

                //Il bottone con i 3 puntini che deve aprire il context menu
                Button menuButton = (Button) loader.getNamespace().get("menuButton");
                menuButton.setOnAction(e -> contextMenu.show(
                        menuButton,
                        javafx.geometry.Side.BOTTOM,
                        0, 0
                ));

                songList.getChildren().add(card);
                System.out.println("[DEBUG]    card aggiunta. songList children = "
                        + songList.getChildren().size());
            } catch (Exception e) {
                System.out.println("[DEBUG]    !!! ECCEZIONE durante il render:");
                e.printStackTrace();               // così gli errori NON spariscono più
            }
        }
        System.out.println("[DEBUG] refreshLibrary() FINE");
    }

    private void ContextMenuEliminaBrano(BranoModel brano){
        Window owner = addButton.getScene().getWindow(); //Non è sempre lo stesso l'owner?
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/gruppo19/progetto_music_player/dialog-delete.fxml"));
            Parent root = loader.load();
            DeleteTrackDialogController controller = loader.getController();
            controller.setMainLabel("Elimina brano");
            controller.setMessageLabel(
                    "Vuoi eliminare definitivamente il brano " + brano.getTitolo() + "? Questa azione non può essere annullata."
            );

            Stage dialog = new Stage();
            dialog.initOwner(owner);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            if(controller.hasDeleted()) model.removeBrani(brano);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void ContextMenuModifyBrano(BranoModel brano){
        Window owner = addButton.getScene().getWindow(); //Non è sempre lo stesso l'owner?
        AddTrackDialogController controller = Dialogs.openModal1(owner, "dialog-add-track.fxml", "Modifica brano", (AddTrackDialogController c) -> c.setBrano(brano));
        if(controller.isConfirmed()) model.updateBrani(brano);
    }

    private void ContextMenuEliminaPlaylist(PlaylistModel playlist){
        Window owner = addButton.getScene().getWindow(); //Non è sempre lo stesso l'owner?
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/it/gruppo19/progetto_music_player/dialog-delete.fxml"));
            Parent root = loader.load();
            DeleteTrackDialogController controller = loader.getController();
            controller.setMainLabel("Elimina playlist");
            controller.setMessageLabel(
                    "Vuoi eliminare definitivamente la playlist " + playlist.getTitolo() + "? Questa azione non può essere annullata."
            );

            Stage dialog = new Stage();
            dialog.initOwner(owner);
            dialog.setScene(new Scene(root));
            dialog.showAndWait();

            if(controller.hasDeleted()) model.removePlaylist(playlist);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshPlaylists() {
        // TODO: refresh dello playlist mostrate
        System.out.println("[DEBUG] refreshPlaylist() INIZIO. model=" + model
                + ", playlistSidebarList=" + playlistSidebarList);

        if (model == null) {
            System.out.println("[DEBUG] refreshPlaylist: model NULL -> esco");
            return;
        }
        if (playlistSidebarList == null) {
            // se songList e' null l'fx:id non e' stato iniettato: NPE silenziosa piu' avanti
            System.out.println("[DEBUG] refreshPlaylist: songList NULL (fx:id non iniettato!) -> esco");
            return;
        }

        System.out.println("[DEBUG] refreshPlaylist: numero brani = " + model.getPlaylists().size());

        playlistSidebarList.getChildren().clear();// Bug C: ricostruisco da zero

        //boolean hasPlaylist = !model.getPlaylists().isEmpty(); nel caso in cui metteremo emptyState

        for (PlaylistModel playlist : model.getPlaylists()) {
            System.out.println("[DEBUG] -- render playlist: titolo=" + playlist.getTitolo());
            try {
                FXMLLoader loader = new FXMLLoader( // Bug B: un loader per card
                        getClass().getResource("/it/gruppo19/progetto_music_player/info-card.fxml")
                );
                System.out.println("[DEBUG]    URL track-card.fxml = " + loader.getLocation());
                Node card = loader.load();
                System.out.println("[DEBUG]    card caricata = " + card);

                // Bug A: leggo i nodi via namespace fx:id (niente lookup CSS
                Label title    = (Label) loader.getNamespace().get("titleLabel");
                title.setText(playlist.getTitolo());
                Label subtitle = (Label) loader.getNamespace().get("subtitleLabel");
                if (subtitle != null) {
                    subtitle.setText(""); // Svuota il testo di default dell'FXML
                }

                ImageView image = (ImageView) loader.getNamespace().get("cardImage");
                if (playlist.getPathImmagine() != null && !playlist.getPathImmagine().trim().isEmpty()) {
                    try {
                        image.setImage(new Image(playlist.getPathImmagine()));
                    } catch (IllegalArgumentException ex) {
                        System.out.println("[DEBUG] Impossibile caricare l'immagine per: " + playlist.getTitolo());
                        // Qui potresti impostare un'immagine di default (es. l'icona di un disco)
                    }
                }
                System.out.println("[DEBUG]    namespace -> title=" + title);


                //Qui viene definito il contest menu della singola playlist nella view
                ContextMenu contextMenu = new ContextMenu();

                MenuItem info = new MenuItem("Info Playlist");
                info.setOnAction(e -> System.out.println("Apri PopUp Info"));
                contextMenu.getItems().add(info);

                /*
                 * SE DOVESSE SERVIRE, ANDREBBE AGGIUNTO IL POPUP PER INSERIRE UN BRANO NELLA PLAYLIST
                 * */

                MenuItem elimina = new MenuItem("Elimina Playlist");
                elimina.setOnAction(e -> ContextMenuEliminaPlaylist(playlist));
                contextMenu.getItems().add(elimina);

                //Il bottone con i 3 puntini che deve aprire il context menu
                Button menuButton = (Button) loader.getNamespace().get("menuButton");
                menuButton.setOnAction(e -> contextMenu.show(
                        menuButton,
                        javafx.geometry.Side.BOTTOM,
                        0, 0
                ));

                playlistSidebarList.getChildren().add(card);
                System.out.println("[DEBUG]    card aggiunta. playlistSidebarList children = "
                        + playlistSidebarList.getChildren().size());
            } catch (Exception e) {
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

