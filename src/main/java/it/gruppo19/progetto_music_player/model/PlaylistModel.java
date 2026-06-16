package it.gruppo19.progetto_music_player.model;

import it.gruppo19.progetto_music_player.model.iteratorPattern.PlayerIterable;
import it.gruppo19.progetto_music_player.model.iteratorPattern.PlayerIterator;
import it.gruppo19.progetto_music_player.model.iteratorPattern.PlaylistIterator;
import it.gruppo19.progetto_music_player.model.observerPattern.Observable;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//import java.nio.file.Path;

public class PlaylistModel implements Serializable, PlayerIterable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String titolo;
    private String descrizione;
    private transient Path pathImmagine;
    private final List<BranoModel> brani;

    public PlaylistModel(String id, String titolo, String descrizione, Path pathImmagine, List<BranoModel> brani) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.pathImmagine = pathImmagine;
        this.brani = new ArrayList<>();

        if (brani != null) {
            this.brani.addAll(brani);
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    public Path getPathImmagine() {
        return pathImmagine;
    }

    public void setPathImmagine(Path pathImmagine) {
        this.pathImmagine = pathImmagine;
    }

    public List<BranoModel> getBrani() {
        return Collections.unmodifiableList(brani);
    }

    public void addBrano(BranoModel b) {
        if (b != null && !brani.contains(b)) {
            brani.add(b);
        }
    }
    public void addBrano(BranoModel b, int index) {
        if (b != null && !brani.contains(b)) {
            brani.add(index, b);
        }
    }

    public int removeBrano(BranoModel b) {
        int index = brani.indexOf(b);
        brani.remove(b);
        return index;
    }

    public int size() {
        return brani.size();
    }

    public BranoModel getBrano(int index) {
        if (index < 0 || index >= brani.size()) {
            return null;
        }
        return brani.get(index);
    }

    public int getAscoltiTotali() {
        int somma = 0;
        for (BranoModel b : brani) {
            somma += b.getAscolti();
        }
        return somma;
    }

    //Chiamato da ObjectOutputStream durante writeObject
    @Serial
    private void writeObject(ObjectOutputStream obj) throws IOException {
        obj.defaultWriteObject();
        obj.writeObject(pathImmagine == null ? null : pathImmagine.toString());

    }

    //Chiamato da ObjectOutputStream durante writeObject
    @Serial
    private void readObject(ObjectInputStream obj) throws  IOException, ClassNotFoundException {
        obj.defaultReadObject();
        String a = (String) obj.readObject();
        this.pathImmagine = (a == null ? null : Path.of(a));


    }

    @Override
    public PlayerIterator createIterator(DataModel model) {
        PlaylistIterator iterator = new PlaylistIterator(this);
        model.Attach(iterator);
        return iterator;
    }
}