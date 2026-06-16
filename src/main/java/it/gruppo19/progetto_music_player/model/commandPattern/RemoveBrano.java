package it.gruppo19.progetto_music_player.model.commandPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.model.PlaylistModel;

import java.util.ArrayList;
import java.util.List;

public class RemoveBrano implements Command{

    BranoModel brano;
    DataModel model;
    int index;
    private final List<PlaylistModel> playlists = new ArrayList<>();

    public RemoveBrano(BranoModel brano, DataModel model){
        this.brano = brano;
        this.model = model;
    }

    @Override
    public void execute() {
        for(PlaylistModel p : model.getPlaylists()){
            if(p.removeBrano(brano) >= 0){
                playlists.add(p);
            }
        }
        index = model.removeBrani(brano);
        model.Notify("PlaylistChange", model.getPlaylists());
    }

    @Override
    public void undo() {
        model.addBrani(brano, index);
        for(PlaylistModel p : playlists){
            p.addBrano(brano);
        }
        model.Notify("PlaylistChange", model.getPlaylists());
    }
}
