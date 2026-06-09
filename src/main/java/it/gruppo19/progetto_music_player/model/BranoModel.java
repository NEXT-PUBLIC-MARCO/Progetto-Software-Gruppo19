package it.gruppo19.progetto_music_player.model;
import it.gruppo19.progetto_music_player.model.iteratorPattern.BranoIterator;
import it.gruppo19.progetto_music_player.model.iteratorPattern.PlayerIterable;
import it.gruppo19.progetto_music_player.model.iteratorPattern.PlayerIterator;
import it.gruppo19.progetto_music_player.model.observerPattern.Observable;

import java.io.*;
import java.nio.file.Path;

public class BranoModel implements Serializable, PlayerIterable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String titolo;
    private String descrizione;
    private String artista;
    private String genere;
    private int anno;
    private transient Path pathImmaggine;
    private transient Path pathAudio;

    public BranoModel(String id, String titolo, String descrizione, String artista, String genere, int anno, Path pathImmaggine, Path pathAudio) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.artista = artista;
        this.genere = genere;
        this.anno=anno;
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

    public int getAnno(){
        return anno;
    }

    public void setAnno(int anno){
        this.anno=anno;
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

    //Chiamato da ObjectOutputStream durante writeObject
    @Serial
    private void writeObject(ObjectOutputStream obj) throws IOException  {
        obj.defaultWriteObject();
        obj.writeObject(pathAudio == null ? null : pathAudio.toString());
        obj.writeObject(pathImmaggine == null ? null : pathImmaggine.toString());

    }

    //Chiamato da ObjectOutputStream durante writeObject
    @Serial
    private void readObject(ObjectInputStream obj) throws  IOException, ClassNotFoundException {
        obj.defaultReadObject();
        String a = (String) obj.readObject();
        String b = (String) obj.readObject();
        this.pathAudio = (a == null ? null : Path.of(a));
        this.pathImmaggine = (b == null ? null : Path.of(b));


    }

    @Override
    public PlayerIterator createIterator(Observable model) {
        BranoIterator iterator = new BranoIterator(this);
        if(model != null) model.Attach(iterator);
        return iterator;
    }
}
