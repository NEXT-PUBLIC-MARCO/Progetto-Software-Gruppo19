package it.gruppo19.progetto_music_player.model.strategyPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;

import java.util.*;

public class ShuffleStrat implements OrderStrat {

    @Override
    public List<BranoModel> setBrani(List<BranoModel> tracks) {
        ArrayList<BranoModel> shuffled = new ArrayList<BranoModel>(tracks);
        Collections.shuffle(tracks);
        return shuffled;
    }
}
