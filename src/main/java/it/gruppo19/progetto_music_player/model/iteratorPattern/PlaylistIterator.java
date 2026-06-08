package it.gruppo19.progetto_music_player.model.iteratorPattern;


import it.gruppo19.progetto_music_player.model.BranoModel;

import java.util.Iterator;

public interface PlaylistIterator extends Iterator {
    boolean hasNext();
    BranoModel next();
    boolean hasPrevious();
    BranoModel previous();
    BranoModel current();
    void reset();
}
