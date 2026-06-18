package it.gruppo19.progetto_music_player.model.iteratorPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.observerPattern.Observer;
import it.gruppo19.progetto_music_player.model.strategyPattern.*;

import java.util.List;

public class TuttiBraniIterator implements PlayerIterator, Observer {

    List<BranoModel> tuttiBrani;
    List<BranoModel> braniRiordinati;
    BranoModel current;
    OrderStrat orderStrat;
    PlaybackStrat playbackStrat;

    public TuttiBraniIterator(List<BranoModel> tuttiBrani, BranoModel current)
    {
        this.tuttiBrani = tuttiBrani;
        orderStrat = new SequentialStrat();
        playbackStrat = new NoAutoPlayStrat();
        braniRiordinati = orderStrat.setBrani(tuttiBrani);
        if(current != null)
            this.current = current;
        else
            this.current = braniRiordinati == null || braniRiordinati.isEmpty() ? null : braniRiordinati.getFirst();
    }

    @Override
    public boolean hasNext() { return playbackStrat.hasNext(braniRiordinati, current); }

    @Override
    public BranoModel getNext() {
        current = playbackStrat.getNext(braniRiordinati, current);
        return current;
    }

    @Override
    public BranoModel getPrevious() {
        current = playbackStrat.getPrevious(braniRiordinati, current);
        return current;
    }

    @Override
    public BranoModel getCurrent() { return current; }

    @Override
    public PlaybackStrat getPlaybackOrderStrat() { return playbackStrat; }

    @Override
    public void setPlaybackStrat(PlaybackStrat strat) { this.playbackStrat = strat; }

    @Override
    public OrderStrat getOrderStrat() { return orderStrat; }

    @Override
    public void setOrderStrat(OrderStrat strat)
    {
        orderStrat = strat;
        braniRiordinati = orderStrat.setBrani(tuttiBrani);
    }

    @Override
    public void Update(Observer.Events event, Object object) {
        // CORREZIONE 2: In Java le stringhe si confrontano SEMPRE con .equals(), non con ==
        if(event.equals(Events.BranoRemove) && (BranoModel)object == current){
            getNext();
        } else if(event.equals(Events.BraniChange)){
            tuttiBrani = (List<BranoModel>)object;
            braniRiordinati = orderStrat.setBrani(tuttiBrani);
        }
    }
}

