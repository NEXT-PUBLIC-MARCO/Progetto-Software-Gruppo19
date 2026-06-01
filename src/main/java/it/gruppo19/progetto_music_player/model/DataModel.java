package it.gruppo19.progetto_music_player.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataModel implements Serializable{
    private final List<BranoModel> brani;
    private final List<PlaylistModel> playlists;


    public DataModel(List<BranoModel> brani, List<PlaylistModel> playlists) {
        this.brani = new ArrayList<>();
        this.playlists = new ArrayList<>();
    }

    public void addBrani(BranoModel b){
        brani.add(b);
    }
    public void removeBrani (BranoModel b){
        brani.remove(b);
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
