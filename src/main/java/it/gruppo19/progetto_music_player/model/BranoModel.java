package it.gruppo19.progetto_music_player.model;
import java.io.Serializable;
public class BranoModel implements Serializable {
    private String id;
    private String titolo;
    private String descrizione;
    private String artista;
    private String genere;
    private String pathImmaggine;
    private String pathAudio;

    public BranoModel(String id, String titolo, String descrizione, String artista, String genere, String pathImmaggine, String pathAudio) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.artista = artista;
        this.genere = genere;
        this.pathImmaggine = pathImmaggine;
        this.pathAudio = pathAudio;
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

    public String getArtista() {
        return artista;
    }

    public void setArtista(String artista) {
        this.artista = artista;
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public String getPathImmaggine() {
        return pathImmaggine;
    }

    public void setPathImmaggine(String pathImmaggine) {
        this.pathImmaggine = pathImmaggine;
    }

    public String getPathAudio() {
        return pathAudio;
    }

    public void setPathAudio(String pathAudio) {
        this.pathAudio = pathAudio;
    }
}
