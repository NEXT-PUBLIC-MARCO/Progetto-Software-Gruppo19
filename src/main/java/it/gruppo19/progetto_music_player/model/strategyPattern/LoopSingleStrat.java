package it.gruppo19.progetto_music_player.model.strategyPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;

import java.util.List;

public class LoopSingleStrat implements PlaybackStrat{
    @Override
    public boolean hasNext(List<BranoModel> tracks, BranoModel current) {
        return current != null;
    }

    @Override
    public BranoModel getNext(List<BranoModel> tracks, BranoModel current) {
        return current;
    }

    @Override
    public BranoModel getPrevious(List<BranoModel> tracks, BranoModel current) {
        return current;
    }
}
