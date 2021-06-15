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
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 *
 * @author Luca
 */
public class saving_loading {

    public saving_loading() {
    }

   /* public static void comandoSalva(byte[] game) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(".\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\game.dat"));
        out.writeObject(game);
        out.close();
    }*/

    public static void comandoSalva(PianetaGame game) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(".\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\games.dat"));
        out.writeObject(game);
        out.close();
    }

    /*public static void comandoSalva3(PianetaGame game) throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(".\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\game2.dat"));
        out.writeObject(game);
        out.close();
    }

    public static byte[] comandoCarica() throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(".\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\game.dat"));
        byte[] gioco = in.readAllBytes();
        in.close();
        return gioco;
    }
    */
    public static PianetaGame comandoCarica2() throws FileNotFoundException, IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(".\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\games.dat"));
        PianetaGame game = (PianetaGame) in.readObject();
        in.close();
        return game;
    }

    public static PianetaGame comandoCarica3(InputStream is) throws FileNotFoundException, IOException, ClassNotFoundException {
        //ObjectInputStream in = new ObjectInputStream(new FileInputStream(".\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\game2.dat"));
        FileOutputStream out = new FileOutputStream(".\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\gamel.dat");
        is.transferTo(out);
        is.close();
        
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(".\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\gamel.dat"));
        PianetaGame game = (PianetaGame) in.readObject();
        in.close();
        return game;
    }
}
