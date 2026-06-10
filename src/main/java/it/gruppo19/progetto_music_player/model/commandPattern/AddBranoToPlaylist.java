package it.gruppo19.progetto_music_player.model.commandPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.model.PlaylistModel;

public class AddBranoToPlaylist implements Command{

    BranoModel brano;
    PlaylistModel playlist;

    public AddBranoToPlaylist(BranoModel brano, PlaylistModel playlist){
        this.brano = brano;
        this.playlist = playlist;
    }

    @Override
    public void execute() {
        playlist.addBrano(brano);
    }

    @Override
    public void undo() {
        playlist.removeBrano(brano);
    }
}
