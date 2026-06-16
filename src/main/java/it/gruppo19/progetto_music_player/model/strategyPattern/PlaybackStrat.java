package it.gruppo19.progetto_music_player.model.strategyPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;

import java.util.List;

public interface PlaybackStrat {
    boolean hasNext(List<BranoModel> tracks, BranoModel current);
}
