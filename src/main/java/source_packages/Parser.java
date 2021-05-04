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
public class Parser {

    public Parser() {
    }

    public String[] analizza_comando(String comando) {

        String ex;
        ex = "[a-z]\\s[a-z]?";
        String pos[];
        Pattern pattern = Pattern.compile(ex);
        Matcher match = pattern.matcher(comando);
        pos = comando.split(" ");

        return pos;
    }
}
