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
import di.uniba.map.b.adventure.type.SocketClient;
import di.uniba.map.b.adventure.type.SocketServer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        System.out.println("Il protagonista, Capitan Hector, si trova \n"
                + "nella Navicella B612 della galassia Reggy e sta per \n"
                + "tornare nel suo pianeta nativo: Blind. Per cause oscure,\n"
                + "perde il controllo della navicella e di tutti i suoi \n"
                + "comandi, per salvarsi dovrà finire tutte le missioni \n"
                + "nelle varie stanze,\n"
                + "cosi' da ristabilire i comandi persi della\n"
                + "navicella.\n");
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

    public static void main(String[] args) throws IOException {
        //new Thread(new Engine(new PianetaGame())).start();
        //SocketClient sc = new SocketClient();
        Engine engine = new Engine(new PianetaGame());
        switch (engine.socket()) {
            case 0:
                System.out.println("Inizio nuova partita");
                engine.execute();
                break;
            case 1:
                System.out.println("Uscita in corso, addio!!");
                break;
            case 2:
                System.out.println("Caricamento nuova partita");
                break;
        }
    }

    public int socket() {
        try {
            String str;
            ServerSocket ss = new ServerSocket(6666);
            Socket s = ss.accept();
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
                
            out.println("cosa vuoi fare?\t\ts->inizia partita\t\tn->esci\t\tc->carica partita");
            while (true) {
                //viene scritta risposta sul client quindi la leggo
                str = in.readLine();
                if (str.equals("s")) {
                    out.println("Adios!");
                    return 0;
                } else if (str.equals("n")) {
                    out.println("Adios!");
                    return 1;
                } else if (str.equals("c")) {
                    out.println("Adios!");
                    return 2;
                } else {
                    out.println("Comando non riconosciuto");
                }
            }

        } catch (IOException ex) {
            Logger.getLogger(Engine.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }
}
