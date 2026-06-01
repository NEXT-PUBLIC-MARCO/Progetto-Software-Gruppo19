package it.gruppo19.progetto_music_player.model.playback;

import it.gruppo19.progetto_music_player.model.PlaylistIterator;
import it.gruppo19.progetto_music_player.model.PlaylistModel;

public interface PlaybackOrderStrategy {
    PlaylistIterator createIterator(PlaylistModel p);
}
