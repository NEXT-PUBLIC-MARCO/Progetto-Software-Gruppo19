package it.gruppo19.progetto_music_player.model.strategyPattern;

import it.gruppo19.progetto_music_player.model.PlaylistModel;
import it.gruppo19.progetto_music_player.model.iteratorPattern.PlaylistIterator;

public interface PlaybackOrderStrategy {
    PlaylistIterator createIterator(PlaylistModel p);
}
