package it.gruppo19.progetto_music_player.model.commandPattern;

import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.model.PlaylistModel;

public class RemovePlaylist implements Command{

    PlaylistModel playlist;
    DataModel model;
    int index;

    public RemovePlaylist(PlaylistModel playlist, DataModel model){
        this.playlist = playlist;
        this.model = model;
    }

    @Override
    public void execute() {
        index = model.removePlaylist(playlist);
    }

    @Override
    public void undo() {
        model.addPlaylist(playlist, index);
    }
}
