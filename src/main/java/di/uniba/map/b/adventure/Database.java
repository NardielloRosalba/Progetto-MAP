/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure;

import di.uniba.map.b.adventure.games.PianetaGame;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

/**
 *
 * @author Luca
 */
public class Database {

    public final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS game("
            + "username VARCHAR(20) PRIMARY KEY,"
            + "score INT(3))";
    public final String INSERT = "INSERT INTO game VALUES (?,?)";
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
                    System.out.println(rs.getObject(1) + " -:- " + rs.getObject(2));
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
        stm.executeUpdate("delete from game");
        stm.close();
    }

    /*public static void main(String arg[]) throws SQLException {
        Database db = new Database();
        //db.getInfo();
        //db.saving();
        //db.delete();
        //db.getInfo();
    }*/
    public void saving(PianetaGame game, String str) throws SQLException, FileNotFoundException, IOException, ClassNotFoundException {
        Scanner scan = new Scanner(System.in);
        Statement stm = conn.createStatement();
        PreparedStatement p_stm_user = conn.prepareStatement("SELECT * FROM game where username like ?");//per verificare esistenza di stesso username
        PreparedStatement p_ins = conn.prepareStatement("INSERT into game VALUES(?,?)");//per inserire una tupla
        PreparedStatement p_alter = conn.prepareStatement("UPDATE game set score = ? where username like ?");//per caricare una partita su un giocatore già registrato

        Properties db = new Properties();
        System.out.println("Sei un nuovo utente?");//capisco cosa fare!

        if (scan.hasNext("si") && str.equals("save")) {//nuovo utente

            while (true) {
                this.login(db);//rende username e psw
                p_stm_user.setString(1, db.getProperty("user"));

                ResultSet rs = p_stm_user.executeQuery();
                if (rs.next()) {//se utente con stesso user esiste..
                    System.out.println("Username già esistente nel db!\n");
                } else {
                    p_ins.setString(1, db.getProperty("user"));
                    p_ins.setObject(2, 10);//salvo pianetagame
                    this.writingFile(game);
                    p_ins.executeUpdate();
                    p_ins.close();
                    System.out.println("Credenziali salvate correttamente!\n");
                    break;
                }
            }
        } else if (scan.hasNext("si") && str.equals("load")) {
            System.out.println("devi prima salvarne una!!");
        } else if (scan.hasNext("no") && str.equals("save")) {//chiedo credenziali per salvare partite di utente già registrato

            while (true) {
                this.login(db);
                p_stm_user.setString(1, db.getProperty("user"));

                ResultSet rs = p_stm_user.executeQuery();
                if (rs.next()) {//se utente con stesso user esiste..
                    p_ins.setString(1, db.getProperty("user"));
                    p_ins.setInt(2, 10);//getScore.pianetagame
                    this.writingFile(game);
                    break;
                } else {
                    System.out.println("Credenziali errate!\n");
                }
            }

        } else if (scan.hasNext("no") && str.equals("load")) {//vuoi caricare partita di giocatore vecchio ok
            while (true) {
                this.login(db);
                p_stm_user.setString(1, db.getProperty("user"));
                //p_stm_user_psw.setString(2, db.getProperty("psw"));

                ResultSet rs = p_stm_user.executeQuery();
                if (rs.next()) {//se utente con stesso user esiste..
                    this.readingFile(game);
                    break;
                } else {
                    System.out.println("Credenziali errate!\n");
                }
            }
        }

    }

    public Properties login(Properties db) {

        Scanner scan = new Scanner(System.in);

        System.out.println("Inserire username..");
        db.setProperty("user", scan.next());
        /*System.out.println("Inserire password..");
        db.setProperty("psw", scan.next());*/
        System.out.println("");

        return db;
    }

    public void writingFile(PianetaGame game) throws FileNotFoundException, ClassNotFoundException {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try {
                saving_loading.comandoSalva(game, file);
                System.out.println(file);
                JOptionPane.showMessageDialog(null, "Salvataggio andato a buon fine!", "Salvataggio", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Errore nel salvataggio!", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void readingFile(PianetaGame game) throws FileNotFoundException, ClassNotFoundException {
        JFileChooser fc = new JFileChooser();
        fc.setMultiSelectionEnabled(false);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fc.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            
            File file = fc.getSelectedFile();
            try {
                game = saving_loading.comandoCarica(file);
                System.out.println(file);
                JOptionPane.showMessageDialog(null, "Caricamento andato a buon fine!", "Caricamento", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Errore nel caricamento!", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
