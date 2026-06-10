package it.gruppo19.progetto_music_player.model.commandPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.PlaylistModel;

public class RemoveBranoFromPlaylist implements Command{

    BranoModel brano;
    PlaylistModel playlist;
    int index;

    public RemoveBranoFromPlaylist(BranoModel brano, PlaylistModel playlist){
        this.brano = brano;
        this.playlist = playlist;
    }

    @Override
    public void execute() {
        index = playlist.removeBrano(brano);
    }

    @Override
    public void undo() {
        playlist.addBrano(brano, index);
    }
}
