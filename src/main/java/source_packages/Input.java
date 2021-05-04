
package source_packages;
import java.util.Scanner;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public class Input {

    String comando;
    Scanner input = new Scanner(System.in);
    
    public Input(){
    }
    
    public String InputComando(){
        System.out.println("Inserire un comando");
        comando = input.nextLine();
        return comando;
    }
}
