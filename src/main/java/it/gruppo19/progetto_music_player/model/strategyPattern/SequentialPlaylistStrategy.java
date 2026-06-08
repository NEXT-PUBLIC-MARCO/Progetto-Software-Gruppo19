package it.gruppo19.progetto_music_player.model.strategyPattern;

import it.gruppo19.progetto_music_player.model.PlaylistModel;
import it.gruppo19.progetto_music_player.model.iteratorPattern.PlaylistIterator;
import it.gruppo19.progetto_music_player.model.iteratorPattern.SequentialPlaylistIterator;

public class SequentialPlaylistStrategy implements PlaybackOrderStrategy{
    @Override
    public PlaylistIterator createIterator(PlaylistModel p){
        return new SequentialPlaylistIterator(p,-1);

    }
}
