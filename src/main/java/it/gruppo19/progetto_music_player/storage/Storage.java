package it.gruppo19.progetto_music_player.storage;

import it.gruppo19.progetto_music_player.model.BranoModel;
import it.gruppo19.progetto_music_player.model.PlaylistModel;

import java.io.*;
import java.util.ArrayList;

public class Storage {

    public ArrayList<BranoModel> LoadBrani() {
        try (FileInputStream fis = new FileInputStream("brani.dat");
             ObjectInputStream ois = new ObjectInputStream(fis)) {

            return (ArrayList<BranoModel>) ois.readObject();   // cast necessario

        } catch (FileNotFoundException e) {
            return new ArrayList<>();   // primo avvio: file non esiste ancora → lista vuota
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public ArrayList<PlaylistModel> LoadPlaylist(){
        return new ArrayList<PlaylistModel>();
    }

    public void SaveBrani(ArrayList<BranoModel> brani)  {
        try(FileOutputStream fc = new FileOutputStream("brani.dat");
            ObjectOutputStream obj = new ObjectOutputStream(fc);
        ){

            obj.writeObject(brani);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void SavePlaylist(ArrayList<PlaylistModel> playlists){

    }

}
