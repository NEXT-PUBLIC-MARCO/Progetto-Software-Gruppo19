package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.model.commandPattern.*;
import it.gruppo19.progetto_music_player.model.iteratorPattern.PlayerIterable;
import it.gruppo19.progetto_music_player.model.iteratorPattern.PlayerIterator;
import it.gruppo19.progetto_music_player.model.observerPattern.Observer;
import it.gruppo19.progetto_music_player.model.PlaylistModel;
import it.gruppo19.progetto_music_player.model.strategyPattern.*;
import it.gruppo19.progetto_music_player.storage.Storage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.animation.RotateTransition;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.geometry.Pos;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.security.PrivilegedAction;
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

    @FXML private Slider playerSlider;
    @FXML private Button playPauseButton;
    @FXML private FontIcon playPauseIcon;
    @FXML private FontIcon coverPlaceholder;
    @FXML private StackPane vinyl;          // disco che ruota durante la riproduzione
    private RotateTransition vinylSpin;     // animazione del vinile (giro continuo)
    @FXML private Button shuffleButton;     // shuffle a 3 stati (vedi onShuffle)
    private int shuffleState = 0;           // 0=spento, 1=freccia attiva, 2=shuffle attivo

    @FXML private VBox playlistCardActive;
    @FXML private ImageView playlistImage;
    @FXML private Label playlistTitle;
    @FXML private Label playlistCount;
    @FXML private ListView<BranoModel> playlistSongsList;
    private PlaylistModel playlistVisualizzata;   // playlist attualmente aperta nella colonna centrale


    private MediaPlayer mediaPlayer;       // player corrente
    private boolean isUserSeeking = false;

    // METODI GENERICI ===================================================

    public void Undo() {
        // Command command = commands.pop();
        // if(command != null) command.undo();
        // storage.SaveBrani(new ArrayList<>(model.getBrani()));
        // storage.SavePlaylist(new ArrayList<>(model.getPlaylists()));
        if (!commands.isEmpty()) {
            Command command = commands.pop();
            command.undo();

            storage.SaveBrani(new ArrayList<>(model.getBrani()));
            storage.SavePlaylist(new ArrayList<>(model.getPlaylists()));
        }
    }

    private String formatTime(javafx.util.Duration d) {
        int sec = (int) d.toSeconds();
        return String.format("%d:%02d", sec / 60, sec % 60);
    }

    @FXML
    public void initialize() {
        playerSlider.setOnMousePressed(e -> isUserSeeking = true);
        playerSlider.setOnMouseReleased(e -> {
            if(mediaPlayer != null) mediaPlayer.seek(Duration.seconds(playerSlider.getValue()));
            isUserSeeking = false;
        });

        // La foto fa da "etichetta" centrale del vinile: clip circolare che segue
        // le dimensioni reali dell'immagine (preserveRatio → può non essere quadrata).
        Circle coverClip = new Circle();
        playerImage.layoutBoundsProperty().addListener((obs, oldB, newB) -> {
            double d = Math.min(newB.getWidth(), newB.getHeight());
            coverClip.setRadius(d / 2.0);
            coverClip.setCenterX(newB.getWidth() / 2.0);
            coverClip.setCenterY(newB.getHeight() / 2.0);
        });
        playerImage.setClip(coverClip);

        // Rotazione del vinile: giro continuo e lineare, in pausa di default.
        // Viene avviata/sospesa da onPlayPause in base allo stato del player.
        if (vinyl != null) {
            vinylSpin = new RotateTransition(Duration.seconds(6), vinyl);
            vinylSpin.setByAngle(360);
            vinylSpin.setCycleCount(Animation.INDEFINITE);
            vinylSpin.setInterpolator(Interpolator.LINEAR);
        }

        // Titoli lunghi vengono troncati con "...": un tooltip recupera il testo
        // completo al passaggio del mouse (sempre allineato al titolo corrente).
        Tooltip titleTip = new Tooltip();
        titleTip.textProperty().bind(playerTitle.textProperty());
        Tooltip.install(playerTitle, titleTip);
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

        // Reagisce al click (non al cambio di selezione): così funziona anche
        // ri-cliccando l'elemento già selezionato.
        songListView.setOnMouseClicked(e -> {
            BranoModel sel = songListView.getSelectionModel().getSelectedItem();
            if (sel != null){
                sel.incrementaAscolti();
                if(storage != null && model != null){
                    storage.SaveBrani(new java.util.ArrayList<>(model.getBrani()));
                }
                refreshLibrary();

                refreshPlaylists();

                mostraPlayer(DataModel.getInstance().createIterator(sel));
            }
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
        // Reagisce al click (non al cambio di selezione): così tornare sulla
        // playlist già selezionata mostra di nuovo la sua card.
        playlistSidebarList.setOnMouseClicked(e -> {
            PlaylistModel sel = playlistSidebarList.getSelectionModel().getSelectedItem();
            if (sel != null) mostraPlaylist(sel);
        });

        playlistSongsList.setCellFactory(lv -> new ListCell<BranoModel>() {
            private final Label t = new Label();
            private final Label s = new Label();
            private final Button deleteTrackFromPlaylist = new Button();
            private final VBox texts = new VBox(2, t, s);
            private final Region spacer = new Region();
            private final HBox box = new HBox(8, texts, spacer, deleteTrackFromPlaylist);
            {
                t.getStyleClass().add("track-title");
                s.getStyleClass().add("track-artist");
                deleteTrackFromPlaylist.getStyleClass().add("track-delete");
                deleteTrackFromPlaylist.setGraphic(new FontIcon("fas-trash"));
                ((FontIcon) deleteTrackFromPlaylist.getGraphic()).setIconSize(16);

                HBox.setHgrow(texts, Priority.ALWAYS);
                HBox.setHgrow(spacer, Priority.ALWAYS);
                box.setAlignment(Pos.CENTER_LEFT);
            }
            @Override
            protected void updateItem(BranoModel b, boolean empty) {
                super.updateItem(b, empty);
                if (empty || b == null) {
                    setGraphic(null);
                    return;
                }
                t.setText(b.getTitolo());
                s.setText(b.getArtista() + " · " + b.getDurataFormattata());
                setGraphic(box);
                deleteTrackFromPlaylist.setOnAction(e -> {
                    System.out.println("test click");
                    PlaylistModel currentPlaylist =
                            playlistSidebarList.getSelectionModel().getSelectedItem();
                    if (currentPlaylist != null) {
                        commands.push(new RemoveBranoFromPlaylist(b, currentPlaylist));
                        commands.peek().execute();
                        // qui probabilmente vorrai anche aggiornare la lista:
                        playlistSongsList.getItems().remove(b);
                        aggiornaContatoriPlaylist();
                        storage.SavePlaylist(new ArrayList<>(model.getPlaylists()));
                    }
                });
            }
        });

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
        model.Attach(this);
    }


    public void setStorage(Storage storage){
        this.storage = storage;
    }


    public void mostraPlayer(PlayerIterator iterable) {

        if(iterable != null) iterator = iterable;
        if(iterator == null) return; //-------------------- Che deve fare????? -----------------------------------

        playlistCardActive.setVisible(false);
        playlistCardActive.setManaged(false);
        playerCardEmpty.setVisible(false);
        playerCardEmpty.setManaged(false);
        playerCardActive.setVisible(true);
        playerCardActive.setManaged(true);
        playerTitle.setText(iterator.getCurrent().getTitolo());
        playerArtist.setText(iterator.getCurrent().getArtista());
        playerDurata.setText(iterator.getCurrent().getDurataFormattata());
        boolean hasArtwork = iterator.getCurrent().getPathImmaggine() != null && iterator.getCurrent().getPathImmaggine().toFile().exists();
        if(hasArtwork){
            playerImage.setImage(new Image(iterator.getCurrent().getPathImmaggine().toUri().toString(),true));
        }
        else{
            playerImage.setImage(null);
        }
        // placeholder (nota) visibile solo quando il brano non ha copertina
        coverPlaceholder.setVisible(!hasArtwork);
        coverPlaceholder.setManaged(!hasArtwork);
        caricaAudio(iterator.getCurrent());
    }

    public void mostraPlaylist(PlaylistModel p){
        playerCardEmpty.setVisible(false);
        playerCardEmpty.setManaged(false);
        playerCardActive.setVisible(false);
        playerCardActive.setManaged(false);
        playlistCardActive.setVisible(true);
        playlistCardActive.setManaged(true);
        this.playlistVisualizzata = p;


        // intestazione: titolo, conteggio brani, copertina
        playlistTitle.setText(p.getTitolo());
        playlistCount.setText(p.getBrani().size() + " brani");
        if (p.getPathImmagine() != null && p.getPathImmagine().toFile().exists()) {
            playlistImage.setImage(new Image(p.getPathImmagine().toUri().toString(), true));
        } else {
            playlistImage.setImage(null);
        }

        // riempio la lista centrale con i brani della playlist
        playlistSongsList.setItems(FXCollections.observableArrayList(p.getBrani()));
    }

    private void caricaAudio(BranoModel b){
        if(mediaPlayer != null){
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
        // nuovo brano: parte in pausa → vinile fermo e dritto, icona "play"
        if (vinylSpin != null) {
            vinylSpin.stop();
            vinyl.setRotate(0);
        }
        if (playPauseIcon != null) playPauseIcon.setIconLiteral("fas-play");
        if(b == null || b.getPathAudio() == null || !b.getPathAudio().toFile().exists()) return;
        Media media = new Media(b.getPathAudio().toUri().toString());
        mediaPlayer = new MediaPlayer(media);

        // quando il media è pronto conosco la durata reale → imposto il max dello slider
        mediaPlayer.setOnReady(() -> {
            playerSlider.setMax(media.getDuration().toSeconds());

        });

        // avanzamento: aggiorna slider + tempo corrente (solo se l'utente non sta trascinando)
        mediaPlayer.currentTimeProperty().addListener((obs, old, cur) -> {
            if (!isUserSeeking) playerSlider.setValue(cur.toSeconds());
            playerCurrentTime.setText(formatTime(cur));
        });

        // a fine brano: torna su play e ferma il vinile
        mediaPlayer.setOnEndOfMedia(() -> {
            if(iterator == null || !iterator.hasNext()){
                playPauseIcon.setIconLiteral("fas-play");
                if (vinylSpin != null) vinylSpin.pause();
            } else {
                iterator.getNext();
                mostraPlayer(null);
            }
        });

        if (!(iterator.getPlaybackOrderStrat() instanceof NoAutoPlay)) {
            mediaPlayer.play();
            playPauseIcon.setIconLiteral("fas-pause");
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
            //commands.add(new RemoveBrano(brano, model));
            //commands.getFirst().execute();
            commands.push(new RemoveBrano(brano, model));
            commands.peek().execute();   // Esegue l’ultimo comando inserito
        }
        storage.SaveBrani(new ArrayList<>( model.getBrani()) );
        storage.SavePlaylist(new ArrayList<>(model.getPlaylists()));
        mediaPlayer.stop();
        mediaPlayer.dispose();
        mediaPlayer = null;
        setShown(playerCardEmpty, true);
        setShown(playerCardActive, false);
        vinylSpin.stop();
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
            // commands.add(new RemovePlaylist(playlist, model));
            // commands.getFirst().execute();

            commands.push(new RemovePlaylist(playlist, model));
            commands.peek().execute();

        }
        storage.SavePlaylist( new ArrayList<>( model.getPlaylists()));
    }

    private void modifyPlaylist(PlaylistModel playlist){
        // TODO: Aggiungere logica di modifica per le playlist analoga a modifyBrano,
        // aprendo il AddPlaylistDialogController e creando un metodo setPlaylist(...)
        System.out.println("[DEBUG] Apertura pannello di modifica per la playlist: " + playlist.getTitolo());
        Window owner = addButton.getScene().getWindow();
        AddPlaylistDialogController controller = Dialogs.openModal1(owner, "dialog-add-playlist.fxml", "Modifica playlist", (AddPlaylistDialogController c) -> c.setPlaylist(playlist));

        if(controller.isConfirmed()) {
            model.updatePlaylist(playlist);
            storage.SavePlaylist(new ArrayList<>(model.getPlaylists()));

            refreshPlaylists();
            mostraPlaylist(playlist);
        }
    }

    private void infoPlaylist(PlaylistModel playlist){
        // TODO: Logica per aprire le info / vista dettaglio della playlist
        System.out.println("[DEBUG] Apri PopUp Info per la playlist: " + playlist.getTitolo());
    }


    public void refreshPlaylists() {
        if (model == null || playlistSidebarList == null) return;
        playlistItems.setAll(model.getPlaylists());

        playlistSidebarList.refresh();
    }

    private void aggiornaContatoriPlaylist() {
        refreshPlaylists();
        if (playlistVisualizzata != null) {
            playlistCount.setText(playlistVisualizzata.getBrani().size() + " brani");
        }
    }

    @FXML
    private void addTrackToPlaylist(BranoModel brano){
        Window owner = addButton.getScene().getWindow();
        Dialogs.openModal1(owner, "dialog-add-to-playlist.fxml", "Aggiungi a una Playlist", (AddToPlaylistDialogController c) -> {
            c.setModel(model);
            c.addBranoToPlaylist(brano);
        });
        aggiornaContatoriPlaylist();
        storage.SavePlaylist(new ArrayList<>(model.getPlaylists()));
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
                // commands.add(new AddPlaylist(nuova, model));
                // commands.getFirst().execute();

                commands.push(new AddPlaylist(nuova, model));
                commands.peek().execute();

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
                // commands.add(new AddBrano(nuovo, model));
                // commands.getFirst().execute();

                commands.push(new AddBrano(nuovo, model));
                commands.peek().execute();

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

    // METODI PLAYER ===================================================

    private PlayerIterator iterator;
    @FXML
    private void onPlayPause() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaPlayer.pause();
            playPauseIcon.setIconLiteral("fas-play");
            if (vinylSpin != null) vinylSpin.pause();   // ferma il giro mantenendo l'angolo
        } else {
            mediaPlayer.play();
            playPauseIcon.setIconLiteral("fas-pause");
            if (vinylSpin != null) vinylSpin.play();     // riprende il giro
        }
    }

    @FXML
    private void onPrev() {
        if(iterator != null) {
            iterator.getPrevious();
            mostraPlayer(null);
        }
    }

    @FXML
    private void onNext() {
        if(iterator != null) {
            iterator.getNext();
            mostraPlayer(null);
        }
    }

    @FXML
    private void onShuffle() {
        if (iterator == null) return;

        // Ciclo a 3 stati: 0 freccia spenta → 1 freccia accesa → 2 shuffle acceso → 0.
        shuffleState = (shuffleState + 1) % 3;

        if (shuffleButton != null) {
            // Reset classi UI
            shuffleButton.getStyleClass().removeAll("is-active", "is-shuffle");

            if (shuffleState == 0) {
                // Stato 0: freccia spenta -> Nessun avanzamento automatico (si ferma a fine brano)
                iterator.setPlaybackStrat(new NoAutoPlay());
                iterator.setOrderStrat(new SequentialStrat());

            } else if (shuffleState == 1) {
                // Stato 1: freccia accesa -> Riproduzione continua in ordine sequenziale
                shuffleButton.getStyleClass().add("is-active");
                iterator.setOrderStrat(new SequentialStrat());
                iterator.setPlaybackStrat(new LoopStrat());

            } else if (shuffleState == 2) {
                // Stato 2: icona shuffle -> Riproduzione continua in ordine casuale
                shuffleButton.getStyleClass().addAll("is-active", "is-shuffle");
                iterator.setOrderStrat(new ShuffleStrat());
                iterator.setPlaybackStrat(new LoopStrat());
            }
        }
    }

    @FXML
    private void onRepeat() {
        if(iterator != null) {
            if (iterator.getPlaybackOrderStrat() instanceof NoAutoPlay)
                iterator.setPlaybackStrat(new PlayOnceStrat());
            else if (iterator.getPlaybackOrderStrat() instanceof PlayOnceStrat)
                iterator.setPlaybackStrat(new LoopStrat());
            else
                iterator.setPlaybackStrat(new NoAutoPlay());
        }

        // aggiornamento UI bottone loop
    }

    @Override
    public void Update(String event, Object object) {
        System.out.println("[DEBUG] Update() ricevuto. event=" + event);
        if(Objects.equals(event, "BraniChange")) refreshLibrary();
        if(Objects.equals(event, "PlaylistChange")) refreshPlaylists();
    }

}