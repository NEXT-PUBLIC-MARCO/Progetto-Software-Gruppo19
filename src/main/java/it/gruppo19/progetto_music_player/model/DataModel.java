package it.gruppo19.progetto_music_player.model;

import it.gruppo19.progetto_music_player.model.iteratorPattern.PlayerIterable;
import it.gruppo19.progetto_music_player.model.iteratorPattern.PlayerIterator;
import it.gruppo19.progetto_music_player.model.iteratorPattern.TuttiBraniIterator;
import it.gruppo19.progetto_music_player.model.observerPattern.Observer;
import it.gruppo19.progetto_music_player.model.observerPattern.Observable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DataModel implements Serializable, Observable, PlayerIterable {

    private static DataModel instance;
    public static DataModel getInstance() { return instance; }

    private List<BranoModel> brani;
    private List<PlaylistModel> playlists;

    private List<Observer> observers;

    public void Attach(Observer Observer){
        observers.add(Observer);
        System.out.println("[DEBUG] DataModel.Attach: observer agganciato = " + Observer
                + ", totale observers = " + observers.size());
    }

    public void Detach(Observer Observer){
        observers.remove(Observer);
    }

    public void Notify(String event, Object object){
        System.out.println("[DEBUG] DataModel.Notify: event=" + event
                + ", observers da notificare = " + observers.size());
        for(Observer Observer : observers)
            Observer.Update(event, object);
    }

    public DataModel(List<BranoModel> brani, List<PlaylistModel> playlists) {
        if(instance != null) return;
        instance = this;
        this.brani = new ArrayList<>();
        this.playlists = new ArrayList<>();
        this.observers = new ArrayList<>();

        if (brani != null) {
            this.brani.addAll(brani);
        }
        if (playlists != null) {
            this.playlists.addAll(playlists);
        }


    }

    public void addBrani(BranoModel b)
    {
        System.out.println("[DEBUG] DataModel.addBrani: b=" + b
                + ", brani totali dopo add = " + (brani.size() + 1));
        brani.add(b);
        //Notify("BraniChange", brani);
        Notify("BranoAdd", b);
        Notify("BraniChange", brani);
    }

    public void addBrani(BranoModel b, int index)
    {
        brani.add(index, b);
        //Notify("BraniChange", brani);
        Notify("BranoAdd", b);
        Notify("BraniChange", brani);
    }

    public int removeBrani (BranoModel b){
        int index = brani.indexOf(b);
        brani.remove(b);
        for(PlaylistModel p : playlists){
            p.removeBrano(b);
        }
        Notify("BranoRemove", b);
        Notify("BraniChange", brani);
        Notify("PlaylistChange", playlists);

        return index;
    }

    public void addPlaylist (PlaylistModel p){
        playlists.add(p);
        Notify("PlaylistAdd", p);
        Notify("PlaylistChange", playlists);
    }

    public void addPlaylist (PlaylistModel p, int index){
        playlists.add(index, p);
        Notify("PlaylistAdd", p);
        Notify("PlaylistChange", playlists);
    }

    public int removePlaylist (PlaylistModel p){
        int index = playlists.indexOf(p);
        playlists.remove(p);
        Notify("PlaylistRemove", p);
        Notify("PlaylistChange", playlists);
        return index;
    }

    public List<BranoModel> getBrani() {
        return brani.stream()
                .sorted((b1, b2) ->Integer.compare(b2.getAscolti(), b1.getAscolti()))
                .collect(java.util.stream.Collectors.toList());
    }

    public List<PlaylistModel> getPlaylists() {
        return playlists.stream()
                .sorted((p1, p2) -> Integer.compare(p2.getAscoltiTotali(), p1.getAscoltiTotali()))
                .collect(java.util.stream.Collectors.toList());
    }

    public void updateBrani(BranoModel b){
        // Questa cosa è sbagliata.
        // Tutte le chiamate di "BraniChange" passano come Object una List non un singolo BranoModel.
        //Notify("BraniChange", b);
        Notify("BranoUpdate", b);
        Notify("BraniChange", brani);
    }

    public void updatePlaylist(PlaylistModel p){
        // Stesso cosa qui di sopra.
        //Notify("PlaylistChange", p);
        Notify("PlaylistUpdate", p);
        Notify("PlaylistsChange", playlists);
    }

    @Override
    public PlayerIterator createIterator(BranoModel current) {
        TuttiBraniIterator iterator = new TuttiBraniIterator(getBrani(), current);
        this.Attach(iterator);
        return iterator;
    }
}
