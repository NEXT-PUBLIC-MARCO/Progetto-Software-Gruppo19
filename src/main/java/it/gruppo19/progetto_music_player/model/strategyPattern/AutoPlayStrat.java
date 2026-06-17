package it.gruppo19.progetto_music_player.model.strategyPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;

import java.util.List;

public class AutoPlayStrat implements PlaybackStrat{
    @Override
    public boolean hasNext(List<BranoModel> tracks, BranoModel current) {
        return true;
    }
}
