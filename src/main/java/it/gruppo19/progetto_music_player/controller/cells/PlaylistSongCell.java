package it.gruppo19.progetto_music_player.controller.cells;

import it.gruppo19.progetto_music_player.model.BranoModel;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.function.Consumer;

/**
 * Cella di un brano dentro una playlist: titolo, artista·durata e cestino.
 * L'eliminazione è una callback; il click per riprodurre è gestito a livello
 * di ListView (così non serve toccare la cella per il play).
 */
public class PlaylistSongCell extends ListCell<BranoModel> {

    private final Label t = new Label();
    private final Label s = new Label();
    private final Button delete = new Button();
    private final VBox texts = new VBox(2, t, s);
    private final Region spacer = new Region();
    private final HBox box = new HBox(8, texts, spacer, delete);

    public PlaylistSongCell(Consumer<BranoModel> onDelete) {
        t.getStyleClass().add("track-title");
        s.getStyleClass().add("track-artist");
        delete.getStyleClass().add("track-delete");
        delete.setGraphic(new FontIcon("fas-trash"));
        ((FontIcon) delete.getGraphic()).setIconSize(16);

        HBox.setHgrow(texts, Priority.ALWAYS);
        HBox.setHgrow(spacer, Priority.ALWAYS);
        box.setAlignment(Pos.CENTER_LEFT);

        // impostato UNA volta: consuma l'evento così il click non avvia la riproduzione
        delete.setOnAction(e -> {
            if (getItem() != null) {
                onDelete.accept(getItem());
                e.consume();
            }
        });
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
    }
}
