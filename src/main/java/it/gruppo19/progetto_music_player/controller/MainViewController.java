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
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.binding.Bindings;
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
    private int shuffleState = 0;           // 0=stop, 1=loop sequenziale, 2=loop shuffle

    // RIGHT PANE — playlist ==========================================
    @FXML private VBox playlistCardActive;
    @FXML private ImageView playlistImage;
    @FXML private Label playlistTitle;
    @FXML private Label playlistCount;
    @FXML private ListView<BranoModel> playlistSongsList;

    // INIZIALIZZAZIONE ===============================================

    @FXML
    public void initialize() {
        setupVinileEClip();
        bindPlayerUi();

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

            // FIX: Creiamo l'iteratore e gli iniettiamo le strategie attualmente in uso nel player
            PlayerIterator nuovoIteratore = DataModel.getInstance().createIterator(sel);
            if (player.hasIterator()) {
                if (player.getPlaybackStrat() != null) nuovoIteratore.setPlaybackStrat(player.getPlaybackStrat());
                if (player.getOrderStrat() != null) nuovoIteratore.setOrderStrat(player.getOrderStrat());
            }
            mostraPlayer(nuovoIteratore);
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

            // FIX: Creiamo l'iteratore e gli iniettiamo le strategie attualmente in uso nel player
            PlayerIterator nuovoIteratore = playlistVisualizzata.createIterator(sel);
            if (player.hasIterator()) {
                if (player.getPlaybackStrat() != null) nuovoIteratore.setPlaybackStrat(player.getPlaybackStrat());
                if (player.getOrderStrat() != null) nuovoIteratore.setOrderStrat(player.getOrderStrat());
            }
            player.play(nuovoIteratore);
        });
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
        player.current.addListener((o, ol, b) -> renderCurrent(b));
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
        if (!player.hasIterator()) return;
        shuffleState = (shuffleState + 1) % 3;
        shuffleButton.getStyleClass().removeAll("is-active", "is-shuffle");
        switch (shuffleState) {
            case 0 -> {   // stop: nessun avanzamento automatico
                player.setOrderStrat(new SequentialStrat());
                player.setPlaybackStrat(new NoAutoPlayStrat());
            }
            case 1 -> {   // loop sequenziale
                shuffleButton.getStyleClass().add("is-active");
                player.setOrderStrat(new SequentialStrat());
                player.setPlaybackStrat(new AutoPlayStrat());
            }
            case 2 -> {   // loop casuale
                shuffleButton.getStyleClass().addAll("is-active", "is-shuffle");
                player.setOrderStrat(new ShuffleStrat());
                player.setPlaybackStrat(new AutoPlayStrat());
            }
        }
    }

    @FXML
    private void onRepeat() {
        if (!player.hasIterator()) return;
        PlaybackStrat s = player.getPlaybackStrat();
        if (s instanceof NoAutoPlayStrat)    player.setPlaybackStrat(new PlayOnceStrat());
        else if (s instanceof PlayOnceStrat) player.setPlaybackStrat(new LoopStrat());
        else                                 player.setPlaybackStrat(new NoAutoPlayStrat());
    }

    // OBSERVER: i dati cambiano → la UI si ridisegna ================
    @Override
    public void Update(String event, Object object) {
        switch (event) {
            case "BraniChange", "BranoAdd", "BranoRemove", "BranoUpdate" -> refreshLibrary();
            case "PlaylistChange", "PlaylistsChange", "PlaylistAdd",
                 "PlaylistRemove", "PlaylistUpdate" -> refreshPlaylists();
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
