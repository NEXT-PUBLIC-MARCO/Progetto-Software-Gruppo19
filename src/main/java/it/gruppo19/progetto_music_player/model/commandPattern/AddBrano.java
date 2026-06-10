package it.gruppo19.progetto_music_player.model.commandPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.DataModel;

public class AddBrano implements Command {

    BranoModel brano;
    DataModel model;

    public AddBrano(BranoModel brano, DataModel model){
        this.brano = brano;
        this.model = model;
    }

    @Override
    public void execute() {
        model.addBrani(brano);
    }

    @Override
    public void undo() {
        model.removeBrani(brano);
    }
}
