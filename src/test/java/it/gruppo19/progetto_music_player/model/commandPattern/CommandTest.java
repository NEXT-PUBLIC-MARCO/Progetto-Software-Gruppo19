package it.gruppo19.progetto_music_player.model.commandPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.model.PlaylistModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test dei Command: ogni execute() deve essere annullabile da undo().
 * DataModel è un singleton: lo azzero via reflection prima di ogni test
 * così ogni metodo parte da un modello pulito.
 */
class CommandTest {

    private DataModel model;

    private static BranoModel brano(String id) {
        return new BranoModel(id, "T" + id, "d", "a", "g", 2020, null, null);
    }

    private static PlaylistModel playlist(String id) {
        return new PlaylistModel(id, "PL" + id, "desc", null, new ArrayList<>());
    }

    @BeforeEach
    void resetSingleton() throws Exception {
        Field f = DataModel.class.getDeclaredField("instance");
        f.setAccessible(true);
        f.set(null, null);
        model = new DataModel(new ArrayList<>(), new ArrayList<>());
    }

    // --- comandi sulla playlist (non toccano il DataModel) ---

    @Test
    void addBranoToPlaylistEseguiEAnnulla() {
        PlaylistModel p = playlist("1");
        BranoModel a = brano("a");
        Command cmd = new AddBranoToPlaylist(a, p);

        cmd.execute();
        assertTrue(p.hasBrano(a));

        cmd.undo();
        assertFalse(p.hasBrano(a));
    }

    @Test
    void removeBranoFromPlaylistEseguiEAnnulla() {
        PlaylistModel p = playlist("1");
        BranoModel a = brano("a");
        p.addBrano(a);
        Command cmd = new RemoveBranoFromPlaylist(a, p);

        cmd.execute();
        assertFalse(p.hasBrano(a));

        cmd.undo();
        assertTrue(p.hasBrano(a), "undo deve ripristinare il brano nella playlist");
    }

    // --- comandi sul DataModel ---

    @Test
    void addBranoEseguiEAnnulla() {
        BranoModel a = brano("a");
        Command cmd = new AddBrano(a, model);

        cmd.execute();
        assertTrue(model.getBrani().contains(a));

        cmd.undo();
        assertFalse(model.getBrani().contains(a));
    }

    @Test
    void addPlaylistEseguiEAnnulla() {
        PlaylistModel p = playlist("1");
        Command cmd = new AddPlaylist(p, model);

        cmd.execute();
        assertTrue(model.getPlaylists().contains(p));

        cmd.undo();
        assertFalse(model.getPlaylists().contains(p));
    }

    @Test
    void removeBranoRimuoveDaModelloEPlaylistEUndoRipristina() {
        BranoModel a = brano("a");
        PlaylistModel p = playlist("1");
        model.addBrani(a);
        model.addPlaylist(p);
        p.addBrano(a);

        Command cmd = new RemoveBrano(a, model);

        cmd.execute();
        assertFalse(model.getBrani().contains(a));
        assertFalse(p.hasBrano(a), "rimuovendo il brano deve sparire anche dalle playlist");

        cmd.undo();
        assertTrue(model.getBrani().contains(a));
        assertTrue(p.hasBrano(a), "undo deve ripristinare il brano anche nella playlist");
    }
}
