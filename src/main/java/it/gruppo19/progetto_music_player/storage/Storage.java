package it.gruppo19.progetto_music_player.storage;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.PlaylistModel;

import java.util.ArrayList;

public class Storage {

    public ArrayList<BranoModel> LoadBrani(){
        return new ArrayList<BranoModel>();
    }

    public ArrayList<PlaylistModel> LoadPlaylist(){
        return new ArrayList<PlaylistModel>();
    }

    public void SaveBrani(ArrayList<BranoModel> brani){

    }

    public void SavePlaylist(ArrayList<PlaylistModel> playlists){

    }

}
