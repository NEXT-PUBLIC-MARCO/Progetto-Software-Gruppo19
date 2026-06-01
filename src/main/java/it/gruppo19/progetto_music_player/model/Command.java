package it.gruppo19.progetto_music_player.model;

public interface Command {
    boolean execute();
    void undo();
    String getDescription();
}
