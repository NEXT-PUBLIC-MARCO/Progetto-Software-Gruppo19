package it.gruppo19.progetto_music_player.model.commandPattern;

import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.model.PlaylistModel;

public class AddPlaylist implements Command{

    PlaylistModel playlist;
    DataModel model;

    public AddPlaylist(PlaylistModel playlist, DataModel model){
        this.playlist = playlist;
        this.model = model;
    }

    @Override
    public void execute() {
        model.addPlaylist(playlist);
    }

    @Override
    public void undo() {
        model.removePlaylist(playlist);
    }
}
