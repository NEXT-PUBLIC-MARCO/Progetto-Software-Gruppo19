package it.gruppo19.progetto_music_player.model.strategyPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test delle strategie di riproduzione (cosa succede a fine brano).
 */
class PlaybackStratTest {

    private static BranoModel brano(String id) {
        return new BranoModel(id, "T" + id, "d", "a", "g", 2020, null, null);
    }

    private final BranoModel a = brano("a");
    private final BranoModel b = brano("b");
    private final BranoModel c = brano("c");
    private final List<BranoModel> tracks = List.of(a, b, c);

    // --- getNext/getPrevious di default (usati da AutoPlayStrat) ---

    @Test
    void defaultGetNextAvanzaInSequenza() {
        PlaybackStrat s = new AutoPlayStrat();
        assertEquals(b, s.getNext(tracks, a));
        assertEquals(c, s.getNext(tracks, b));
    }

    @Test
    void defaultGetNextRiparteDopoLUltimo() {
        PlaybackStrat s = new AutoPlayStrat();
        assertEquals(a, s.getNext(tracks, c)); // wrap-around
    }

    @Test
    void defaultGetPreviousTornaIndietroEWrap() {
        PlaybackStrat s = new AutoPlayStrat();
        assertEquals(a, s.getPrevious(tracks, b));
        assertEquals(c, s.getPrevious(tracks, a)); // wrap-around
    }

    // --- hasNext per ogni strategia ---

    @Test
    void noAutoPlayNonHaMaiUnSuccessivo() {
        assertFalse(new NoAutoPlayStrat().hasNext(tracks, a));
    }

    @Test
    void autoPlayHaSempreUnSuccessivo() {
        assertTrue(new AutoPlayStrat().hasNext(tracks, a));
    }

    @Test
    void playOnceHaSuccessivoSoloSeNonEUltimo() {
        PlaybackStrat s = new PlayOnceStrat();
        assertTrue(s.hasNext(tracks, a));
        assertTrue(s.hasNext(tracks, b));
        assertFalse(s.hasNext(tracks, c)); // c è l'ultimo
    }

    // --- loop singolo brano ---

    @Test
    void loopSingleRipeteSempreLoStessoBrano() {
        PlaybackStrat s = new LoopSingleStrat();
        assertEquals(b, s.getNext(tracks, b));
        assertEquals(b, s.getPrevious(tracks, b));
    }

    @Test
    void loopSingleHaSuccessivoSoloConBranoCorrente() {
        PlaybackStrat s = new LoopSingleStrat();
        assertTrue(s.hasNext(tracks, a));
        assertFalse(s.hasNext(tracks, null));
    }

    // --- loop intera playlist ---

    @Test
    void loopPlaylistAvanzaEWrap() {
        PlaybackStrat s = new LoopStrat();
        assertEquals(a, s.getNext(tracks, c)); // riparte dal primo
        assertTrue(s.hasNext(tracks, c));
    }
}
