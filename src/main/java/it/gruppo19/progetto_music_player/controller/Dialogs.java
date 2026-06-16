package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.MusicPlayerApplication;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Helper per aprire i popup/dialog come finestre modali.
 * Carica l'FXML, mostra lo stage in modo bloccante e restituisce il controller,
 * cosi' il chiamante puo' leggere l'esito (es. isConfirmed()).
 */
public final class Dialogs {

    private Dialogs() {}

    /**
     * Apre {@code resource} come dialog modale figlio di {@code owner}.
     *
     * @return il controller dell'FXML caricato (per leggere il risultato)
     */
    public static <T> T openModal(Window owner, String resource, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(MusicPlayerApplication.class.getResource(resource));
            Parent root = loader.load();

            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.TRANSPARENT);   // niente barra di Windows
            dialog.initModality(Modality.WINDOW_MODAL);
            if (owner != null) {
                dialog.initOwner(owner);
            }
            dialog.setTitle(title);
            dialog.setResizable(false);
            Scene scene = new Scene(root, Color.TRANSPARENT);   // sfondo trasparente → card arrotondata "galleggia"
            dialog.setScene(scene);
            dialog.showAndWait();

            return loader.getController();
        } catch (IOException e) {
            throw new UncheckedIOException("Impossibile caricare il dialog: " + resource, e);
        }
    }

    public static <T> T openModal1(Window owner, String resource, String title, java.util.function.Consumer<T> init) {
        try {
            FXMLLoader loader = new FXMLLoader(MusicPlayerApplication.class.getResource(resource));
            Parent root = loader.load();
            T controller = loader.getController();   // ① prendi il controller
            init.accept(controller);

            Stage dialog = new Stage();
            dialog.initStyle(StageStyle.TRANSPARENT);   // niente barra di Windows
            dialog.initModality(Modality.WINDOW_MODAL);
            if (owner != null) {
                dialog.initOwner(owner);
            }
            dialog.setTitle(title);
            dialog.setResizable(false);
            Scene scene = new Scene(root, Color.TRANSPARENT);   // sfondo trasparente → card arrotondata "galleggia"
            dialog.setScene(scene);
            dialog.showAndWait();

            return controller;
        } catch (IOException e) {
            throw new UncheckedIOException("Impossibile caricare il dialog: " + resource, e);
        }
    }
}
