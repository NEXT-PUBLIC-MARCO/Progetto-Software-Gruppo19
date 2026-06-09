package it.gruppo19.progetto_music_player.model.iteratorPattern;

import it.gruppo19.progetto_music_player.model.observerPattern.Observable;

public interface PlayerIterable{
    PlayerIterator createIterator(Observable model);
}
