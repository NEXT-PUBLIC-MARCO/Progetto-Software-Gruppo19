package it.gruppo19.progetto_music_player.model.iteratorPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.strategyPattern.AutoPlayStrat;
import it.gruppo19.progetto_music_player.model.strategyPattern.ShuffleStrat;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test dell'iterator sulla libreria. In particolare verifica il bug corretto:
 * getNext()/getPrevious() devono AGGIORNARE il brano corrente, non solo
 * restituirlo (altrimenti loop/shuffle/next non avanzano mai).
 */
class TuttiBraniIteratorTest {

    private static BranoModel brano(String id) {
        return new BranoModel(id, "T" + id, "d", "a", "g", 2020, null, null);
    }

    private final BranoModel a = brano("a");
    private final BranoModel b = brano("b");
    private final BranoModel c = brano("c");

    private TuttiBraniIterator iterator() {
        return new TuttiBraniIterator(new ArrayList<>(List.of(a, b, c)), a);
    }

    @Test
    void currentIniziaDalBranoPassatoAlCostruttore() {
        assertEquals(a, iterator().getCurrent());
    }

    @Test
    void getNextAggiornaIlBranoCorrente() {   // <-- regressione del bug
        TuttiBraniIterator it = iterator();
        assertEquals(b, it.getNext());
        assertEquals(b, it.getCurrent(), "getNext deve spostare il corrente, non solo restituirlo");
        assertEquals(c, it.getNext());
        assertEquals(c, it.getCurrent());
    }

    @Test
    void getPreviousAggiornaIlBranoCorrenteEWrap() {
        TuttiBraniIterator it = iterator();
        assertEquals(c, it.getPrevious(), "dal primo si torna all'ultimo");
        assertEquals(c, it.getCurrent());
    }

    @Test
    void setOrderStratNonPerdeNeAggiungeBrani() {
        TuttiBraniIterator it = iterator();
        it.setPlaybackStrat(new AutoPlayStrat()); // così getNext usa l'avanzamento di default
        it.setOrderStrat(new ShuffleStrat());

        // raccolgo i brani visitati: devono essere esattamente {a,b,c}
        List<BranoModel> visti = new ArrayList<>();
        visti.add(it.getCurrent());
        for (int i = 0; i < 2; i++) visti.add(it.getNext());

        assertTrue(visti.containsAll(List.of(a, b, c)),
                "dopo lo shuffle devono restare gli stessi brani");
    }

    @Test
    void updateBranoRemoveSpostaIlCorrenteSeRimossoQuelloInRiproduzione() {
        TuttiBraniIterator it = iterator(); // current = a
        it.Update("BranoRemove", a);
        assertNotEquals(a, it.getCurrent(),
                "rimuovendo il brano corrente, l'iterator deve spostarsi su un altro");
    }
}
