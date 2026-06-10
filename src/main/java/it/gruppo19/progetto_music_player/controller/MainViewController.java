package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.model.commandPattern.*;
import it.gruppo19.progetto_music_player.model.observerPattern.Observable;
import it.gruppo19.progetto_music_player.model.observerPattern.Observer;
import it.gruppo19.progetto_music_player.model.PlaylistModel;
import it.gruppo19.progetto_music_player.storage.Storage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

/**
 * Controller per la schermata principale (Melodia - 01 Homepage).
 * Per ora gli handler sono stub: l'fxml e' stato generato dal design Figma
 * e collega gia' tutti i nodi tramite fx:id.
 */
public class MainViewController implements Observer {


    // ATTRIBUTI GENERICI =============================================
    private DataModel model;
    private  Storage storage;
    private final ObservableList<BranoModel> braniItems = FXCollections.observableArrayList();
    private final ObservableList<PlaylistModel> playlistItems = FXCollections.observableArrayList();
    private final Stack<Command> commands = new Stack<>();
    // ATTRIBUTI LEFT PANE ============================================
    @FXML private ToggleButton tabBrani;
    @FXML private ToggleButton tabPlaylist;
    @FXML private Label sectionLabel;
    @FXML private ListView<BranoModel> songListView;

    @FXML private VBox emptyLibrary;


    @FXML public Label titleLabel;
    @FXML public Label subtitleLabel;
    @FXML public Label annoLabel;

    @FXML private StackPane braniSidebar;
    @FXML private VBox playerCard;

    @FXML private ScrollPane playlistSidebar;
    @FXML private ListView<PlaylistModel> playlistSidebarList;

    @FXML private Button addButton;

    @FXML private Label durataLabel;



    //ATTRIBUTI RIGHT PANE ================================================
    @FXML private VBox playerCardEmpty;
    @FXML private VBox playerCardActive;
    @FXML private ImageView playerImage;
    @FXML private Label playerTitle;
    @FXML private Label playerArtist;
    @FXML private Label playerCurrentTime;
    @FXML private Label playerDurata;


    // METODI GENERICI ===================================================

    public void Undo(){
        Command command = commands.pop();
        if(command != null) command.undo();
        storage.SaveBrani(new ArrayList<>(model.getBrani()));
        storage.SavePlaylist(new ArrayList<>(model.getPlaylists()));
    }

    @FXML
    public void initialize() {
        songListView.setItems(braniItems);
        songListView.setCellFactory(lv -> new ListCell<BranoModel>(){
            private Node card;
            private Label title, subtitle, anno, durata;
            private ImageView image;

            // blocco di inizializzazione: eseguito una volta alla creazione della cella
            {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/it/gruppo19/progetto_music_player/track-info-card.fxml")
                    );
                    card = loader.load();

                    // la cella non impone larghezza propria e la card non supera la ListView:
                    // così i Label troncano con "..." invece di sforare a destra
                    setPrefWidth(0);
                    ((VBox) card).maxWidthProperty().bind(songListView.widthProperty().subtract(30));

                    title    = (Label) loader.getNamespace().get("titleLabel");
                    subtitle = (Label) loader.getNamespace().get("subtitleLabel");
                    anno     = (Label) loader.getNamespace().get("annoLabel");
                    image    = (ImageView) loader.getNamespace().get("cardImage");
                    durata = (Label) loader.getNamespace().get("durataLabel");

                    // handler impostati UNA volta: usano getItem() = brano corrente della cella
                    Button editButton = (Button) loader.getNamespace().get("editButton");
                    editButton.setOnAction(e -> { if (getItem() != null) modifyBrano(getItem()); });

                    Button addToPlaylistButton = (Button) loader.getNamespace().get("addToPlaylistButton");
                    addToPlaylistButton.setOnAction(e -> { if (getItem() != null) addTrackToPlaylist(getItem()); });

                    Button deleteButton = (Button) loader.getNamespace().get("deleteButton");
                    deleteButton.setOnAction(e -> { if (getItem() != null) eliminaBrano(getItem()); });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // chiamato dal ListView a ogni disegno/riciclo della cella
            @Override
            protected void updateItem(BranoModel brano, boolean empty) {
                super.updateItem(brano, empty);
                if (empty || brano == null || card == null) {
                    setGraphic(null);
                    return;
                }
                title.setText(brano.getTitolo());
                subtitle.setText(brano.getArtista());
                anno.setText(Integer.toString(brano.getAnno()));
                durata.setText(brano.getDurataFormattata());

                if (brano.getPathImmaggine() != null && brano.getPathImmaggine().toFile().exists())
                    image.setImage(new Image(brano.getPathImmaggine().toUri().toString(), true));
                else
                    image.setImage(null);   // evita che resti l'immagine del brano precedente (riciclo)

                setGraphic(card);
            }
        });

        songListView.getSelectionModel().selectedItemProperty().addListener((obs,vecchio,nuovo )-> {
            if(nuovo != null) mostraPlayer(nuovo);
        });

        songListView.sceneProperty().addListener((obs, oldScene, scene) -> {
            if (scene != null) {
                scene.getAccelerators().put(
                        new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN),
                        this::Undo
                );
            }
        });


