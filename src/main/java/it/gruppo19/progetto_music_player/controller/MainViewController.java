package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.controller.cells.BranoCell;
import it.gruppo19.progetto_music_player.controller.cells.PlaylistCell;
import it.gruppo19.progetto_music_player.controller.cells.PlaylistSongCell;
import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.model.PlaylistModel;
import it.gruppo19.progetto_music_player.model.commandPattern.*;
import it.gruppo19.progetto_music_player.model.iteratorPattern.PlayerIterator;
import it.gruppo19.progetto_music_player.model.observerPattern.Observer;
import it.gruppo19.progetto_music_player.model.strategyPattern.*;
import it.gruppo19.progetto_music_player.storage.Storage;
import javafx.application.Platform;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.beans.binding.Bindings;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
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
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

/**
 * Controller della schermata principale (Melodia - Homepage).
 *
 * Principio di sincronizzazione: la UI è SEMPRE una funzione dello stato.
 *  - i dati (brani/playlist) cambiano via {@link DataModel} → {@link #Update} ridisegna.
 *  - lo stato del player vive in {@link PlayerEngine}; la UI è legata alle sue
 *    Property in {@link #bindPlayerUi()} e si aggiorna da sola.
 * Gli handler dei bottoni non toccano i widget: cambiano solo lo stato.
 */
public class MainViewController implements Observer {

    // STATO / MODELLO ================================================
    private DataModel model;
    private Storage storage;
    private final ObservableList<BranoModel> braniItems = FXCollections.observableArrayList();
    private final ObservableList<PlaylistModel> playlistItems = FXCollections.observableArrayList();
    private final Stack<Command> commands = new Stack<>();
    private final PlayerEngine player = new PlayerEngine();
    private PlaylistModel playlistVisualizzata;   // playlist aperta nella colonna centrale

    // LEFT PANE ======================================================
    @FXML private ToggleButton tabPlaylist;
    @FXML private Label sectionLabel;
    @FXML private ListView<BranoModel> songListView;
    @FXML private VBox emptyLibrary;
    @FXML private StackPane braniSidebar;
    @FXML private ScrollPane playlistSidebar;
    @FXML private ListView<PlaylistModel> playlistSidebarList;
    @FXML private Button addButton;

    // CENTER — palco (ospita gli orb animati dello sfondo) ===========
    @FXML private StackPane stagePane;

    // RIGHT PANE — player ============================================
    @FXML private VBox playerCardEmpty;
    @FXML private VBox playerCardActive;
    @FXML private ImageView playerImage;
    @FXML private Label playerTitle;
    @FXML private Label playerArtist;
    @FXML private Label playerCurrentTime;
    @FXML private Label playerDurata;
    @FXML private Slider playerSlider;
    @FXML private FontIcon playPauseIcon;
    @FXML private FontIcon coverPlaceholder;
    @FXML private StackPane vinyl;          // disco che ruota durante la riproduzione
    private RotateTransition vinylSpin;     // animazione del vinile (giro continuo)
    @FXML private Button shuffleButton;
    @FXML private Button loopButton;        // loop del singolo brano (footer)
    // Stato dei controlli di riproduzione = unica fonte di verità.
    // asse ORDINE/auto-avanzamento: ciclo a 3 stati pilotato da onShuffle()
    //   OFF      = sequenziale, si ferma a fine brano  -> freccia grigia
    //   SEQ      = sequenziale, auto-avanza             -> freccia accesa
    //   SHUFFLE  = casuale, auto-avanza                 -> icona shuffle accesa
    private static final int ORDER_OFF = 0, ORDER_SEQ = 1, ORDER_SHUFFLE = 2;
    private int orderMode = ORDER_OFF;
    private boolean loopPlaylist = false;   // ripeti intera playlist (LoopStrat)
    private boolean loopSingle = false;     // ripeti brano corrente (LoopSingleStrat)
    // RIGHT PANE — playlist ==========================================
    @FXML private VBox playlistCardActive;
    @FXML private ImageView playlistImage;
    @FXML private Label playlistTitle;
    @FXML private Label playlistCount;
    @FXML private ListView<BranoModel> playlistSongsList;
    @FXML private Button playlistLoopButton;

    // INIZIALIZZAZIONE ===============================================

