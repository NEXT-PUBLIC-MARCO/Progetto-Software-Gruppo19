package it.gruppo19.progetto_music_player.controller.cells;

import it.gruppo19.progetto_music_player.model.PlaylistModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.function.Consumer;

/**
 * Cella della sidebar playlist: mostra la playlist-info-card per un
 * {@link PlaylistModel}. Le azioni (modifica, info, elimina) sono callback.
 */
public class PlaylistCell extends ListCell<PlaylistModel> {

    private Node card;
    private Label title, subtitle;
    private ImageView image;

    public PlaylistCell(Consumer<PlaylistModel> onEdit,
                        Consumer<PlaylistModel> onInfo,
                        Consumer<PlaylistModel> onDelete) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/it/gruppo19/progetto_music_player/playlist-info-card.fxml"));
            card = loader.load();

            title    = (Label) loader.getNamespace().get("titleLabel");
            subtitle = (Label) loader.getNamespace().get("subtitleLabel");
            image    = (ImageView) loader.getNamespace().get("cardImage");

            ((Button) loader.getNamespace().get("editButton"))
                    .setOnAction(e -> { if (getItem() != null) onEdit.accept(getItem()); });
            ((Button) loader.getNamespace().get("infoButton"))
                    .setOnAction(e -> { if (getItem() != null) onInfo.accept(getItem()); });
            ((Button) loader.getNamespace().get("deleteButton"))
                    .setOnAction(e -> { if (getItem() != null) onDelete.accept(getItem()); });
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
}
