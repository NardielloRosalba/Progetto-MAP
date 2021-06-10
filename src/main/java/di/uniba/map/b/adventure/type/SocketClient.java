/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.type;

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

/**
 *
 * @author Luca
 */
public class SocketClient {

    public static void main(String args[]) throws UnknownHostException, IOException {
        //public SocketClient() throws UnknownHostException, IOException {
        String str = "";
        Scanner scan = new Scanner(System.in);
        InetAddress add = InetAddress.getByName("localhost");
        System.out.println(add);//localhost/127.0.0.1
        Socket s = new Socket(add, 6666);
        System.out.println("Creazione socket riuscita");
        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
        //ho creato uno spazio comune Flush di input
        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
        //spazio comune di output client-server
        //leggo dal server
        str = in.readLine();
        
        String ris_serv, ris_cl;//risposta server,risposta client
        while (true) {
            System.out.println(str + "-");//stampa delle possibilità
            //rispondo al server
            ris_cl = scan.next();
            out.println(ris_cl);
            //leggo esito
            ris_serv = in.readLine();
            if (ris_serv.equals("Adios!")) {
                break;
            } else {
                System.out.println(ris_serv);
            }

        }
        s.close();
    }
}
