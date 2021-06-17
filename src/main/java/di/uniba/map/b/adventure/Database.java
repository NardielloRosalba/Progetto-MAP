/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package di.uniba.map.b.adventure;

import di.uniba.map.b.adventure.games.PianetaGame;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import java.util.Scanner;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author Luca
 */
public class Database {

    public final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS game("
            + "username VARCHAR(20) PRIMARY KEY,"
            + "password VARCHAR(20),"
            + "match BLOB not null,"
            + "score int(5))";
    public final Connection conn;

    public Database() throws SQLException {
        conn = DriverManager.getConnection("jdbc:h2:.\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\db");
        create();

    }

    public void create() {

        try {
            Statement stm = conn.createStatement();
            stm.executeUpdate(CREATE_TABLE);
            stm.close();

        } catch (SQLException ex) {
            System.err.println(ex.getErrorCode() + " : " + ex.getMessage());
        }
    }

    public void getInfo() {
        try {
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM game");
            if (rs.isBeforeFirst()) {
                System.out.println("--- NOME \t\t PUNTEGGIO ---");
                while (rs.next()) {
                    System.out.println("  " + rs.getObject(1) + "   \t\t   " + rs.getObject(4));
                }
            } else {
                System.out.println("Non ci sono ancora giocatori registrati");
            }

            stm.close();
        } catch (SQLException ex) {
            System.out.println(ex.getErrorCode() + " : " + ex.getMessage());
        }
    }

    public void delete() throws SQLException {
        Statement stm = conn.createStatement();
        stm.executeUpdate("delete * from game");
        stm.close();
    }

    /*public static void main(String arg[]) throws SQLException {
Database db = new Database();
db.getInfo();
//db.saving();
db.delete();
db.getInfo();

 }*/
    public PianetaGame saving(PianetaGame game) throws SQLException, FileNotFoundException, IOException, ClassNotFoundException {
        Scanner scan = new Scanner(System.in);
        PreparedStatement pstm_user = conn.prepareStatement("SELECT * FROM game where username like ?");//per verificare esistenza di stesso username
        PreparedStatement pstm_user_psw = conn.prepareStatement("SELECT * FROM game where username like ? and password like ?");//per autenticare con username e psw
        PreparedStatement pstm_ins = conn.prepareStatement("INSERT into game VALUES(?,?,?,?)");//per inserire una tupla
        PreparedStatement pstm_alter = conn.prepareStatement("UPDATE game set match = ?, score = ? where username like ?");//per caricare una partita su un giocatore già registrato
        Properties db = new Properties();

        System.out.println("Sei un nuovo utente?");//capisco cosa fare!

        while (scan.hasNextLine()) {
            String command = scan.nextLine();

            if (command.equalsIgnoreCase("si")) {//nuovo utente

                while (true) {
                    this.login(db);//rende username e psw
                    pstm_user.setString(1, db.getProperty("user"));

                    ResultSet rs = pstm_user.executeQuery();
                    if (rs.isBeforeFirst()) {//se utente con stesso user esiste..
                        System.out.println("Username già esistente nel db!\n");
                        System.out.println("\n");

                    } else {
                        pstm_user.close();

                        pstm_ins.setString(1, db.getProperty("user"));
                        pstm_ins.setString(2, db.getProperty("psw"));
                        saving_loading.comandoSalva(game);
                        pstm_ins.setObject(3, game);//carico pianetagame
                        pstm_ins.setInt(4, game.getScore());
                        pstm_ins.executeUpdate();

                        pstm_ins.close();

                        JOptionPane optionPane = new JOptionPane("Salvataggio andato a buon fine!");
                        JDialog dialog = optionPane.createDialog("Salvataggio");

                        dialog.setAlwaysOnTop(true);
                        dialog.setVisible(true);
                        dialog.dispose();
                        break;
                    }
                    break;
                }
                break;
            } else if (command.equalsIgnoreCase("no")) {//chiedo credenziali per salvare partite di utente già registrato
                while (true) {
                    this.login(db);
                    pstm_user_psw.setString(1, db.getProperty("user"));
                    pstm_user_psw.setString(2, db.getProperty("psw"));

                    ResultSet rs = pstm_user_psw.executeQuery();
                    if (rs.next()) {//se utente con stesso user e psw esiste..
                        int score = rs.getInt(4);
                        pstm_user_psw.close();

                        saving_loading.comandoSalva(game);
                        pstm_alter.setObject(1, game);
                        pstm_alter.setInt(2, game.getScore()+score);
                        pstm_alter.setString(3, db.getProperty("user"));
                        
                        pstm_alter.executeUpdate();

                        pstm_alter.close();

                        JOptionPane optionPane = new JOptionPane("Salvataggio andato a buon fine!");
                        JDialog dialog = optionPane.createDialog("Salvataggio");

                        dialog.setAlwaysOnTop(true);
                        dialog.setVisible(true);
                        dialog.dispose();
                        break;

                    } else {
                        System.out.println("Credenziali errate!");
                        System.out.println("\n");
                    }
                    break;
                }
                break;
            } else if (command.equalsIgnoreCase("esci")) {
                break;
            } else {
                System.out.println("Inserire comando valido");
                System.out.println("Sei un nuovo utente?");//capisco cosa fare
                System.out.println("\n");
            }
        }
        return game;
    }

    public PianetaGame loading(PianetaGame game) throws SQLException, IOException, FileNotFoundException, ClassNotFoundException {
        game.stopThread();
        Scanner scanner = new Scanner(System.in);
        PreparedStatement pstm_user_psw = conn.prepareStatement("SELECT * FROM game where username like ? and password like ?");//per autenticare con username e psw
        PianetaGame game_load = null;
        Properties db = new Properties();

        System.out.println("Sei un nuovo utente?");//capisco cosa fare!
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("no")) {
//vuoi caricare partita di giocatore vecchio ok
                while (true) {
                    this.login(db);
                    pstm_user_psw.setString(1, db.getProperty("user"));
                    pstm_user_psw.setString(2, db.getProperty("psw"));

                    ResultSet rs = pstm_user_psw.executeQuery();
                    if (rs.next()) {//se utente con stesso user e psw esiste..

                        saving_loading.comandoSalva(saving_loading.comandoCarica(rs.getBinaryStream(3)));

                        game_load = saving_loading.comandoCarica();

                        JOptionPane optionPane = new JOptionPane("Caricamento andato a buon fine!");
                        JDialog dialog = optionPane.createDialog("Caricamento");

                        dialog.setAlwaysOnTop(true);
                        dialog.setVisible(true);
                        dialog.dispose();
                        pstm_user_psw.close();
                        break;
                    } else {
                        System.out.println("Credenziali errate!\n");
                        System.out.println("\n");
                    }
                }
                break;
            } else if (command.equalsIgnoreCase("si")) {
                System.out.println("devi prima salvarne una!!");
                System.out.println("\n");
                game.checkTimer();
                break;
            } else if (command.equalsIgnoreCase("esci")) {
                game.checkTimer();
                break;
            } else {
                System.out.println("Inserire comando valido");
                System.out.println("Sei un nuovo utente?");//capisco cosa fare!
                System.out.println("\n");
            }
        }

        if (game_load == null) {
            game.checkTimer();
        }
        return game_load;
    }

    public Properties login(Properties db) {

        Scanner scanner = new Scanner(System.in);

        System.out.println("Inserire username..");
        db.setProperty("user", scanner.next());

        System.out.println("Inserire password..");
        db.setProperty("psw", scanner.next());

        System.out.println("");

        return db;
    }

}
