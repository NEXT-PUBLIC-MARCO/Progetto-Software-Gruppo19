package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.model.PlaylistModel;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
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

    @FXML private Label sectionLabel;

    @FXML private VBox songList;

    // --- vista Brani ---
    @FXML private StackPane braniSidebar;
    @FXML private VBox playerCard;

    // --- vista Playlist---
    @FXML private ScrollPane playlistSidebar;
    @FXML private VBox playlistSidebarList;

    @FXML private Button addButton;

    /** Il model condiviso, iniettato da RealMainController dopo il load dell'FXML. */
    private DataModel model;

    /**
     * Id della playlist attualmente espansa nella sidebar (accordion).
     * null = nessuna espansa. Cliccando una playlist gia' espansa la si richiude.
     */
    private String expandedPlaylistId;

    public void setModel(DataModel model) {
        this.model = model;
        // appena ho il model posso disegnare le liste iniziali
        refreshLibrary();
        refreshPlaylists();
    }


    /** Ridisegna la lista in base al contenuto del model (stub da completare). */
    private void refreshLibrary() {
        if (model == null) return;
        // TODO: svuotare songList e ricostruire una riga per ogni model.getBrani()
        //       poi mostrare/nascondere l'empty-state di conseguenza.
    }

    /**
     * Ricostruisce la sidebar delle playlist: una riga (box) per ogni playlist
     * del model, seguita dalla riga "Nuova playlist". La playlist con id
     * {@link #expandedPlaylistId} viene mostrata espansa con l'elenco dei brani.
     */
    private void refreshPlaylists() {
        if (model == null || playlistSidebarList == null) return;
        playlistSidebarList.getChildren().clear();

        for (PlaylistModel pl : model.getPlaylists()) {
            playlistSidebarList.getChildren().add(buildPlaylistEntry(pl));
        }

        playlistSidebarList.getChildren().add(buildNewPlaylistRow());
    }

    /**
     * Costruisce il box di una playlist: header cliccabile (titolo + n. brani)
     * e, se la playlist e' quella espansa, l'elenco in-line dei suoi brani.
     */
    private Node buildPlaylistEntry(PlaylistModel pl) {
        boolean expanded = pl.getId() != null && pl.getId().equals(expandedPlaylistId);

        // --- header cliccabile ---
        HBox header = new HBox(14.0);
        header.getStyleClass().add("song-row");
        if (expanded) header.getStyleClass().add("song-row-selected");
        header.setAlignment(Pos.CENTER_LEFT);

        Region thumb = new Region();
        thumb.getStyleClass().add("pl-thumb");
        thumb.setStyle("-fx-min-width:52; -fx-min-height:52;");

        VBox meta = new VBox(1.0);
        meta.getStyleClass().add("song-meta");
        HBox.setHgrow(meta, Priority.ALWAYS);
        Label title = new Label(pl.getTitolo());
        title.getStyleClass().add("song-title");
        if (expanded) title.getStyleClass().add("song-title-active");
        Label count = new Label(pl.size() + (pl.size() == 1 ? " brano" : " brani"));
        count.getStyleClass().add("song-artist");
        meta.getChildren().addAll(title, count);

        Label chevron = new Label(expanded ? "⌄" : "›");
        chevron.getStyleClass().add("song-artist");

        header.getChildren().addAll(thumb, meta, chevron);
        header.setOnMouseClicked(e -> {
            // toggle accordion: se gia' aperta la chiudo, altrimenti la apro
            expandedPlaylistId = expanded ? null : pl.getId();
            refreshPlaylists();
        });

        VBox entry = new VBox(2.0);
        entry.getChildren().add(header);

        if (expanded) {
            entry.getChildren().add(buildTrackList(pl));
        }
        return entry;
    }

    /** Elenco in-line dei brani della playlist (mostrato quando espansa). */
    private VBox buildTrackList(PlaylistModel pl) {
        VBox list = new VBox(0.0);
        list.setPadding(new Insets(4.0, 12.0, 8.0, 20.0));

        if (pl.size() == 0) {
            Label empty = new Label("Nessun brano in questa playlist");
            empty.getStyleClass().add("song-artist");
            list.getChildren().add(empty);
            return list;
        }

        int i = 1;
        for (BranoModel b : pl.getBrani()) {
            HBox row = new HBox();
            row.getStyleClass().add("pl-track-row");
            row.setAlignment(Pos.CENTER_LEFT);

            Label index = new Label(Integer.toString(i++));
            index.getStyleClass().add("track-index");

            Region t = new Region();
            t.getStyleClass().add("track-thumb");

            VBox m = new VBox(1.0);
            HBox.setHgrow(m, Priority.ALWAYS);
            Label tt = new Label(b.getTitolo());
            tt.getStyleClass().add("song-title");
            Label ar = new Label(b.getArtista());
            ar.getStyleClass().add("song-artist");
            m.getChildren().addAll(tt, ar);

            Button del = new Button("🗑");
            del.getStyleClass().add("icon-btn");
            del.setOnAction(e -> {
                pl.removeBrano(b);
                refreshPlaylists();
            });

            row.getChildren().addAll(index, t, m, del);
            list.getChildren().add(row);
        }
        return list;
    }

    /** Riga in fondo alla sidebar per creare una nuova playlist. */
    private Node buildNewPlaylistRow() {
        HBox row = new HBox(14.0);
        row.getStyleClass().add("song-row");
        row.setAlignment(Pos.CENTER_LEFT);

        StackPane thumb = new StackPane();
        thumb.getStyleClass().add("pl-thumb");
        thumb.setStyle("-fx-min-width:52; -fx-min-height:52;");
        Label plus = new Label("+");
        plus.getStyleClass().add("empty-icon-glyph");
        thumb.getChildren().add(plus);

        VBox meta = new VBox(1.0);
        meta.getStyleClass().add("song-meta");
        HBox.setHgrow(meta, Priority.ALWAYS);
        Label title = new Label("Nuova playlist");
        title.getStyleClass().add("song-title");
        Label sub = new Label("Vuota");
        sub.getStyleClass().add("song-artist");
        meta.getChildren().addAll(title, sub);

        row.getChildren().addAll(thumb, meta);
        row.setOnMouseClicked(e -> onNewPlaylist());
        return row;
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

    /**
     * Alterna le due viste integrate (Brani / Playlist) in base alla tab
     * selezionata: aggiorna la sidebar e l'etichetta di sezione. Il dettaglio
     * della playlist ora vive nella sidebar (accordion), non nel pannello di
     * destra, quindi quest'ultimo resta sul player.
     */
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

    /** Mostra o nasconde un nodo togliendolo anche dal layout quando nascosto. */
    private void setShown(Node node, boolean shown) {
        if (node == null) return;
        node.setVisible(shown);
        node.setManaged(shown);
    }

    @FXML
    private void onRemoveFromPlaylist(){

    }

    @FXML
    private void onAddTrackToPlaylist(){

    }

    /** Apre il dialog "Nuova playlist" e aggiunge la playlist creata al model. */
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
}
