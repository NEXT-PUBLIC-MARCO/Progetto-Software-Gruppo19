package it.gruppo19.progetto_music_player.model;


import java.util.Iterator;

public interface PlaylistIterator extends Iterator {
    boolean hasNext();
    BranoModel next();
    boolean hasPrevious();
    BranoModel previous();
    BranoModel current();
    void reset();
}
