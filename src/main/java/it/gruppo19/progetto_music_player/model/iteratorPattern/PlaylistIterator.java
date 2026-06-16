package it.gruppo19.progetto_music_player.model.iteratorPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.PlaylistModel;
import it.gruppo19.progetto_music_player.model.observerPattern.Observer;
import it.gruppo19.progetto_music_player.model.strategyPattern.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlaylistIterator implements PlayerIterator, Observer {

    PlaylistModel playlist;
    List<BranoModel> braniRiordinati;
    BranoModel current;
    OrderStrat orderStrat;
    PlaybackStrat playbackStrat;

    public PlaylistIterator(PlaylistModel playlist, BranoModel current)
    {
        this.playlist = playlist;
        orderStrat = new SequentialStrat();
        playbackStrat = new PlayOnceStrat();
        braniRiordinati = orderStrat.setBrani(playlist.getBrani());
        if(current != null)
            this.current = current;
        else
            this.current = braniRiordinati == null || braniRiordinati.isEmpty() ? null : braniRiordinati.getFirst();
    }

    @Override
    public boolean hasNext() { return playbackStrat.hasNext(braniRiordinati, current); }

    @Override
    public BranoModel getNext() {
        if(braniRiordinati != null && !braniRiordinati.isEmpty() && current != null)
            current = braniRiordinati.get((braniRiordinati.indexOf(current) + 1) % braniRiordinati.size());
        return current;
    }

    @Override
    public BranoModel getPrevious() {
        if(braniRiordinati != null && !braniRiordinati.isEmpty() && current != null)
            current = braniRiordinati.get((braniRiordinati.indexOf(current) - 1) % braniRiordinati.size());
        return current;
    }

    @Override
    public BranoModel getCurrent() { return current; }

    @Override
    public void setPlaybackStrat(PlaybackStrat strat) { this.playbackStrat = strat; }

    @Override
    public void setOrderStrat(OrderStrat strat)
    {
        orderStrat = strat;
        braniRiordinati = orderStrat.setBrani(playlist.getBrani());
    }

    @Override
    public void Update(String event, Object object) {
        if(event == "PlaylistRemove" && (PlaylistModel)object == playlist) {
            playlist = null;
            braniRiordinati = null;
            current = null;
        } else if(event == "PlaylistChange" && (PlaylistModel)object == playlist) {
            Set setPlaylist = new HashSet<>(playlist.getBrani());
            Set setBrani = new HashSet<>(braniRiordinati);
            if(!setPlaylist.equals(setBrani)){
                if(!playlist.getBrani().contains(current)) getNext();
                braniRiordinati = orderStrat.setBrani(playlist.getBrani());
            }
        }
    }
}
