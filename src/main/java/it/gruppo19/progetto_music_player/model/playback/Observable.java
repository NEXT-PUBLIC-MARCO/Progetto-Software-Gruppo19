package it.gruppo19.progetto_music_player.model.playback;

import it.gruppo19.progetto_music_player.model.Observer;

public interface Observable<T> {
    void Attach(Observer<T> observer);
    void Detach(Observer<T> observer);
    void Notify();
}
