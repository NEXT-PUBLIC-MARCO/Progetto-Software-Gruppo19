package it.gruppo19.progetto_music_player.model.commandPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.DataModel;

public class RemoveBrano implements Command{

    BranoModel brano;
    DataModel model;
    int index;

    public RemoveBrano(BranoModel brano, DataModel model){
        this.brano = brano;
        this.model = model;
    }

    @Override
    public void execute() {
        index = model.removeBrani(brano);
    }

    @Override
    public void undo() {
        model.addBrani(brano, index);
    }
}
