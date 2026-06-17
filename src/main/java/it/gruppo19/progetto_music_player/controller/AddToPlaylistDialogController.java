package it.gruppo19.progetto_music_player.controller;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.model.PlaylistModel;

import java.util.ArrayList;
import java.util.List;

public class AddToPlaylistDialogController {

    @FXML private Label trackLabel;
    @FXML private VBox playlistList;

    private DataModel model;
    private BranoModel brano;
    private List<PlaylistModel> selectedPlaylists = new ArrayList<>();

    public void setModel(DataModel model) {
        this.model = model;
    }

    public void addBranoToPlaylist(BranoModel brano) {
        this.brano = brano;
        trackLabel.setText(brano.getTitolo() + " · " + brano.getArtista());
        populatePlaylistList();
    }

    private void populatePlaylistList() {
        playlistList.getChildren().clear();

        for (PlaylistModel playlist : model.getPlaylists()) {
            HBox row = createPlaylistRow(playlist);
            playlistList.getChildren().add(row);
        }
    }

    private HBox createPlaylistRow(PlaylistModel playlist) {
        HBox row = new HBox(8.0);
        row.setAlignment(Pos.CENTER_LEFT);

        ImageView imageView = new ImageView();
        imageView.setFitHeight(48.0);
        imageView.setFitWidth(48.0);
        imageView.setPreserveRatio(true);

        if (playlist.getPathImmagine() != null && playlist.getPathImmagine().toFile().exists()) {
            imageView.setImage(new Image(playlist.getPathImmagine().toUri().toString()));
        }

        Label titleLabel = new Label(playlist.getTitolo());
        titleLabel.setStyle("-fx-font-size: 14; -fx-font-weight: bold; -fx-text-fill: white;");
        HBox.setHgrow(titleLabel, javafx.scene.layout.Priority.ALWAYS);

        CheckBox checkBox = new CheckBox();
        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                selectedPlaylists.add(playlist);
            } else {
                selectedPlaylists.remove(playlist);
            }
        });

        row.getChildren().addAll(imageView, titleLabel, checkBox);
        return row;
    }

    @FXML
    public void onConfirm() {
        for (PlaylistModel playlist : selectedPlaylists) {
            playlist.addBrano(brano);
            model.updatePlaylist(playlist);
        }
        closeDialog();
    }

    @FXML
    private void onCancel() {
        closeDialog();
    }

    private void closeDialog() {
        playlistList.getScene().getWindow().hide();
    }
}

