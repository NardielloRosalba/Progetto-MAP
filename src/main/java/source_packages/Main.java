/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package source_packages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Luca
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Menu m = new Menu();
        Input in = new Input();
        Parser parser = new Parser();
        
        int esci;
        String comando_splittato[];

        m.intro();
        m.help();
        
        comando_splittato = parser.analizza_comando(in.InputComando());
        System.out.println("primo comando " + comando_splittato[0] + " secondo comando " + comando_splittato[1]);
    }

}
