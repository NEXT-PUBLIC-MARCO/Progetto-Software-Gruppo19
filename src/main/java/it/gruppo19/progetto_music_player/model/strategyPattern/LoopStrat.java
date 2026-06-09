package it.gruppo19.progetto_music_player.model.strategyPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;

import java.util.List;


public class LoopStrat implements PlaybackStrat {

    @Override
    public boolean hasNext(List<BranoModel> tracks, BranoModel current) {
        return (tracks != null && !tracks.isEmpty()) || current != null;
    }

    @Override
    public boolean hasPrevious(List<BranoModel> tracks, BranoModel current) {
        return (tracks != null && !tracks.isEmpty()) || current != null;
    }

    @Override
    public BranoModel getNext(List<BranoModel> tracks, BranoModel current){
        if(tracks == null || tracks.isEmpty() || current == null) return current;
        return tracks.get((tracks.indexOf(current) + 1) % tracks.size());
    }

    @Override
    public BranoModel getPrevious(List<BranoModel> tracks, BranoModel current) {
        if(tracks == null || tracks.isEmpty() || current == null) return current;
        return tracks.get((tracks.indexOf(current) - 1) % tracks.size());
    }
}
