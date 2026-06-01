package it.gruppo19.progetto_music_player.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PlaylistModel implements  Serializable {
    private String id;
    private String titolo;
    private String descrizione;
    private String pathImmagine;
    private  List<BranoModel> brani;

    public PlaylistModel(String id, String titolo, String descrizione, String pathImmagine, List<BranoModel> brani) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.pathImmagine = pathImmagine;
        this.brani = new ArrayList<>(); //si potrebbe cambiare in Queue
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
        return brani;
    }

    //Metodo per aggiungere brani alla lista in un Arraylist, possibile cambiamento in coda
    public void addBrani(BranoModel b){
        brani.add(b);
    }
    public void removeBrani (BranoModel b){
        brani.remove(b);
    }
}
