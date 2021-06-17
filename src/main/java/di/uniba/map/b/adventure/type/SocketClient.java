/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.type;

import di.uniba.map.b.adventure.interfacee.Inizio;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 *
 * @author gruppo 'Le bimbe di Luca'
 */

/**
 *
 * @author Luca
 */
public class SocketClient {

    public static void main(String args[]) throws UnknownHostException, IOException {

        Scanner scan = new Scanner(System.in);
        InetAddress add = InetAddress.getByName("localhost");
        try ( Socket s = new Socket(add, 6666)) {
            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);

            Inizio interfacciaInizio = new Inizio(out);
            interfacciaInizio.setVisible(true);
            JOptionPane.showMessageDialog(null, "Connessione avvenuta con successo", "Connessione socket", JOptionPane.INFORMATION_MESSAGE);
            
            String ris_serv;//risposta server,risposta client
            while (true) {
                //System.out.println(str + "-");//stampa delle possibilità
                //ris_cl = scan.next();
                ris_serv = in.readLine();//qua scrivo credenziali utente ottenute dal login +"!"
                JOptionPane.showMessageDialog(null, ris_serv, "Risposta server", JOptionPane.INFORMATION_MESSAGE);
                if (ris_serv.endsWith("!")) {
                    break;
                }
            }
        }
    }
}
