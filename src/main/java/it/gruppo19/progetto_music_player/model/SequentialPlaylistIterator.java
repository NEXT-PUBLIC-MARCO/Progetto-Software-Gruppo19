package it.gruppo19.progetto_music_player.model;

public class SequentialPlaylistIterator implements PlaylistIterator {
    private final PlaylistModel p;
    private int currentIndex;

    public SequentialPlaylistIterator(PlaylistModel p, int currentIndex) {
        this.p = p;
        if(p==null || currentIndex < -1 || currentIndex >= p.size()){
            this.currentIndex = -1;
        }
        else{
            this.currentIndex = currentIndex;
        }
    }

    @Override
    public boolean hasNext(){
        return p != null && currentIndex+1 < p.size();
    }

    @Override
    public  BranoModel next(){
        if(!hasNext()){
            return null;
        }
        return p.getBrano(++currentIndex);
    }

    @Override
    public boolean hasPrevious(){
        return p != null && currentIndex-1 >= 0;
    }

    @Override
    public  BranoModel previous(){
         if(!hasPrevious()){
        return null;
        }
        return p.getBrano(--currentIndex);
    }

    @Override
    public BranoModel current(){
        if(p == null || currentIndex < 0 || currentIndex >= p.size()){
            return null;
        }
        return p.getBrano(currentIndex);
    }

    @Override
    public void reset(){
        currentIndex = -1;
    }
}
