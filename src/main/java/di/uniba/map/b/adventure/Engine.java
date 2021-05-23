/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure;

import di.uniba.map.b.adventure.games.PianetaGame;
import di.uniba.map.b.adventure.parser.Parser;
import di.uniba.map.b.adventure.parser.ParserOutput;
import di.uniba.map.b.adventure.type.CommandType;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

/**
 * ATTENZIONE: l'Engine è molto spartano, in realtà demanda la logica alla
 * classe che implementa GameDescription e si occupa di gestire I/O sul
 * terminale.
 *
 * @author pierpaolo
 */
public class Engine {

    private final GameDescription game;

    private Parser parser;

    public Engine(GameDescription game) {
        this.game = game;
        try {
            this.game.init();
        } catch (Exception ex) {
            System.err.println(ex);
        }
        try {
            Set<String> stopwords = Utils.loadFileListInSet(new File("./resources/stopwords"));
            parser = new Parser(stopwords);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public void execute() {

        System.out.println("========================================");
        System.out.println("** Adventure Luca, Rosalba, Raffaella **");
        System.out.println("========================================" + "\n" + "\n");
        /*   System.out.println("Il protagonista, Capitan Hector, si trova \n"
                    + "nella Navicella B612 della galassia Reggy e sta per \n"
                    + "tornare nel suo pianeta nativo: Blind,per cause oscure,\n"
                    + "  perde il controllo della navicella e di tutti i suoi \n"
                    + "comandi, per salvarsi dovrà finire tutte le missioni \n"
                    + "nelle varie stanze e ricomporre un puzzle,\n"
                    + " i cui pezzi si avranno alla fine di ogni missione,\n"
                    + " che darà il codice per ristabilire i comandi persi della\n"
                    + " navicella.\n");*/
        System.out.println("");
        System.out.println(game.getCurrentRoom().getName());
        System.out.println("");
        System.out.println(game.getCurrentRoom().getDescription());
        System.out.println("");
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            ParserOutput p = parser.parse(command, game.getCommands(), game.getCurrentRoom().getObjects(), game.getInventory());
            if (p.getCommand() != null && p.getCommand().getType() == CommandType.END) {
                System.out.println("Addio!");
                break;
            } else if (p.getCommand() != null && p.getCommand().getType() == CommandType.SAVE) {
                System.out.println("Salvataggio...");
                break;
            } else if (p.getCommand() != null && p.getCommand().getType() == CommandType.LOAD) {
                System.out.println("Caricamento...");
                break;
            } else {
                System.out.println(game.nextMove(p));
                System.out.println();
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Engine engine = new Engine(new PianetaGame());
        engine.execute();
    }

}
