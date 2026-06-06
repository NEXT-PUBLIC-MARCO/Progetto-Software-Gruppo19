package it.gruppo19.progetto_music_player.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
//import java.nio.file.Path;

public class PlaylistModel implements Serializable {
    private String id;
    private String titolo;
    private String descrizione;
    private String pathImmagine; //Non converrebbe usare Path anche qui come per i brani?
    private final List<BranoModel> brani;

    public PlaylistModel(String id, String titolo, String descrizione, String pathImmagine, List<BranoModel> brani) {
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

    public String getPathImmagine() {
        return pathImmagine;
    }

    public void setPathImmagine(String pathImmagine) {
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

    public void removeBrano(BranoModel b) {
        brani.remove(b);
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
}