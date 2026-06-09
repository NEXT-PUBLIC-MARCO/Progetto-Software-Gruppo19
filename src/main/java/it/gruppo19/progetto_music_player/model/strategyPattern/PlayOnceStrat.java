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

    @Override
    public BranoModel getNext(List<BranoModel> tracks, BranoModel current) {
        if(tracks == null || tracks.isEmpty() || current == null) return null;
        int nextIndex = tracks.indexOf(current) + 1;
        return nextIndex < tracks.size() ? tracks.get(nextIndex) : null;
    }

    @Override
    public BranoModel getPrevious(List<BranoModel> tracks, BranoModel current) {
        if(tracks == null || tracks.isEmpty() || current == null) return null;
        int prevIndex = tracks.indexOf(current) - 1;
        return prevIndex > 0 ? tracks.get(prevIndex) : null;
    }
}