    @FXML
    public void initialize() {
        setupVinileEClip();
        setupGlow();
        bindPlayerUi();
        aggiornaVisualiControlli();   // stato di default (sequential): freccia accesa

        // --- libreria ---
        songListView.setItems(braniItems);
        songListView.setCellFactory(lv -> new BranoCell(lv,
                this::modifyBrano, this::addTrackToPlaylist, this::eliminaBrano));
        // click (non cambio selezione): funziona anche ri-cliccando l'elemento già selezionato
        songListView.setOnMouseClicked(e -> {
            BranoModel sel = songListView.getSelectionModel().getSelectedItem();
            if (sel == null) return;
            sel.incrementaAscolti();
            salvaTutto();
            refreshLibrary();
            refreshPlaylists();

            mostraPlayer(DataModel.getInstance().createIterator(sel));
            applicaStrategie();   // applica shuffle/loop correnti al nuovo iterator
        });
        // scorciatoia Ctrl/Cmd+Z → Undo
        songListView.sceneProperty().addListener((obs, oldScene, scene) -> {
            if (scene != null) {
                scene.getAccelerators().put(
                        new KeyCodeCombination(KeyCode.Z, KeyCombination.SHORTCUT_DOWN),
                        this::Undo);
            }
        });

        // --- sidebar playlist ---
        playlistSidebarList.setItems(playlistItems);
        playlistSidebarList.setCellFactory(lv -> new PlaylistCell(
                this::modifyPlaylist, this::infoPlaylist, this::eliminaPlaylist));
        playlistSidebarList.setOnMouseClicked(e -> {
            PlaylistModel sel = playlistSidebarList.getSelectionModel().getSelectedItem();
            if (sel != null) mostraPlaylist(sel);
        });

        // --- brani dentro la playlist aperta ---
        playlistSongsList.setCellFactory(lv -> new PlaylistSongCell(this::removeBranoFromPlaylist));
        playlistSongsList.setOnMouseClicked(e -> {
            BranoModel sel = playlistSongsList.getSelectionModel().getSelectedItem();
            if (sel == null || playlistVisualizzata == null) return;

            // conta l'ascolto sull'ISTANZA in libreria (dopo un reload può essere un
            // oggetto diverso da quello dentro la playlist): così risale tra i Brani
            BranoModel inLibreria = model.getBrani().stream()
                    .filter(x -> x.getId().equals(sel.getId()))
                    .findFirst().orElse(sel);
            inLibreria.incrementaAscolti();
            salvaTutto();
            refreshLibrary();    // DataModel.getBrani() riordina per ascolti -> sale in cima
            refreshPlaylists();

            // riproduzione
            player.play(playlistVisualizzata.createIterator(sel));
            applicaStrategie();   // applica shuffle/loop correnti al nuovo iterator
        });
    }

    /**
     * Crea due "orb" luminosi (cerchi sfocati con gradiente) dietro al contenuto
     * centrale e li anima con un lento movimento + "respiro". Insieme, sul fondo
     * scuro, generano l'effetto arcobaleno.
     */
    private void setupGlow() {
        if (stagePane == null) return;

        // confina gli orb (e il loro ingombro per i click) all'area centrale:
        // così non sforano sull'header rendendolo non cliccabile. La grafica non
        // cambia perché lo sforo era comunque coperto dai pannelli opachi.
        javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle();
        clip.widthProperty().bind(stagePane.widthProperty());
        clip.heightProperty().bind(stagePane.heightProperty());
        stagePane.setClip(clip);

        Circle orb1 = makeOrb(Color.web("#ff4ecd"), Color.web("#9d7bff"), 260);
        orb1.layoutXProperty().bind(stagePane.widthProperty().multiply(0.24));
        orb1.layoutYProperty().bind(stagePane.heightProperty().multiply(0.30));

        Circle orb2 = makeOrb(Color.web("#00e0ff"), Color.web("#32e6a0"), 280);
        orb2.layoutXProperty().bind(stagePane.widthProperty().multiply(0.78));
        orb2.layoutYProperty().bind(stagePane.heightProperty().multiply(0.74));

        // dietro alle card del player/playlist
        stagePane.getChildren().add(0, orb1);
        stagePane.getChildren().add(1, orb2);

        animateOrb(orb1, 70, 50, 9, 0.92, 1.18);
        animateOrb(orb2, -80, -60, 12, 1.12, 0.90);
    }

