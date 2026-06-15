package it.gruppo19.progetto_music_player.model.iteratorPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.PlaylistModel;
import it.gruppo19.progetto_music_player.model.observerPattern.Observer;
import it.gruppo19.progetto_music_player.model.strategyPattern.PlaybackStrat;
import it.gruppo19.progetto_music_player.model.strategyPattern.OrderStrat;
import it.gruppo19.progetto_music_player.model.strategyPattern.SequentialStrat;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PlaylistIterator implements PlayerIterator, Observer {

    PlaylistModel playlist;
    List<BranoModel> brani;
    BranoModel current;
    PlaybackStrat strat;

    public PlaylistIterator(PlaylistModel playlist)
    {
        this.playlist = playlist;
        init(playlist);
    }

    void init(PlaylistModel playlist){
        brani = new SequentialStrat().setBrani(playlist.getBrani());
        this.current = brani == null || brani.isEmpty() ? null : brani.getFirst();
    }

    @Override
    public boolean hasNext() { return strat.hasNext(brani, current); }

    @Override
    public boolean hasPrevious() { return strat.hasPrevious(brani, current); }

    @Override
    public BranoModel getNext() {
        BranoModel next = strat.getNext(brani, current);
        if (next != null) current = next;   // avanza davvero nella coda
        return next;
    }

    @Override
    public BranoModel getPrevious() {
        BranoModel prev = strat.getPrevious(brani, current);
        if (prev != null) current = prev;
        return prev;
    }

    @Override
    public BranoModel getCurrent() { return current; }

    @Override
    public void setPlaybackStrat(PlaybackStrat strat) { this.strat = strat; }

    @Override
    public void setOrderStrat(OrderStrat strat) { brani = strat.setBrani(playlist.getBrani()); }

    @Override
    public void Update(String event, Object object) {
        if(event == "PlaylistRemove" && (PlaylistModel)object == playlist) {
            playlist = null;
            brani = null;
            current = null;
        } else if(event == "PlaylistChange" && (PlaylistModel)object == playlist) {
            Set setPlaylist = new HashSet<>(playlist.getBrani());
            Set setBrani = new HashSet<>(brani);
            if(!setPlaylist.equals(setBrani)) init(playlist);
        }
    }
}
