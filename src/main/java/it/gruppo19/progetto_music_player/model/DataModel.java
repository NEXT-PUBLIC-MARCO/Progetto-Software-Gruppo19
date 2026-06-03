package it.gruppo19.progetto_music_player.model;

import it.gruppo19.progetto_music_player.model.playback.Observable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataModel implements Serializable, Observable<List<BranoModel>> {
    private final List<BranoModel> brani;
    private final List<PlaylistModel> playlists;

    private List<Observer<List<BranoModel>>> Observers;

    public void Attach(Observer<List<BranoModel>> Observer){
        Observers.add(Observer);
    }

    public void Detach(Observer<List<BranoModel>> Observer){
        Observers.remove(Observer);
    }

    public void Notify(){
        for(Observer<List<BranoModel>> Observer : Observers)
            Observer.Update(brani);
    }

    public DataModel(List<BranoModel> brani, List<PlaylistModel> playlists) {
        this.brani = new ArrayList<>();
        this.playlists = new ArrayList<>();

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
        Notify();
    }
    public void removeBrani (BranoModel b){
        brani.remove(b);
        Notify();
    }

    public void addPlaylist (PlaylistModel p){
        playlists.add(p);
    }
    public void removePlaylist (PlaylistModel p){
        playlists.remove(p);
    }

    public List<BranoModel> getBrani() {
        return brani;
    }

    public List<PlaylistModel> getPlaylists() {
        return playlists;
    }
}
