package it.gruppo19.progetto_music_player.model.observerPattern;

public interface Observer {
    void Update(Observer.Events event, Object object);

    public static enum Events{
        BranoAdd,
        BranoRemove,
        BranoUpdate,
        BraniChange,
        PlaylistAdd,
        PlaylistRemove,
        PlaylistUpdate,
        PlaylistsChange
    }
}
