/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.parser;

import di.uniba.map.b.adventure.Utils;
import di.uniba.map.b.adventure.type.AdvObject;
import di.uniba.map.b.adventure.type.AdvObjectContainer;
import di.uniba.map.b.adventure.type.Command;
import java.util.List;
import java.util.Set;

public class Parser {

    private final int COSTANTE_CONTENITORI = 50;
    private final Set<String> stopwords;

    public Parser(Set<String> stopwords) {
        this.stopwords = stopwords;
    }

    private int checkForCommand(String token, List<Command> commands) {
        for (int i = 0; i < commands.size(); i++) {
            if (commands.get(i).getName().equals(token) || commands.get(i).getAlias().contains(token)) {
                return i;
            }
        }
        return -1;
    }

    private int checkForObject(String token, List<AdvObject> objects) {
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).getName().equals(token) || objects.get(i).getAlias().contains(token)) {
                return i;
            }
        }
        return -1;
    }

    private int checkForObjectContainer(String token, List<AdvObject> objects) {
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i).getName().equals(token) || objects.get(i).getAlias().contains(token)) {
                if (objects.get(i) instanceof AdvObjectContainer) {
                    return i + COSTANTE_CONTENITORI;
                } else {
                    return -1;
                }
            }
        }
        return -1;
    }

    /* ATTENZIONE: il parser è implementato in modo abbastanza independete dalla lingua, ma riconosce solo 
    * frasi semplici del tipo <azione> <oggetto> <oggetto>. Eventuali articoli o preposizioni vengono semplicemente
    * rimossi.
     */
    public ParserOutput parse(String command, List<Command> commands, List<AdvObject> objects, List<AdvObject> inventory) {
        List<String> tokens = Utils.parseString(command, stopwords);
        int io1 = -1;
        int io2 = -1;
        int ioinv1 = -1;
        int ioinv2 = -1;
        if (!tokens.isEmpty()) {
            int ic = checkForCommand(tokens.get(0), commands);
            System.out.println("comando " + tokens.get(0));
            if (ic > -1) {
                if (tokens.size() > 1) { //dopo il comando c'è qualcosa USA TESSERA
                    io1 = checkForObject(tokens.get(1), objects);
                    System.out.println("altro 1 " + tokens.get(1));
                    if (io1 < 0) {
                        ioinv1 = checkForObject(tokens.get(1), inventory); //usa tessera e la tessera sta nell'inventario
                    }
                    if (tokens.size() > 2) {
                        if (io1 < 0 && ioinv1 < 0) {
                            io2 = checkForObjectContainer(tokens.get(2), objects);
                            if (io2 > -1) {
                                AdvObjectContainer contenitore = (AdvObjectContainer) objects.get(io2 - COSTANTE_CONTENITORI);
                                io1 = checkForObject(tokens.get(1), contenitore.getList());
                            } else {
                                return new ParserOutput(null, null);
                            }
                        } else {
                            io2 = checkForObject(tokens.get(2), objects);
                            System.out.println("altro 2 " + tokens.get(2));
                            if (io2 < 0) {
                                ioinv2 = checkForObject(tokens.get(2), inventory);
                            }
                        }
                    }
                    System.out.println("io1 "+ io1
                            + "\n io2 " + io2
                            + "\n inv1 " + ioinv1
                            + "\n inv2 "+ ioinv2
                            + "\n ");
                    if (io1 > -1 && ioinv2 > -1) { //entrambi maggiori oggetto e inventario
                        return new ParserOutput(commands.get(ic), objects.get(io1), inventory.get(ioinv2));
                    } else if (ioinv1 > -1 && io2 > -1) { // entrambi maggiori inventario oggetto
                        return new ParserOutput(commands.get(ic), objects.get(io2), inventory.get(ioinv1));
                    } else if (io1 > -1 && io2 < 0 && ioinv2 < 0) { //maggiore solo oggetto e niente 2 parola
                        return new ParserOutput(commands.get(ic), objects.get(io1), null);
                    } else if (ioinv1 > -1 && io2 < 0 && ioinv2 < 0) { //maggiore solo primo inventario e niente 2 parola
                        return new ParserOutput(commands.get(ic), null, inventory.get(ioinv1));
                    } else if (io2 >= COSTANTE_CONTENITORI && io1 > -1) {
                        AdvObjectContainer contenitore = (AdvObjectContainer) objects.get(io2 - COSTANTE_CONTENITORI);
                        return (new ParserOutput(commands.get(ic), contenitore.getList().get(io1), null, contenitore)); //caso di oggetti nel contenitore
                    } else {
                        return new ParserOutput(commands.get(ic), null, null);
                    }
                } else {
                    return new ParserOutput(commands.get(ic), null, null);
                }
            } else {
                return new ParserOutput(null, null);
            }
        } else {
            return new ParserOutput(null, null);
        }
    }

}
