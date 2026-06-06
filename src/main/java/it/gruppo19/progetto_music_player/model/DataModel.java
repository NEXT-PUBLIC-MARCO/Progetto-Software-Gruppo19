package it.gruppo19.progetto_music_player.model;

import it.gruppo19.progetto_music_player.model.playback.Observable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataModel implements Serializable, Observable {
    private final List<BranoModel> brani;
    private final List<PlaylistModel> playlists;

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
        Notify("BraniChange", brani);
    }
    public void removeBrani (BranoModel b){
        brani.remove(b);
        Notify("BraniChange", brani);
    }

    public void addPlaylist (PlaylistModel p){
        playlists.add(p);
        Notify("PlaylistChange", playlists);
    }
    public void removePlaylist (PlaylistModel p){
        playlists.remove(p);
        Notify("PlaylistChange", playlists);
    }

    public List<BranoModel> getBrani() {
        return brani;
    }

    public List<PlaylistModel> getPlaylists() {
        return playlists;
    }

    public void updateBrani(BranoModel b){
        Notify("BraniChange", b);
    }
}
