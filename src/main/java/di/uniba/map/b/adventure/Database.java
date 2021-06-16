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
            + "match BLOB not null)";
    public final String INSERT = "INSERT INTO game VALUES (?,?,?)";
    public final Connection conn;

    public Database() throws SQLException {
        conn = DriverManager.getConnection("jdbc:h2:.\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\db");
        this.create();

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
                while (rs.next()) {
                    System.out.println(rs.getObject(1) + " -:- " + rs.getObject(2) + " -:- " + rs.getObject(3));
                }
            } else {
                System.out.println("Non ci sono ancora giocatori registrati");
            }

            stm.close();
        } catch (SQLException ex) {
            System.out.println(ex.getErrorCode() + " :2 " + ex.getMessage());
        }
    }

    public void delete() throws SQLException {
        Statement stm = conn.createStatement();
        stm.executeUpdate("delete from game");
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
        PreparedStatement p_stm_user = conn.prepareStatement("SELECT * FROM game where username like ?");//per verificare esistenza di stesso username
        PreparedStatement p_stm_user_psw = conn.prepareStatement("SELECT * FROM game where username like ? and password like ?");//per autenticare con username e psw
        PreparedStatement p_ins = conn.prepareStatement("INSERT into game VALUES(?,?,?)");//per inserire una tupla
        PreparedStatement p_alter = conn.prepareStatement("UPDATE game set match = ? where username like ?");//per caricare una partita su un giocatore già registrato

        Properties db = new Properties();
        System.out.println("Sei un nuovo utente?");//capisco cosa fare!
        while (scan.hasNextLine()) {
            String command = scan.nextLine();

            if (command.equalsIgnoreCase("si")) {//nuovo utente

                while (true) {
                    this.login(db);//rende username e psw
                    p_stm_user.setString(1, db.getProperty("user"));

                    ResultSet rs = p_stm_user.executeQuery();
                    if (rs.isBeforeFirst()) {//se utente con stesso user esiste..
                        System.out.println("Username già esistente nel db!\n");
                        System.out.println("\n");
                        
                    } else {
                        p_ins.setString(1, db.getProperty("user"));
                        p_ins.setString(2, db.getProperty("psw"));
                        saving_loading.comandoSalva(game);
                        p_ins.setObject(3, game);//carico pianetagame
                        p_ins.executeUpdate();
                        p_ins.close();

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
                    p_stm_user_psw.setString(1, db.getProperty("user"));
                    p_stm_user_psw.setString(2, db.getProperty("psw"));

                    ResultSet rs = p_stm_user_psw.executeQuery();
                    if (rs.next()) {//se utente con stesso user e psw esiste..

                        saving_loading.comandoSalva(game);
                        p_alter.setObject(1, game);
                        p_alter.setString(2, db.getProperty("user"));
                        p_alter.executeUpdate();
                        p_alter.close();

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

    public PianetaGame loading(PianetaGame game2) throws SQLException, IOException, FileNotFoundException, ClassNotFoundException {
        game2.stopThread();
        Scanner scan = new Scanner(System.in);
        PreparedStatement p_stm_user_psw = conn.prepareStatement("SELECT * FROM game where username like ? and password like ?");//per autenticare con username e psw
        PianetaGame game = null;
        Properties db = new Properties();

        System.out.println("Sei un nuovo utente?");//capisco cosa fare!
        while (scan.hasNextLine()) {
            String command = scan.nextLine();

            if (command.equalsIgnoreCase("no")) {
                //vuoi caricare partita di giocatore vecchio ok
                while (true) {
                    this.login(db);
                    p_stm_user_psw.setString(1, db.getProperty("user"));
                    p_stm_user_psw.setString(2, db.getProperty("psw"));

                    ResultSet rs = p_stm_user_psw.executeQuery();
                    if (rs.next()) {//se utente con stesso user e psw esiste..
                        saving_loading.comandoSalva(saving_loading.comandoCarica3(rs.getBinaryStream(3)));

                        game = saving_loading.comandoCarica2();
                        JOptionPane optionPane = new JOptionPane("Caricamento andato a buon fine!");
                        JDialog dialog = optionPane.createDialog("Caricamento");

                        dialog.setAlwaysOnTop(true);
                        dialog.setVisible(true);
                        dialog.dispose();
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
                break;
            } else if (command.equalsIgnoreCase("esci")) {
                break;
            } else {
                System.out.println("Inserire comando valido");
                System.out.println("Sei un nuovo utente?");//capisco cosa fare!
                System.out.println("\n");
            }
        }

        if (game == null) {
            game2.checkTimer();
        }
        return game;
    }

    public Properties login(Properties db) {

        Scanner scan = new Scanner(System.in);

        System.out.println("Inserire username..");
        db.setProperty("user", scan.next());
        System.out.println("Inserire password..");
        db.setProperty("psw", scan.next());
        System.out.println("");

        return db;
    }

}
