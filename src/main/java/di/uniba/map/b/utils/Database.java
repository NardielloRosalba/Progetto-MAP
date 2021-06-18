/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package di.uniba.map.b.utils;

import di.uniba.map.b.adventure.games.PianetaGame;
import di.uniba.map.b.utils.User;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author le bimbe di Luca
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
                Set<User> users = new LinkedHashSet();
                System.out.println("Utenti Registrati: \n");
                System.out.println("--- NOME \t\t PUNTEGGIO ---");
                while (rs.next()) {
                    System.out.println("  " + rs.getObject(1) + "   \t\t   " + rs.getObject(4));
                    User user = new User(rs.getString(1), rs.getInt(4));
                    users.add(user);
                }
                print(users);
            } else {
                System.out.println("Non ci sono ancora giocatori registrati");
            }

            stm.close();
        } catch (SQLException ex) {
            System.out.println(ex.getErrorCode() + " : " + ex.getMessage());
        }
    }

    public PianetaGame saving(PianetaGame game) throws SQLException, FileNotFoundException, IOException, ClassNotFoundException {
        Scanner scan = new Scanner(System.in);
        PreparedStatement pstm_user = conn.prepareStatement("SELECT * FROM game where username like ?");
        PreparedStatement pstm_user_psw = conn.prepareStatement("SELECT * FROM game where username like ? and password like ?");
        PreparedStatement pstm_ins = conn.prepareStatement("INSERT into game VALUES(?,?,?,?)");
        PreparedStatement pstm_alter = conn.prepareStatement("UPDATE game set match = ?, score = ? where username like ?");
        Properties db = new Properties();

        System.out.println("Sei un nuovo utente?");

        while (scan.hasNextLine()) {
            String command = scan.nextLine();

            if (command.equalsIgnoreCase("si")) {

                while (true) {
                    this.login(db);
                    pstm_user.setString(1, db.getProperty("user"));

                    ResultSet rs = pstm_user.executeQuery();
                    if (rs.isBeforeFirst()) {
                        System.out.println("Username già esistente nel db!\n");
                        System.out.println("\n");

                    } else {
                        pstm_user.close();

                        pstm_ins.setString(1, db.getProperty("user"));
                        pstm_ins.setString(2, db.getProperty("psw"));
                        saving_loading.comandoSalva(game);
                        pstm_ins.setObject(3, game);
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
            } else if (command.equalsIgnoreCase("no")) {
                while (true) {
                    this.login(db);
                    pstm_user_psw.setString(1, db.getProperty("user"));
                    pstm_user_psw.setString(2, db.getProperty("psw"));

                    ResultSet rs = pstm_user_psw.executeQuery();
                    if (rs.next()) {
                        int score = rs.getInt(4);
                        pstm_user_psw.close();

                        saving_loading.comandoSalva(game);
                        pstm_alter.setObject(1, game);
                        pstm_alter.setInt(2, game.getScore() + score);
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
                System.out.println("Sei un nuovo utente?");
                System.out.println("\n");
            }
        }
        return game;
    }

    public PianetaGame loading(PianetaGame game) throws SQLException, IOException, FileNotFoundException, ClassNotFoundException {
        game.stopThread();
        Scanner scanner = new Scanner(System.in);
        PreparedStatement pstm_user_psw = conn.prepareStatement("SELECT * FROM game where username like ? and password like ?");
        PreparedStatement pstm_user = conn.prepareStatement("SELECT * FROM game");
        PianetaGame game_load = null;
        Properties db = new Properties();

        System.out.println("Sei un nuovo utente?");
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();

            if (command.equalsIgnoreCase("no")) {
                while (true) {
                    ResultSet rs = pstm_user.executeQuery();
                    if (rs.next()) {
                        this.login(db);
                        pstm_user_psw.setString(1, db.getProperty("user"));
                        pstm_user_psw.setString(2, db.getProperty("psw"));
                        rs = pstm_user_psw.executeQuery();
                        if (rs.next()) {

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
                    } else {
                        System.out.println("Non ci sono utenti registrati");
                        break;
                    }
                }
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
                System.out.println("Sei un nuovo utente?");
                System.out.println("\n");
            }
            break;
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

    private void print(Set<User> users) {
        System.out.println(""
                + "_________ PRINCIPIANTE __________\n"
                + "(punteggio compreso tra (" + 0 + " e " + 910 + ")\n");
        printPersonWithPredicateConsumer(users, (User u) -> u.getScore() >= 0 && u.getScore() <= 910, u -> System.out.println(u));
        System.out.println(""
                + "_________ INTERMEDIO ____________\n"
                + "(punteggio compreso tra (" + 911 + " e " + 1820 + ")\n");
        printPersonWithPredicateConsumer(users, (User u) -> u.getScore() >= 911 && u.getScore() <= 1820, u -> System.out.println(u));
        System.out.println(""
                + "__________ AVANZATO  ____________\n"
                + "(punteggio compreso tra (" + 1821 + " e " + 2730 + ")\n");
        printPersonWithPredicateConsumer(users, (User u) -> u.getScore() >= 1821 && u.getScore() <= 2730, u -> System.out.println(u));
        int max = users
                .stream()
                .mapToInt(u -> u.getScore())
                .max().getAsInt();
        System.out.print("L'utente con il punteggio MASSIMO ( " + max + " )e':\n");

        printPersonWithPredicateConsumerFunction(users, u -> u.getScore() == max, u -> u.getName(), name -> System.out.println(name));
    }

    public static void printPersonWithPredicateConsumerFunction(Set<User> users, Predicate<User> tester, Function<User, String> mapper, Consumer<String> operation) {
        for (User u : users) {
            if (tester.test(u)) {
                String data = mapper.apply(u);
                operation.accept(data);
            }
        }
    }

    public static void printPersonWithPredicateConsumer(Set<User> users, Predicate<User> tester, Consumer<User> operation) {
        for (User u : users) {
            if (tester.test(u)) {
                operation.accept(u);
            }
        }
    }

}
