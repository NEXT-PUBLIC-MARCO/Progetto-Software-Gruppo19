package it.gruppo19.progetto_music_player.model.playback;

import it.gruppo19.progetto_music_player.model.Observer;

public interface Observable{
    void Attach(Observer observer);
    void Detach(Observer observer);
    void Notify(String event, Object object);
}