    /** Orb = cerchio con gradiente radiale (centro acceso → bordi trasparenti) e blur. */
    private Circle makeOrb(Color inner, Color mid, double radius) {
        Circle c = new Circle(radius);
        c.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                new Stop(0, inner),
                new Stop(0.5, Color.color(mid.getRed(), mid.getGreen(), mid.getBlue(), 0.55)),
                new Stop(1, Color.TRANSPARENT)));
        c.setEffect(new GaussianBlur(90));
        c.setOpacity(0.55);
        c.setManaged(false);         // non influenza il layout dello stage
        c.setMouseTransparent(true); // non intercetta i click
        return c;
    }

    /** Movimento lento (autoreverse) + "respiro" in scala, all'infinito. */
    private void animateOrb(Circle orb, double dx, double dy, double secs, double sFrom, double sTo) {
        TranslateTransition t = new TranslateTransition(javafx.util.Duration.seconds(secs), orb);
        t.setByX(dx);
        t.setByY(dy);
        t.setAutoReverse(true);
        t.setCycleCount(Animation.INDEFINITE);
        t.setInterpolator(Interpolator.EASE_BOTH);
        t.play();

        ScaleTransition s = new ScaleTransition(javafx.util.Duration.seconds(secs * 0.85), orb);
        s.setFromX(sFrom); s.setFromY(sFrom);
        s.setToX(sTo);     s.setToY(sTo);
        s.setAutoReverse(true);
        s.setCycleCount(Animation.INDEFINITE);
        s.setInterpolator(Interpolator.EASE_BOTH);
        s.play();
    }

    /** Clip circolare della copertina + animazione del vinile (in pausa di default). */
    private void setupVinileEClip() {
        Circle coverClip = new Circle();
        playerImage.layoutBoundsProperty().addListener((obs, oldB, newB) -> {
            double d = Math.min(newB.getWidth(), newB.getHeight());
            coverClip.setRadius(d / 2.0);
            coverClip.setCenterX(newB.getWidth() / 2.0);
            coverClip.setCenterY(newB.getHeight() / 2.0);
        });
        playerImage.setClip(coverClip);

        if (vinyl != null) {
            vinylSpin = new RotateTransition(javafx.util.Duration.seconds(6), vinyl);
            vinylSpin.setByAngle(360);
            vinylSpin.setCycleCount(Animation.INDEFINITE);
            vinylSpin.setInterpolator(Interpolator.LINEAR);
        }

        // tooltip col titolo completo quando il testo è troncato
        Tooltip titleTip = new Tooltip();
        titleTip.textProperty().bind(playerTitle.textProperty());
        Tooltip.install(playerTitle, titleTip);
    }


    private void selezionaBranoCorrente(BranoModel b) {
        if (b == null) {
            playlistSongsList.getSelectionModel().clearSelection();
            return;
        }
        // seleziona nella playlist aperta, solo se la contiene
        if (playlistSongsList.getItems().contains(b)) {
            playlistSongsList.getSelectionModel().select(b);
            playlistSongsList.scrollTo(b);
        }
        // (opzionale) stessa cosa per la libreria
        if (songListView.getItems().contains(b)) {
            songListView.getSelectionModel().select(b);
            songListView.scrollTo(b);
        }
    }

    /**
     * Lega la UI del player allo stato di {@link PlayerEngine}: da qui in poi
     * nessun handler tocca icone/slider/vinile, ci pensano questi binding.
     */
    private void bindPlayerUi() {
        // slider: l'utente trascina → seek; la posizione segue il player
        playerSlider.setOnMousePressed(e -> player.setUserSeeking(true));
        playerSlider.setOnMouseReleased(e -> {
            player.seek(playerSlider.getValue());
            player.setUserSeeking(false);
        });
        playerSlider.maxProperty().bind(player.duration);
        player.position.addListener((o, ol, v) -> playerSlider.setValue(v.doubleValue()));
        playerCurrentTime.textProperty().bind(Bindings.createStringBinding(
                () -> formatSeconds(player.position.get()), player.position));

        // icona play/pausa + vinile = SEMPRE funzione dello stato del player
        player.status.addListener((o, ol, s) -> {
            boolean playing = (s == MediaPlayer.Status.PLAYING);
            if (playPauseIcon != null)
                playPauseIcon.setIconLiteral(playing ? "fas-pause" : "fas-play");
            if (vinylSpin != null) {
                if (playing) vinylSpin.play();
                else vinylSpin.pause();
            }
        });

        // contenuto della card = SEMPRE funzione del brano corrente
        player.current.addListener((o, ol, b) -> {
            renderCurrent(b);
            selezionaBranoCorrente(b);
        } );
    }

    public void setModel(DataModel model) {
        this.model = model;
        refreshLibrary();
        refreshPlaylists();
        model.Attach(this);
    }

    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    // VISUALIZZAZIONE ================================================

    /** Mostra il pannello destro indicato, nascondendo gli altri. */
    private void showRightPane(Node toShow) {
        setShown(playerCardEmpty, toShow == playerCardEmpty);
        setShown(playerCardActive, toShow == playerCardActive);
        setShown(playlistCardActive, toShow == playlistCardActive);
    }

    public void mostraPlayer(PlayerIterator iterable) {
        player.play(iterable);
        if (player.current.get() == null) return;   // brano non valido: niente da mostrare
        showRightPane(playerCardActive);
    }

    /** Aggiorna il contenuto della card player (chiamato dal binding su current). */
    private void renderCurrent(BranoModel b) {
        if (b == null) return;
        // nuovo brano: vinile fermo e dritto (lo status lo riavvia se si sta riproducendo)
        if (vinylSpin != null) {
            vinylSpin.stop();
            if (vinyl != null) vinyl.setRotate(0);
        }
        playerTitle.setText(b.getTitolo());
        playerArtist.setText(b.getArtista());
        playerDurata.setText(b.getDurataFormattata());
        boolean hasArtwork = b.getPathImmaggine() != null && b.getPathImmaggine().toFile().exists();
        playerImage.setImage(hasArtwork ? new Image(b.getPathImmaggine().toUri().toString(), true) : null);
        coverPlaceholder.setVisible(!hasArtwork);
        coverPlaceholder.setManaged(!hasArtwork);
    }

    public void mostraPlaylist(PlaylistModel p) {
        this.playlistVisualizzata = p;
        showRightPane(playlistCardActive);

        playlistTitle.setText(p.getTitolo());
        playlistCount.setText(p.getBrani().size() + " brani");
        if (p.getPathImmagine() != null && p.getPathImmagine().toFile().exists())
            playlistImage.setImage(new Image(p.getPathImmagine().toUri().toString(), true));
        else
            playlistImage.setImage(null);

        playlistSongsList.setItems(FXCollections.observableArrayList(p.getBrani()));
    }

    public void refreshLibrary() {
        if (model == null || songListView == null) return;
        braniItems.setAll(model.getBrani());
        boolean hasBrani = !model.getBrani().isEmpty();
        setShown(songListView, hasBrani);
        setShown(emptyLibrary, !hasBrani);
    }

    public void refreshPlaylists() {
        if (model == null || playlistSidebarList == null) return;
        playlistItems.setAll(model.getPlaylists());
        playlistSidebarList.refresh();
    }

    // COMANDI (Command pattern) + PERSISTENZA ========================

    /** Esegue un comando e salva: unico punto, così nessun salvataggio resta indietro. */
    private void esegui(Command c) {
        commands.push(c);
        c.execute();
        salvaTutto();
    }

    private void salvaTutto() {
        if (storage == null || model == null) return;
        storage.SaveBrani(new ArrayList<>(model.getBrani()));
        storage.SavePlaylist(new ArrayList<>(model.getPlaylists()));
    }

    public void Undo() {
        if (commands.isEmpty()) return;
        commands.pop().undo();
        salvaTutto();
    }

    // AZIONI SU BRANI ================================================

    private void eliminaBrano(BranoModel brano) {
        if (!confermaEliminazione("Elimina brano",
                "Vuoi eliminare definitivamente il brano " + brano.getTitolo()
                        + "? Questa azione non può essere annullata.")) return;

        boolean eraInRiproduzione = (player.current.get() == brano);
        esegui(new RemoveBrano(brano, model));
        if (eraInRiproduzione) {
            player.stopAndClear();
            showRightPane(playerCardEmpty);
        }
    }

    private void modifyBrano(BranoModel brano) {
        AddTrackDialogController c = Dialogs.openModal1(ownerWindow(), "dialog-add-track.fxml",
                "Modifica brano", (AddTrackDialogController cc) -> cc.setBrano(brano));
        if (c.isConfirmed()) {
            model.updateBrani(brano);
            salvaTutto();
        }
    }

    private void addTrackToPlaylist(BranoModel brano) {
        Dialogs.openModal1(ownerWindow(), "dialog-add-to-playlist.fxml", "Aggiungi a una Playlist",
                (AddToPlaylistDialogController c) -> {
                    c.setModel(model);
                    c.addBranoToPlaylist(brano);
                });
        salvaTutto();
        refreshPlaylists();
        if (playlistVisualizzata != null) mostraPlaylist(playlistVisualizzata);
    }

    private void removeBranoFromPlaylist(BranoModel b) {
        if (playlistVisualizzata == null) return;
        esegui(new RemoveBranoFromPlaylist(b, playlistVisualizzata));
        refreshPlaylists();
        mostraPlaylist(playlistVisualizzata);   // ri-disegna brani + contatore
    }

    // AZIONI SU PLAYLIST =============================================

    private void eliminaPlaylist(PlaylistModel playlist) {
        if (!confermaEliminazione("Elimina playlist",
                "Vuoi eliminare definitivamente la playlist " + playlist.getTitolo()
                        + "? Questa azione non può essere annullata.")) return;
        esegui(new RemovePlaylist(playlist, model));
    }

    private void modifyPlaylist(PlaylistModel playlist) {
        AddPlaylistDialogController c = Dialogs.openModal1(ownerWindow(), "dialog-add-playlist.fxml",
                "Modifica playlist", (AddPlaylistDialogController cc) -> cc.setPlaylist(playlist));
        if (c.isConfirmed()) {
            model.updatePlaylist(playlist);
            salvaTutto();
            mostraPlaylist(playlist);
        }
    }

    private void infoPlaylist(PlaylistModel playlist) {
        // TODO: vista dettaglio della playlist
    }

    // HANDLER LEFT PANE ==============================================

    @FXML
    private void onAdd() {
        if (tabPlaylist.isSelected()) {
            AddPlaylistDialogController d = Dialogs.openModal(ownerWindow(),
                    "dialog-add-playlist.fxml", "Nuova playlist");
            if (d.isConfirmed()) esegui(new AddPlaylist(d.getResult(), model));
        } else {
            AddTrackDialogController d = Dialogs.openModal(ownerWindow(),
                    "dialog-add-track.fxml", "Aggiungi brano");
            if (d.isConfirmed()) esegui(new AddBrano(d.getResult(), model));
        }
    }

    /** Chiude l'applicazione ("spegni la musica"). I dati sono già salvati ad ogni azione. */
    @FXML
    private void onClose() {
        player.stopAndClear();              // ferma il MediaPlayer (thread non-daemon)
        if (vinylSpin != null) vinylSpin.stop();
        Platform.exit();                    // chiude le finestre JavaFX
        System.exit(0);                     // garantisce la fine del processo
    }

    @FXML
    private void onTabChanged() {
        boolean playlist = tabPlaylist.isSelected();

        // Gestione Sidebar (Sinistra)
        setShown(braniSidebar, !playlist);
        setShown(playlistSidebar, playlist);

        // Etichetta di sezione
        if (sectionLabel != null) {
            sectionLabel.setText(playlist ? "LE TUE PLAYLIST" : "LIBRERIA");
        }

        // GESTIONE PANNELLO DI DESTRA
        if (!playlist) {
            // Se l'utente clicca su "Brani", nascondiamo la schermata della playlist
            setShown(playlistCardActive, false);

            // Se c'è già un brano in coda/riproduzione (iterator non nullo), mostriamo il player attivo.
            // Altrimenti, mostriamo la schermata del player vuoto.
            boolean haBranoAttivo = ( player.hasIterator());
            setShown(playerCardActive, haBranoAttivo);
            setShown(playerCardEmpty, !haBranoAttivo);
        }
    }
    // HANDLER PLAYER (delegano solo allo stato; la UI si aggiorna da sola) ====

    @FXML private void onPlayPause() { player.togglePlay(); }

    @FXML private void onPrev()      { player.prev(); }

    @FXML private void onNext()      { player.next(); }

    @FXML
    private void onShuffle() {
        orderMode = (orderMode + 1) % 3;   // OFF -> SEQ -> SHUFFLE -> OFF
        applicaStrategie();
    }

    @FXML
    private void onPlaylistRepeat() {
        loopPlaylist = !loopPlaylist;
        if (loopPlaylist) loopSingle = false;   // i due loop sono mutuamente esclusivi
        applicaStrategie();
    }

    @FXML
    private void onRepeat() {
        loopSingle = !loopSingle;
        if (loopSingle) loopPlaylist = false;    // i due loop sono mutuamente esclusivi
        applicaStrategie();
    }

    /**
     * Unico punto che traduce lo stato dei controlli in strategie sull'iterator
     * e aggiorna le visuali dei bottoni. Ordine e continuazione sono due assi
     * indipendenti, quindi i bottoni non si "rubano" più lo stato a vicenda.
     */
    private void applicaStrategie() {
        if (player.hasIterator()) {
            // asse ORDINE
            player.setOrderStrat(orderMode == ORDER_SHUFFLE ? new ShuffleStrat() : new SequentialStrat());
            // asse CONTINUAZIONE (a fine brano): priorità loop-singolo > loop-playlist > auto-avanza > stop
            PlaybackStrat pb;
            if (loopSingle)              pb = new LoopSingleStrat();
            else if (loopPlaylist)       pb = new LoopStrat();
            else if (orderMode != ORDER_OFF) pb = new AutoPlayStrat();   // SEQ/SHUFFLE: avanza al brano successivo
            else                         pb = new NoAutoPlayStrat();     // OFF: a fine brano si ferma
            player.setPlaybackStrat(pb);
        }
        aggiornaVisualiControlli();
    }

    private void aggiornaVisualiControlli() {
        setActive(loopButton, loopSingle);
        setActive(playlistLoopButton, loopPlaylist);
        // ciclo a 3 stati: OFF = grigio (no is-active), SEQ = freccia accesa (is-active),
        // SHUFFLE = icona shuffle accesa (is-active + is-shuffle)
        setActive(shuffleButton, orderMode != ORDER_OFF);
        if (orderMode == ORDER_SHUFFLE) shuffleButton.getStyleClass().add("is-shuffle");
        else                            shuffleButton.getStyleClass().remove("is-shuffle");
    }

    private static void setActive(Button b, boolean active) {
        if (b == null) return;
        if (active) { if (!b.getStyleClass().contains("is-active")) b.getStyleClass().add("is-active"); }
        else        b.getStyleClass().remove("is-active");
    }

    // OBSERVER: i dati cambiano → la UI si ridisegna ================
    @Override
    public void Update(Observer.Events event, Object object) {
        switch (event) {
            case Events.BranoAdd, Events.BranoRemove, Events.BranoUpdate, Events.BraniChange -> refreshLibrary();
            case Events.PlaylistAdd, Events.PlaylistRemove, Events.PlaylistUpdate, Events.PlaylistsChange-> refreshPlaylists();
            default -> { /* evento non rilevante per questa vista */ }
        }
    }

    // UTILITY ========================================================

    private Window ownerWindow() {
        return addButton.getScene().getWindow();
    }

    private static void setShown(Node node, boolean shown) {
        if (node == null) return;
        node.setVisible(shown);
        node.setManaged(shown);
    }

    private static String formatSeconds(double seconds) {
        int sec = (int) seconds;
        return String.format("%d:%02d", sec / 60, sec % 60);
    }

    /** Apre il dialog di conferma eliminazione e ritorna true se l'utente conferma. */
    private boolean confermaEliminazione(String titolo, String messaggio) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/it/gruppo19/progetto_music_player/dialog-delete.fxml"));
            Parent root = loader.load();
            DeleteTrackDialogController controller = loader.getController();
            controller.setMainLabel(titolo);
            controller.setMessageLabel(messaggio);

            Stage dialog = new Stage();
            dialog.initOwner(ownerWindow());
            dialog.setScene(new Scene(root));
            dialog.showAndWait();
            return controller.hasDeleted();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
