package it.gruppo19.progetto_music_player.model.strategyPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test delle strategie d'ordine (sequenziale / shuffle).
 */
class OrderStratTest {

    private static BranoModel brano(String id) {
        return new BranoModel(id, "T" + id, "d", "a", "g", 2020, null, null);
    }

    private final List<BranoModel> tracks =
            List.of(brano("a"), brano("b"), brano("c"), brano("d"));

    @Test
    void sequentialMantieneLOrdine() {
        List<BranoModel> out = new SequentialStrat().setBrani(tracks);
        assertEquals(tracks, out);
    }

    @Test
    void sequentialRestituisceUnaCopia() {
        List<BranoModel> out = new SequentialStrat().setBrani(tracks);
        assertNotSame(tracks, out, "deve essere una nuova lista, non la stessa referenza");
    }

    @Test
    void shuffleMantieneGliStessiElementi() {
        List<BranoModel> out = new ShuffleStrat().setBrani(tracks);
        assertEquals(tracks.size(), out.size());
        assertTrue(out.containsAll(tracks), "lo shuffle non deve perdere brani");
        assertTrue(tracks.containsAll(out), "lo shuffle non deve aggiungere brani");
    }

    @Test
    void shuffleNonModificaLaListaOriginale() {
        List<BranoModel> originale = new ArrayList<>(tracks);
        new ShuffleStrat().setBrani(tracks);
        assertEquals(originale, tracks);
    }
}
