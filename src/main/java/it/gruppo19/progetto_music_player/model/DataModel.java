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
    }

    public void Detach(Observer Observer){
        observers.remove(Observer);
    }

    public void Notify(String event, Object object){
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
}