        playlistSidebarList.setItems(playlistItems);
        playlistSidebarList.setCellFactory(lv -> new ListCell<PlaylistModel>(){
            private Node card;
            private Label title, subtitle;
            private ImageView image;

            {
                try {
                    FXMLLoader loader = new FXMLLoader(
                            getClass().getResource("/it/gruppo19/progetto_music_player/playlist-info-card.fxml")
                    );
                    card = loader.load();

                    title    = (Label) loader.getNamespace().get("titleLabel");
                    subtitle = (Label) loader.getNamespace().get("subtitleLabel");
                    image    = (ImageView) loader.getNamespace().get("cardImage");

                    Button editButton = (Button) loader.getNamespace().get("editButton");
                    editButton.setOnAction(e -> { if (getItem() != null) modifyPlaylist(getItem()); });

                    Button infoButton = (Button) loader.getNamespace().get("infoButton");
                    infoButton.setOnAction(e -> { if (getItem() != null) infoPlaylist(getItem()); });

                    Button deleteButton = (Button) loader.getNamespace().get("deleteButton");
                    deleteButton.setOnAction(e -> { if (getItem() != null) eliminaPlaylist(getItem()); });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void updateItem(PlaylistModel playlist, boolean empty) {
                super.updateItem(playlist, empty);
                if (empty || playlist == null || card == null) {
                    setGraphic(null);
                    return;
                }
                title.setText(playlist.getTitolo());
                subtitle.setText(playlist.getBrani().size() + " brani");

                if (playlist.getPathImmagine() != null && playlist.getPathImmagine().toFile().exists()) {
                    try {
                        image.setImage(new Image(playlist.getPathImmagine().toUri().toString(), true));
                    } catch (IllegalArgumentException ex) {
                        image.setImage(null);
                    }
                } else {
                    image.setImage(null);
                }

                setGraphic(card);
            }
        });
    }
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


    public void mostraPlayer(BranoModel b){
        playerCardEmpty.setVisible(false);
        playerCardEmpty.setManaged(false);
        playerCardActive.setVisible(true);
        playerCardActive.setManaged(true);
        playerTitle.setText(b.getTitolo());
        playerArtist.setText(b.getArtista());
        playerDurata.setText(b.getDurataFormattata());
        if(b.getPathImmaggine() != null && b.getPathImmaggine().toFile().exists()){
            playerImage.setImage(new Image(b.getPathImmaggine().toUri().toString(),true));
        }
        else{
            playerImage.setImage(null);
        }

    }


    public void refreshLibrary() {
        if (model == null || songListView == null) return;

        braniItems.setAll(model.getBrani());

        boolean hasBrani = !model.getBrani().isEmpty();
        setShown(songListView, hasBrani);
        setShown(emptyLibrary, !hasBrani);
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
        )) {
            commands.add(new RemoveBrano(brano, model));
            commands.getFirst().execute();
        }
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
        )){
            commands.add(new RemovePlaylist(playlist, model));
            commands.getFirst().execute();
        }
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


    public void refreshPlaylists() {
        if (model == null || playlistSidebarList == null) return;
        playlistItems.setAll(model.getPlaylists());
    }

    @FXML
    private void addTrackToPlaylist(BranoModel brano){
        Window owner = addButton.getScene().getWindow();
        Dialogs.openModal1(owner, "dialog-add-to-playlist.fxml", "Aggiungi a una Playlist", (AddToPlaylistDialogController c) -> {
            c.setModel(model);
            c.addBranoToPlaylist(brano);
        });

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
                commands.add(new AddPlaylist(nuova, model));
                commands.getFirst().execute();
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
                commands.add(new AddBrano(nuovo, model));
                commands.getFirst().execute();
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
    private void onDelete() {
        // TODO: aprire il popup "Elimina brano"
    }

    @FXML
    private void onRemoveFromPlaylist(){

    }

    @FXML
    private void onNewPlaylist(){
        if (model == null || addButton == null) return;
        Window owner = addButton.getScene().getWindow();
        AddPlaylistDialogController dialog =
                Dialogs.openModal(owner, "dialog-add-playlist.fxml", "Nuova playlist");
        if (dialog.isConfirmed()) {
            PlaylistModel nuova = dialog.getResult();
            commands.add(new AddPlaylist(nuova, model));
            commands.getFirst().execute();
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

