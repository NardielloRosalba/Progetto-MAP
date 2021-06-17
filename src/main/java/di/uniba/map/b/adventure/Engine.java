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
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ATTENZIONE: l'Engine è molto spartano, in realtà demanda la logica alla
 * classe che implementa GameDescription e si occupa di gestire I/O sul
 * terminale.
 *
 * @author gruppo 'Le bimbe di Luca'
 */
public class Engine {

    private GameDescription game;

    private Parser parser;

    public Engine(GameDescription game) {
        this.game = game;
        try {
            this.game.init();
        } catch (Exception ex) {
            System.err.println(ex);
        }
        try {
            Set<String> stopwords = Utils.loadFileListInSet(new File(".\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\stopwords.txt"));
            parser = new Parser(stopwords);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    public void execute() throws IOException, FileNotFoundException, ClassNotFoundException, SQLException {
        System.out.println("Attesa comandi!");
        switch (this.socket()) {
            case 0:
                System.out.println("Inizio nuova partita");
                System.out.println("========================================");
                System.out.println("** Adventure Luca, Rosalba, Raffaella **");
                System.out.println("========================================" + "\n" + "\n");
                System.out.println("Il protagonista, Capitan Hector, si trova \n"
                + "nella Navicella B612 della galassia Reggy e sta per \n"
                + "tornare nel suo pianeta nativo: Blind. Per cause oscure,\n"
                + "perde il controllo della navicella e di tutti i suoi \n"
                + "comandi, per salvarsi dovra' completare le missioni \n"
                + "nelle varie stanze,\n"
                + "cosi' da ristabilire i comandi persi della\n"
                + "navicella.\n");
                System.out.println("");
                System.out.println(game.getCurrentRoom().getName());
                System.out.println("");
                System.out.println(game.getCurrentRoom().getDescription());
                System.out.println("");      
                break;
                
            case 1:
                System.out.println("Uscita in corso, addio!!");
                break;
                
            case 2:
                //nome e password
                System.out.println("Caricamento partita");
                ParserOutput p = parser.parse("carica", game.getCommands(), game.getCurrentRoom().getObjects(), game.getInventory().getList());
                Database db = new Database();
                PianetaGame game_load = null;
                game_load = db.loading((PianetaGame) game);
                if(game_load != null){
                    game = game_load;
                    this.game.nextMove(p);
                }
                break;
        }
        Scanner scanner = new Scanner(System.in);
        System.out.println("Cosa devo fare? ");
        Database db = new Database();
        db.getInfo();
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            System.out.println("");
            ParserOutput p = parser.parse(command, game.getCommands(), game.getCurrentRoom().getObjects(), game.getInventory().getList());
            if (p.getCommand() != null && p.getCommand().getType() == CommandType.END) {
                this.game.nextMove(p);
                break;

            } else if (p.getCommand() != null && p.getCommand().getType() == CommandType.SAVE) {
                this.game.nextMove(p);

            } else if (p.getCommand() != null && p.getCommand().getType() == CommandType.LOAD) {
                PianetaGame game_load = null;
                game_load = db.loading((PianetaGame) game);
                if(game_load != null){
                    game = game_load;
                    this.game.nextMove(p);
                }
            } else {
                System.out.println(game.nextMove(p));
                System.out.println();
            }
            System.out.println("Cosa devo fare? ");
        }
    }

    public static void main(String[] args) throws IOException, FileNotFoundException, ClassNotFoundException, SQLException {
        Engine engine = new Engine(new PianetaGame());
        engine.execute();
    }

    public int socket() {
        try {
            String str = "";
            ServerSocket ss = new ServerSocket(6666);
            Socket s = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);

            while (true) {
                str = in.readLine();
                switch (str) {
                    case "new":
                        out.println("Ora inizierai una nuova partita!");
                        ss.close();
                        //nome e password
                        return 0;

                    case "quit":
                        out.println("Adios!");
                        ss.close();
                        return 1;

                    case "load":
                        out.println("Ora riprenderai una vecchia partita!");
                        ss.close();
                        return 2;

                    default:
                        out.println("Comando non riconosciuto");
                        break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
}
