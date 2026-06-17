package it.gruppo19.progetto_music_player.model.strategyPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;

import java.util.List;

public interface PlaybackStrat {
    boolean hasNext(List<BranoModel> tracks, BranoModel current);

    default BranoModel getNext(List<BranoModel> tracks, BranoModel current){
        if(tracks != null && !tracks.isEmpty() && current != null)
            current = tracks.get((tracks.indexOf(current) + 1) % tracks.size());
        return current;
    }

    default BranoModel getPrevious(List<BranoModel> tracks, BranoModel current){
        if(tracks != null && !tracks.isEmpty() && current != null)
            current = tracks.get((tracks.indexOf(current) - 1 + tracks.size()) % tracks.size());
        return current;
    }
}
