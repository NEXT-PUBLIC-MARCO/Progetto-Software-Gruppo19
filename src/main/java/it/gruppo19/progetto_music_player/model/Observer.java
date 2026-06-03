package it.gruppo19.progetto_music_player.model;

import it.gruppo19.progetto_music_player.model.playback.Observable;

public interface Observer<T> {
    void Update(T observed);
}
