package it.gruppo19.progetto_music_player.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test del modello Playlist: aggiunta/rimozione brani, no duplicati, conteggi.
 */
class PlaylistModelTest {

    private static BranoModel brano(String id) {
        return new BranoModel(id, "T" + id, "d", "a", "g", 2020, null, null);
    }

    private PlaylistModel playlistVuota() {
        return new PlaylistModel("p1", "Preferiti", "desc", null, new ArrayList<>());
    }

    @Test
    void addBranoAggiungeIlBrano() {
        PlaylistModel p = playlistVuota();
        BranoModel a = brano("a");
        p.addBrano(a);
        assertTrue(p.hasBrano(a));
        assertEquals(1, p.size());
    }

    @Test
    void addBranoNonAggiungeDuplicati() {
        PlaylistModel p = playlistVuota();
        BranoModel a = brano("a");
        p.addBrano(a);
        p.addBrano(a); // stesso oggetto
        assertEquals(1, p.size(), "lo stesso brano non deve essere aggiunto due volte");
    }

    @Test
    void removeBranoRimuoveERestituisceLIndice() {
        PlaylistModel p = playlistVuota();
        BranoModel a = brano("a");
        BranoModel b = brano("b");
        p.addBrano(a);
        p.addBrano(b);

        int index = p.removeBrano(b);
        assertEquals(1, index, "b era in posizione 1");
        assertFalse(p.hasBrano(b));
        assertEquals(1, p.size());
    }

    @Test
    void getAscoltiTotaliSommaGliAscoltiDeiBrani() {
        PlaylistModel p = playlistVuota();
        BranoModel a = brano("a");
        BranoModel b = brano("b");
        a.incrementaAscolti();              // 1
        b.incrementaAscolti();
        b.incrementaAscolti();              // 2
        p.addBrano(a);
        p.addBrano(b);
        assertEquals(3, p.getAscoltiTotali());
    }

    @Test
    void getBraniNonEModificabile() {
        PlaylistModel p = playlistVuota();
        p.addBrano(brano("a"));
        List<BranoModel> view = p.getBrani();
        assertThrows(UnsupportedOperationException.class, () -> view.add(brano("x")));
    }

    @Test
    void getBranoFuoriRangeRestituisceNull() {
        PlaylistModel p = playlistVuota();
        assertNull(p.getBrano(0));
        assertNull(p.getBrano(-1));
    }
}
