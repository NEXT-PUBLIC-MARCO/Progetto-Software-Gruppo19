package it.gruppo19.progetto_music_player.model.strategyPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;

import java.util.*;

public interface OrderStrat {
    List<BranoModel> setBrani(List<BranoModel> tracks);
}
