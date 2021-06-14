/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure;

import di.uniba.map.b.adventure.games.PianetaGame;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 *
 * @author Luca
 */
public class saving_loading {

    public saving_loading() {
    }

    public static void comandoSalva(GameDescription game) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("game.dat"));
        out.writeObject(game);
        out.close();
    }

    public static GameDescription comandoCarica() throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream("game.dat"));
        PianetaGame game = (PianetaGame) in.readObject();
        in.close();
        return game;
    } 

}
