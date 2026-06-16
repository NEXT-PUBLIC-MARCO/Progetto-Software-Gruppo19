package it.gruppo19.progetto_music_player.model.iteratorPattern;


import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.strategyPattern.PlaybackStrat;
import it.gruppo19.progetto_music_player.model.strategyPattern.OrderStrat;

public interface PlayerIterator{
    boolean hasNext();
    BranoModel getNext();
    BranoModel getPrevious();
    BranoModel getCurrent();
    void setPlaybackStrat(PlaybackStrat strat);
    void setOrderStrat(OrderStrat strat);
}
