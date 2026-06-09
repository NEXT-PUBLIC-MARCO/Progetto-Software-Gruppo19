package it.gruppo19.progetto_music_player.model.strategyPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;

import java.util.*;

public class SequentialStrat implements OrderStrat {

    @Override
    public List<BranoModel> setBrani(List<BranoModel> tracks) {
        return new ArrayList<BranoModel>(tracks);
    }
}
