package it.gruppo19.progetto_music_player.model;

import it.gruppo19.progetto_music_player.model.playback.Observable;

public interface Observer {
    void Update(String event, Object object);
}
