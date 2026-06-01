package it.gruppo19.progetto_music_player.model.playback;

import it.gruppo19.progetto_music_player.model.PlaylistIterator;
import it.gruppo19.progetto_music_player.model.PlaylistModel;
import it.gruppo19.progetto_music_player.model.SequentialPlaylistIterator;

public class SeqentialPlaylistStrategy implements PlaybackOrderStrategy{
    @Override
    public PlaylistIterator createIterator(PlaylistModel p){
        return new SequentialPlaylistIterator(p);
    }
}
