package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.iteratorPattern.PlayerIterator;
import it.gruppo19.progetto_music_player.model.strategyPattern.NoAutoPlayStrat;
import it.gruppo19.progetto_music_player.model.strategyPattern.OrderStrat;
import it.gruppo19.progetto_music_player.model.strategyPattern.PlaybackStrat;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

/**
 * Motore di riproduzione: unica fonte di verità per lo stato del player.
 *
 * Espone lo stato come Property osservabili ({@link #status}, {@link #current},
 * {@link #position}, {@link #duration}). La UI vi si lega una volta sola e si
 * aggiorna da sola: nessuno deve toccare a mano icone, slider o vinile.
 * Chi usa il motore si limita a cambiare lo stato con play/togglePlay/next/prev.
 */
public class PlayerEngine {

    /** Stato del MediaPlayer corrente (PLAYING, PAUSED, STOPPED, ...). */
    public final ObjectProperty<MediaPlayer.Status> status =
            new SimpleObjectProperty<>(MediaPlayer.Status.UNKNOWN);
    /** Brano attualmente caricato (null = nessuna riproduzione). */
    public final ObjectProperty<BranoModel> current = new SimpleObjectProperty<>();
    /** Posizione corrente in secondi. */
    public final DoubleProperty position = new SimpleDoubleProperty(0);
    /** Durata totale del brano corrente in secondi. */
    public final DoubleProperty duration = new SimpleDoubleProperty(0);

    private MediaPlayer mediaPlayer;
    private PlayerIterator iterator;
    private boolean userSeeking;

    // --- comandi di riproduzione ---------------------------------------

    /** Avvia la riproduzione su un nuovo iterator (es. play dalla libreria/playlist). */
    public void play(PlayerIterator it) {
        if (it != null) iterator = it;
        if (iterator == null) return;
        load(iterator.getCurrent());
    }

    /** Carica e (se previsto dalla strategy) avvia il brano indicato. */
    private void load(BranoModel b) {
        disposePlayer();
        position.set(0);
        duration.set(0);
        current.set(b);

        if (b == null || b.getPathAudio() == null || !b.getPathAudio().toFile().exists()) {
            status.set(MediaPlayer.Status.STOPPED);
            return;
        }

        Media media = new Media(b.getPathAudio().toUri().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnReady(() -> duration.set(media.getDuration().toSeconds()));
        // l'unico punto che propaga lo stato del player alla UI
        mediaPlayer.statusProperty().addListener((o, ol, s) -> status.set(s));
        mediaPlayer.currentTimeProperty().addListener((o, ol, t) -> {
            if (!userSeeking) position.set(t.toSeconds());
        });
        mediaPlayer.setOnEndOfMedia(this::onEndOfMedia);

        if (iterator != null && !(iterator.getPlaybackOrderStrat() instanceof NoAutoPlayStrat)) {
            mediaPlayer.play();
        }
    }

    private void onEndOfMedia() {
        if (iterator == null || !iterator.hasNext()) {
            if (mediaPlayer != null) mediaPlayer.stop();
            status.set(MediaPlayer.Status.STOPPED);
        } else {
            load(iterator.getNext());
        }
    }

    /** Play/pausa in base allo stato corrente. */
    public void togglePlay() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) mediaPlayer.pause();
        else mediaPlayer.play();
    }

    public void next() {
        if (iterator == null) return;
        load(iterator.getNext());
    }

    public void prev() {
        if (iterator == null) return;
        load(iterator.getPrevious());
    }

    public void seek(double seconds) {
        if (mediaPlayer != null) mediaPlayer.seek(Duration.seconds(seconds));
    }

    public void setUserSeeking(boolean seeking) { this.userSeeking = seeking; }

    /** Ferma e azzera la riproduzione (es. dopo aver eliminato il brano corrente). */
    public void stopAndClear() {
        disposePlayer();
        current.set(null);
        position.set(0);
        duration.set(0);
        status.set(MediaPlayer.Status.STOPPED);
    }

    /** Ferma la riproduzione solo se il brano corrente è {@code b}. */
    public void stopIfCurrent(BranoModel b) {
        if (current.get() == b) stopAndClear();
    }

    private void disposePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.dispose();
            mediaPlayer = null;
        }
    }

    // --- strategie (Strategy pattern) ----------------------------------

    public boolean hasIterator() { return iterator != null; }

    public PlaybackStrat getPlaybackStrat() {
        return iterator == null ? null : iterator.getPlaybackOrderStrat();
    }

    public OrderStrat getOrderStrat() {
        return iterator == null ? null : iterator.getOrderStrat();
    }

    public void setOrderStrat(OrderStrat strat) {
        if (iterator != null) iterator.setOrderStrat(strat);
    }

    public void setPlaybackStrat(PlaybackStrat strat) {
        if (iterator != null) iterator.setPlaybackStrat(strat);
    }
}
