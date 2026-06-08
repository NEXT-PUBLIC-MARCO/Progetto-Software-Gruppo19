package it.gruppo19.progetto_music_player.model.iteratorPattern;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.PlaylistModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShufflePlaylistIterator implements PlaylistIterator{

    private final PlaylistModel p;
    private final List<Integer> ordineCasuale;
    private int currentIndex;

    public ShufflePlaylistIterator(PlaylistModel p, int currentIndex) {
        this.p = p;
        this.ordineCasuale = new ArrayList<>();
        if(p==null || currentIndex < -1 || currentIndex >= p.size()){
            this.currentIndex = -1;
        }
        else{
            this.currentIndex = currentIndex;
        }
        if (p != null){
            for(int i = 0 ; i < p.size() ; i++){
                ordineCasuale.add(i);
            }
            Collections.shuffle(ordineCasuale);
        }


    }

    @Override
    public boolean hasNext(){
        return p!= null && currentIndex+1 < ordineCasuale.size();
    }

    @Override
    public BranoModel next(){
        if(!hasNext()){
            return null;
        }
        currentIndex++;
        int realIndex = ordineCasuale.get(currentIndex);
        return p.getBrano(realIndex);
    }

    @Override
    public boolean hasPrevious(){
        return p!=null && currentIndex-1 >= 0;
    }
    @Override
    public BranoModel previous(){
        if(!hasPrevious()){
            return null;
        }
        currentIndex--;
        int realIndex = ordineCasuale.get(currentIndex);
        return p.getBrano(realIndex);
    }

    @Override
    public BranoModel current() {
        if(p == null || currentIndex < 0 || currentIndex >= ordineCasuale.size()){
            return null;
        }
        int realIndex = ordineCasuale.get(currentIndex);
        return p.getBrano(realIndex);
    }


    @Override
    public void reset() {
        currentIndex = -1;
    }

}
