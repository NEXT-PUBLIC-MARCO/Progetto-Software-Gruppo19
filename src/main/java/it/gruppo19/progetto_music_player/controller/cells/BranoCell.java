package it.gruppo19.progetto_music_player.controller.cells;

import it.gruppo19.progetto_music_player.model.BranoModel;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * Cella della libreria: mostra la track-info-card per un {@link BranoModel}.
 * Le azioni (modifica, aggiungi a playlist, elimina) sono passate dal chiamante
 * come callback, così la cella non dipende dal controller.
 */
public class BranoCell extends ListCell<BranoModel> {

    private Node card;
    private Label title, subtitle, anno, durata, tagFav, tagNew, tagExp;
    private ImageView image;

    public BranoCell(ListView<BranoModel> lv,
                     Consumer<BranoModel> onEdit,
                     Consumer<BranoModel> onAddToPlaylist,
                     Consumer<BranoModel> onDelete) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/it/gruppo19/progetto_music_player/track-info-card.fxml"));
            card = loader.load();

            // la cella non impone larghezza propria e la card non supera la ListView:
            // così i Label troncano con "..." invece di sforare a destra
            setPrefWidth(0);
            ((VBox) card).maxWidthProperty().bind(lv.widthProperty().subtract(30));

            title    = (Label) loader.getNamespace().get("titleLabel");
            subtitle = (Label) loader.getNamespace().get("subtitleLabel");
            anno     = (Label) loader.getNamespace().get("annoLabel");
            image    = (ImageView) loader.getNamespace().get("cardImage");
            durata   = (Label) loader.getNamespace().get("durataLabel");
            tagFav   = (Label) loader.getNamespace().get("tagFavourite");
            tagNew   = (Label) loader.getNamespace().get("tagNewRelease");
            tagExp   = (Label) loader.getNamespace().get("tagExplicit");

            // handler impostati UNA volta: usano getItem() = brano corrente della cella
            ((Button) loader.getNamespace().get("editButton"))
                    .setOnAction(e -> { if (getItem() != null) onEdit.accept(getItem()); });
            ((Button) loader.getNamespace().get("addToPlaylistButton"))
                    .setOnAction(e -> { if (getItem() != null) onAddToPlaylist.accept(getItem()); });
            ((Button) loader.getNamespace().get("deleteButton"))
                    .setOnAction(e -> { if (getItem() != null) onDelete.accept(getItem()); });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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
        setShown(tagFav, brano.isFavourite());
        setShown(tagNew, brano.isNewRelease());
        setShown(tagExp, brano.isExplicit());

        if (brano.getPathImmaggine() != null && brano.getPathImmaggine().toFile().exists())
            image.setImage(new Image(brano.getPathImmaggine().toUri().toString(), true));
        else
            image.setImage(null);   // evita che resti l'immagine del brano precedente (riciclo)

        setGraphic(card);
    }

    private static void setShown(Node node, boolean shown) {
        if (node == null) return;
        node.setVisible(shown);
        node.setManaged(shown);
    }
}
