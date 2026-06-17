package it.gruppo19.progetto_music_player.model;
import it.gruppo19.progetto_music_player.model.iteratorPattern.TuttiBraniIterator;
import it.gruppo19.progetto_music_player.model.iteratorPattern.PlayerIterable;
import it.gruppo19.progetto_music_player.model.iteratorPattern.PlayerIterator;
import it.gruppo19.progetto_music_player.model.observerPattern.Observable;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BranoModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String titolo;
    private String descrizione;
    private String artista;
    private String genere;
    private int anno;
    private long durata;
    private transient Path pathImmaggine;
    private transient Path pathAudio;
    private int ascolti=0;
    private boolean favourite;
    private boolean explicit;
    private boolean newRelease;

    public BranoModel(String id, String titolo, String descrizione, String artista, String genere, int anno, Path pathImmaggine, Path pathAudio) {
        this.id = id;
        this.titolo = titolo;
        this.descrizione = descrizione;
        this.artista = artista;
        this.genere = genere;
        this.anno=anno;
        this.pathImmaggine = pathImmaggine;
        this.pathAudio = pathAudio;
        this.durata = calcolaDurata(pathAudio);
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

    public long getDurata(){return durata;}

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

    public int getAscolti() {
        return ascolti;
    }

    public void incrementaAscolti() {
        this.ascolti++;
    }

    public boolean isFavourite() {
        return favourite;
    }

    public void setFavourite(boolean favourite) {
        this.favourite = favourite;
    }

    public boolean isExplicit() {
        return explicit;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }

    public boolean isNewRelease() {
        return newRelease;
    }

    public void setNewRelease(boolean newRelease) {
        this.newRelease = newRelease;
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

    /*
    @Override
    public PlayerIterator createIterator(DataModel model) {
        TuttiBraniIterator iterator = new TuttiBraniIterator(model.getBrani(), this);
        model.Attach(iterator);
        return iterator;
    }
     */

    //calcola la durata della canzone partendo dal fileAudio impostato
    private static long calcolaDurata(Path pathAudio){
        if(pathAudio == null) return -1 ;
        try{
            AudioFileFormat f = AudioSystem.getAudioFileFormat(pathAudio.toFile());
            Map<String,Object> props  = f.properties();
            Object micros = props.get("duration");
            if(micros instanceof Long l){
                return l/1_000_000L;
            }
        }catch(UnsupportedAudioFileException  | IOException e){
            e.printStackTrace();
        }
        return -1;
    }

    public String getDurataFormattata(){
        if(durata <= 0) return "--:--";
        return String.format("%d:%02d",durata / 60 , durata % 60);
    }


}
