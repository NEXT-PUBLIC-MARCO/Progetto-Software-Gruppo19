package it.gruppo19.progetto_music_player.model.strategyPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;

import java.util.List;

public class PlayOnceStrat implements PlaybackStrat {

    @Override
    public boolean hasNext(List<BranoModel> tracks, BranoModel current) {
        return tracks != null && !tracks.isEmpty() && current != null && tracks.indexOf(current) + 1 < tracks.size();
    }

    @Override
    public boolean hasPrevious(List<BranoModel> tracks, BranoModel current) {
        return tracks != null && !tracks.isEmpty() && current != null && tracks.indexOf(current) - 1 > 0;
    }
}
