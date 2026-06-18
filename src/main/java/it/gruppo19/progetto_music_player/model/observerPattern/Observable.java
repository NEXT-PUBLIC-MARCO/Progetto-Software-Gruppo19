package it.gruppo19.progetto_music_player.model.observerPattern;

public interface Observable{
    void Attach(Observer observer);
    void Detach(Observer observer);
    void Notify(Observer.Events event, Object object);
}
