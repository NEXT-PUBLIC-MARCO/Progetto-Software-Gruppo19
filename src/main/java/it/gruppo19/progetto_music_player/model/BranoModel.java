package it.gruppo19.progetto_music_player.model;
import java.io.Serializable;
import java.nio.file.Path;

public class BranoModel implements Serializable {
    private String id;
    private String titolo;
    private String descrizione;
    private String artista;
    private String genere;
    private Path pathImmaggine;
    private Path pathAudio;

    public BranoModel(String id, String titolo, String descrizione, String artista, String genere, Path pathImmaggine, Path pathAudio) {
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

    public Path getPathImmaggine() {
        return pathImmaggine;
    }

    public void setPathImmaggine(Path pathImmaggine) {
        this.pathImmaggine = pathImmaggine;
    }

    public Path getPathAudio() {
        return pathAudio;
    }

    public void setPathAudio(Path pathAudio) {
        this.pathAudio = pathAudio;
    }
}
