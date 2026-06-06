package it.gruppo19.progetto_music_player.controller;

import it.gruppo19.progetto_music_player.model.DataModel;
import it.gruppo19.progetto_music_player.storage.Storage;
import it.gruppo19.progetto_music_player.view.DummyView;

import java.io.IOException;
import java.util.ArrayList;

public class Controller {

    private DataModel model;
    private Storage storage;
    private DummyView view;

    public void OnAppStartup() throws IOException {
        storage = new Storage();
        view = new DummyView();
        model = new DataModel(storage.LoadBrani(),storage.LoadPlaylist());
        model.Attach(view.getController());
        view.getController().setModel(model);

        // Aggancio il controller come Observer del model (pattern Observer).
        //
        // PERCHE' SERVE:
        // DataModel.addBrani()/removeBrani() chiamano Notify("BraniChange", ...),
        // che a sua volta scorre la lista 'observers' e invoca Update() su ognuno.
        // Se nessuno e' agganciato, quella lista e' vuota: la notifica parte ma
        // non raggiunge nessuno, quindi MainViewController.Update() -> refreshLibrary()
        // (il path che ridisegna la libreria) non viene MAI eseguito.
        //
        // Senza questa Attach la UI si aggiorna solo perche' onAdd() richiama
        // refreshLibrary() a mano: ogni altra modifica al model (es. fatta da
        // un'altra parte del codice) passerebbe inosservata. Agganciando qui
        // l'Observer, la view reagisce automaticamente a ogni cambiamento del
        // model, che e' lo scopo del pattern.
    }

    public void OnAppClose(){

    }
}