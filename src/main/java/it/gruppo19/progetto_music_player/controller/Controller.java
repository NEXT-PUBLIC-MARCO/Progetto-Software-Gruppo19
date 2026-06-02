package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.storage.DummyStorage;
import it.gruppo19.progetto_music_player.view.DummyView;

import java.io.IOException;

public class Controller {

    private DataModel model;
    private DummyStorage storage;
    private DummyView view;

    public void OnAppStartup() throws IOException {
        view = new DummyView();
        //model = new DataModel();
        storage = new DummyStorage();
    }

    public void OnAppClose(){

    }
}