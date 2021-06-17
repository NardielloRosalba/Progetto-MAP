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
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

/**
 *
 * @author le bimbe di Luca
 */

public class SocketServer {

    //public static void main(String args[]) throws IOException {
    public SocketServer() throws IOException {
        ServerSocket ss = new ServerSocket(6666);
        System.out.println(ss.getLocalPort());
        try (Socket s = ss.accept()) //si mette in ascolto sulla porta specificata e basta
        {
            System.out.println(s.getLocalSocketAddress());
            //ricevuta richiesta stampa indirizzo client+ porta da cui comunica

            BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
            //ho creato uno spazio comune Flush di input
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(s.getOutputStream())), true);
            //spazio comune di output client-server

            /*String str;
            for (int i = 0; i < 10; i++) {
                str = in.readLine();
                System.out.println("stampa buffer socket: " + str);
                out.println(str);
            }*/

        } catch (SocketException ex) {
        }
        ss.close();
    }
}