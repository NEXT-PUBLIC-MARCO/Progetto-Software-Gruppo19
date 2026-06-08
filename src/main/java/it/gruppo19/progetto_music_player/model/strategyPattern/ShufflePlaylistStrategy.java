package it.gruppo19.progetto_music_player.model.strategyPattern;

import it.gruppo19.progetto_music_player.model.PlaylistModel;
import it.gruppo19.progetto_music_player.model.iteratorPattern.PlaylistIterator;
import it.gruppo19.progetto_music_player.model.iteratorPattern.ShufflePlaylistIterator;

public class ShufflePlaylistStrategy implements PlaybackOrderStrategy {
    @Override
    public PlaylistIterator createIterator(PlaylistModel p){
        return new ShufflePlaylistIterator(p,-1);

    }
}
