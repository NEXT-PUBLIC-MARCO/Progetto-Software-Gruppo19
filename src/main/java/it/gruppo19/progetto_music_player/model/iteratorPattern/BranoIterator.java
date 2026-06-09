package it.gruppo19.progetto_music_player.model.iteratorPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.observerPattern.Observer;
import it.gruppo19.progetto_music_player.model.strategyPattern.PlaybackStrat;
import it.gruppo19.progetto_music_player.model.strategyPattern.OrderStrat;

public class BranoIterator implements PlayerIterator, Observer {

    BranoModel brano;
    PlaybackStrat loopingStrat;

    public BranoIterator(BranoModel current)
    {
        brano = current;
    }

    @Override
    public boolean hasNext() { return loopingStrat.hasNext(null, brano); }

    @Override
    public boolean hasPrevious() { return loopingStrat.hasPrevious(null, brano); }

    @Override
    public BranoModel getNext() { return loopingStrat.getNext(null, brano); }

    @Override
    public BranoModel getPrevious() { return loopingStrat.getPrevious(null, brano); }

    @Override
    public BranoModel getCurrent() { return brano; }

    @Override
    public void setPlaybackStrat(PlaybackStrat strat) { loopingStrat = strat; }

    @Override
    public void setOrderStrat(OrderStrat strat) { }

    @Override
    public void Update(String event, Object object) {
        if(event == "BranoRemove" && (BranoModel)object == brano){
            brano = null;
        }
    }
}
