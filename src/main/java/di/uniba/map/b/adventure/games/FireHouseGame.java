/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.games;

import di.uniba.map.b.adventure.GameDescription;
import di.uniba.map.b.adventure.parser.ParserOutput;
import di.uniba.map.b.adventure.type.AdvObject;
import di.uniba.map.b.adventure.type.AdvObjectContainer;
import di.uniba.map.b.adventure.type.Command;
import di.uniba.map.b.adventure.type.CommandType;
import di.uniba.map.b.adventure.type.Room;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ATTENZIONE: La descrizione del gioco è fatta in modo che qualsiasi gioco
 * debba estendere la classe GameDescription. L'Engine è fatto in modo che posso
 * eseguire qualsiasi gioco che estende GameDescription, in questo modo si
 * possono creare più gioci utilizzando lo stesso Engine.
 *
 * Diverse migliorie possono essere applicate: - la descrizione del gioco
 * potrebbe essere caricate da file o da DBMS in modo da non modificare il
 * codice sorgente - l'utilizzo di file e DBMS non è semplice poiché all'interno
 * del file o del DBMS dovrebbe anche essere codificata la logica del gioco
 * (nextMove) oltre alla descrizione di stanze, oggetti, ecc...
 *
 * @author pierpaolo
 */
public class FireHouseGame extends GameDescription {

    @Override
    public void init() throws Exception {
        //Commands
        Command nord = new Command(CommandType.NORD, "nord");
        nord.setAlias(new String[]{"n", "N", "Nord", "NORD"});
        getCommands().add(nord);

        Command iventory = new Command(CommandType.INVENTORY, "inventario");
        iventory.setAlias(new String[]{"inv", "i", "I","zaino"});
        getCommands().add(iventory);

        Command sud = new Command(CommandType.SOUTH, "sud");
        sud.setAlias(new String[]{"s", "S", "Sud", "SUD"});
        getCommands().add(sud);

        Command est = new Command(CommandType.EAST, "est");
        est.setAlias(new String[]{"e", "E", "Est", "EST"});
        getCommands().add(est);

        Command ovest = new Command(CommandType.WEST, "ovest");
        ovest.setAlias(new String[]{"o", "O", "Ovest", "OVEST"});
        getCommands().add(ovest);

        Command end = new Command(CommandType.END, "end");
        end.setAlias(new String[]{"end", "fine", "esci", "muori", "ammazzati", "ucciditi", "suicidati", "exit"});
        getCommands().add(end);

        Command look = new Command(CommandType.LOOK_AT, "osserva");
        look.setAlias(new String[]{"guarda", "vedi", "trova", "cerca", "descrivi", "esamina"});
        getCommands().add(look);

        Command pickup = new Command(CommandType.PICK_UP, "raccogli");
        pickup.setAlias(new String[]{"prendi"});
        getCommands().add(pickup);

        Command open = new Command(CommandType.OPEN, "apri");
        open.setAlias(new String[]{});
        getCommands().add(open);

        Command push = new Command(CommandType.PUSH, "premi");
        push.setAlias(new String[]{"spingi", "attiva"});
        getCommands().add(push);
        //AGGIUNGERE ALTRI COMANDI IN BASE ALLA STORIA!!!!!!!!!!!!!!!
        

        //Stanze lette da file aggiungi try catch
        Scanner fr = new Scanner(new FileReader(".\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\Stanze.txt"));
        String titolo, descrizione = "";
        String[] prova;

        titolo = fr.nextLine();
        prova = fr.nextLine().split("\\.");
        for (String s : prova) {
            descrizione += (s + ".\n");
        }
        Room salaComandi = new Room(0, titolo, descrizione);
        salaComandi.setLook("Attorno a te è tutto buio, solo una luce fioca "
                + "proviene dall'oblo situato a nord. A Sud è presente una porta"
                + " e accanto ad essa un armadio di colore nero. ");

        descrizione = "";
        titolo = fr.nextLine();
        prova = fr.nextLine().split("\\.");
        for (String s : prova) {
            descrizione += (s + ".\n");
        }
        Room corridoioX = new Room(1, titolo, descrizione);
        corridoioX.setLook("E' un semplice corridoio, cosa ti aspettavi?! Continua con le tue missioni!!");
        
        descrizione = "";
        titolo = fr.nextLine();
        prova = fr.nextLine().split("\\.");
        for (String s : prova) {
            descrizione += (s + ".\n");
        }
        Room corridoioSud = new Room(2, titolo, descrizione);
        
        
        descrizione = "";
        titolo = fr.nextLine();
        prova = fr.nextLine().split("\\.");
        for (String s : prova) {
            descrizione += (s + ".\n");
        }
        Room salaElettrica = new Room(3, titolo, descrizione);
        salaElettrica.setLook("Puoi notare una frase sul contatore elettronico "
                + "con su scritto \"Attenzione, non tirare i fili "
                + "perchè trasportano corrente!!\"");
        
        descrizione = "";
        titolo = fr.nextLine();
        prova = fr.nextLine().split("\\.");
        for (String s : prova) {
            descrizione += (s + ".\n");
        }
        Room corridoioNord = new Room(4, titolo, descrizione);

        descrizione = "";
        titolo = fr.nextLine();
        prova = fr.nextLine().split("\\.");
        for (String s : prova) {
            descrizione += (s + ".\n");
        }
        Room infermeria = new Room(5, titolo, descrizione);

        descrizione = "";
        titolo = fr.nextLine();
        prova = fr.nextLine().split("\\.");
        for (String s : prova) {
            descrizione += (s + ".\n");
        }
        Room stanzaTelecomunicazioni = new Room(6, titolo, descrizione);

        descrizione = "";
        titolo = fr.nextLine();
        prova = fr.nextLine().split("\\.");
        for (String s : prova) {
            descrizione += (s + ".\n");
        }
        Room stanzaEsterna = new Room(7, titolo, descrizione);

        //maps
        salaComandi.setSouth(corridoioX);

        corridoioX.setWest(salaComandi);
        corridoioX.setNorth(corridoioNord);
        corridoioX.setSouth(corridoioSud);
        corridoioX.setEast(stanzaTelecomunicazioni);

        corridoioNord.setEast(infermeria);
        corridoioNord.setSouth(corridoioX);

        infermeria.setSouth(stanzaTelecomunicazioni);
        infermeria.setWest(corridoioNord);

        salaElettrica.setEast(corridoioSud);

        corridoioSud.setWest(salaElettrica);
        corridoioSud.setNorth(corridoioX);

        stanzaTelecomunicazioni.setEast(corridoioX);
        stanzaTelecomunicazioni.setNorth(infermeria);
        stanzaTelecomunicazioni.setSouth(stanzaEsterna);
        stanzaTelecomunicazioni.setWest(corridoioX);
        
        stanzaEsterna.setNorth(stanzaTelecomunicazioni);

        getRooms().add(salaComandi);
        getRooms().add(corridoioX);
        getRooms().add(corridoioSud);
        getRooms().add(salaElettrica);
        getRooms().add(infermeria);
        getRooms().add(corridoioNord);
        getRooms().add(stanzaTelecomunicazioni);
        getRooms().add(stanzaEsterna);
        
        //obejcts
        //AdvObjectContainer armadio = new AdvObjectContainer(1, );
/*
        AdvObject torcia = new AdvObject(0, "torcia", "Un pacco di batterie, chissà se sono cariche.");
        AdvObject chiave = new AdvObject(1, "torcia", "Un pacco di batterie, chissà se sono cariche.");
        AdvObject colla = new AdvObject(2, "torcia", "Un pacco di batterie, chissà se sono cariche.");
        AdvObject pozione1 = new AdvObject(3, "pozione", "Un pacco di batterie, chissà se sono cariche.");
        AdvObject battery = new AdvObject(0, "torcia", "Un pacco di batterie, chissà se sono cariche.");
        
        battery.setAlias(new String[]{"batterie", "pile", "pila"});
        bathroom.getObjects().add(battery);
        AdvObjectContainer wardrobe = new AdvObjectContainer(2, "armadio", "Un semplice armadio.");
        wardrobe.setAlias(new String[]{"guardaroba", "vestiario"});
        wardrobe.setOpenable(true);
        wardrobe.setPickupable(false);
        wardrobe.setOpen(false);
        yourRoom.getObjects().add(wardrobe);
        AdvObject toy = new AdvObject(3, "giocattolo", "Il gioco che ti ha regalato zia Lina.");
        toy.setAlias(new String[]{"gioco", "robot"});
        toy.setPushable(true);
        toy.setPush(false);
        wardrobe.add(toy);
         */
        //set starting room
        setCurrentRoom(salaComandi);
    }

    @Override
    public void nextMove(ParserOutput p, PrintStream out) {
        if (p.getCommand() == null) {
            out.println("Non ho capito cosa devo fare! Prova con un altro comando.");
        } else {
            //move
            boolean noroom = false;
            boolean move = false;
            if (p.getCommand().getType() == CommandType.NORD) {
                if (getCurrentRoom().getNorth() != null) {
                    setCurrentRoom(getCurrentRoom().getNorth());
                    move = true;
                } else {
                    noroom = true;
                }
            } else if (p.getCommand().getType() == CommandType.SOUTH) {
                if (getCurrentRoom().getSouth() != null) {
                    setCurrentRoom(getCurrentRoom().getSouth());
                    move = true;
                } else {
                    noroom = true;
                }
            } else if (p.getCommand().getType() == CommandType.EAST) {
                if (getCurrentRoom().getEast() != null) {
                    setCurrentRoom(getCurrentRoom().getEast());
                    move = true;
                } else {
                    noroom = true;
                }
            } else if (p.getCommand().getType() == CommandType.WEST) {
                if (getCurrentRoom().getWest() != null) {
                    setCurrentRoom(getCurrentRoom().getWest());
                    move = true;
                } else {
                    noroom = true;
                }
            } else if (p.getCommand().getType() == CommandType.INVENTORY) {
                out.println("Nel tuo inventario ci sono:");
                for (AdvObject o : getInventory()) {
                    out.println(o.getName() + ": " + o.getDescription());
                }
            } else if (p.getCommand().getType() == CommandType.LOOK_AT) {
                if (getCurrentRoom().getLook() != null) {
                    out.println(getCurrentRoom().getLook());
                } else {
                    out.println("Non c'è niente di interessante qui.");
                }
            } else if (p.getCommand().getType() == CommandType.PICK_UP) {
                if (p.getObject() != null) {
                    if (p.getObject().isPickupable()) {
                        getInventory().add(p.getObject());
                        getCurrentRoom().getObjects().remove(p.getObject());
                        out.println("Hai raccolto: " + p.getObject().getDescription());
                    } else {
                        out.println("Non puoi raccogliere questo oggetto.");
                    }
                } else {
                    out.println("Non c'è niente da raccogliere qui.");
                }
            } else if (p.getCommand().getType() == CommandType.OPEN) {
                /*ATTENZIONE: quando un oggetto contenitore viene aperto, tutti gli oggetti contenuti
                * vengongo inseriti nella stanza o nell'inventario a seconda di dove si trova l'oggetto contenitore.
                * Questa soluzione NON va bene poiché quando un oggetto contenitore viene richiuso è complicato
                * non rendere più disponibili gli oggetti contenuti rimuovendoli dalla stanza o dall'invetario.
                * Trovare altra soluzione.
                 */
                if (p.getObject() == null && p.getInvObject() == null) {
                    out.println("Non c'è niente da aprire qui.");
                } else {
                    if (p.getObject() != null) {
                        if (p.getObject().isOpenable() && p.getObject().isOpen() == false) {
                            if (p.getObject() instanceof AdvObjectContainer) {
                                out.println("Hai aperto: " + p.getObject().getName());
                                AdvObjectContainer c = (AdvObjectContainer) p.getObject();
                                if (!c.getList().isEmpty()) {
                                    out.print(c.getName() + " contiene:");
                                    Iterator<AdvObject> it = c.getList().iterator();
                                    while (it.hasNext()) {
                                        AdvObject next = it.next();
                                        getCurrentRoom().getObjects().add(next);
                                        out.print(" " + next.getName());
                                        it.remove();
                                    }
                                    out.println();
                                }
                            } else {
                                out.println("Hai aperto: " + p.getObject().getName());
                                p.getObject().setOpen(true);
                            }
                        } else {
                            out.println("Non puoi aprire questo oggetto.");
                        }
                    }
                    if (p.getInvObject() != null) {
                        if (p.getInvObject().isOpenable() && p.getInvObject().isOpen() == false) {
                            if (p.getInvObject() instanceof AdvObjectContainer) {
                                AdvObjectContainer c = (AdvObjectContainer) p.getInvObject();
                                if (!c.getList().isEmpty()) {
                                    out.print(c.getName() + " contiene:");
                                    Iterator<AdvObject> it = c.getList().iterator();
                                    while (it.hasNext()) {
                                        AdvObject next = it.next();
                                        getInventory().add(next);
                                        out.print(" " + next.getName());
                                        it.remove();
                                    }
                                    out.println();
                                }
                            } else {
                                p.getInvObject().setOpen(true);
                            }
                            out.println("Hai aperto nel tuo inventario: " + p.getInvObject().getName());
                        } else {
                            out.println("Non puoi aprire questo oggetto.");
                        }
                    }
                }
            } else if (p.getCommand().getType() == CommandType.PUSH) {
                //ricerca oggetti pushabili
                if (p.getObject() != null && p.getObject().isPushable()) {
                    out.println("Hai premuto: " + p.getObject().getName());
                    if (p.getObject().getId() == 3) {
                        end(out);
                    }
                } else if (p.getInvObject() != null && p.getInvObject().isPushable()) {
                    out.println("Hai premuto: " + p.getInvObject().getName());
                    if (p.getInvObject().getId() == 3) {
                        end(out);
                    }
                } else {
                    out.println("Non ci sono oggetti che puoi premere qui.");
                }
            }
            if (noroom) {
                out.println("Da quella parte non si può andare c'è un muro! Non hai ancora acquisito i poteri per oltrepassare i muri...");
            } else if (move) {
                out.println(getCurrentRoom().getName());
                out.println("================================================");
                out.println(getCurrentRoom().getDescription());
            }
        }
    }

    private void end(PrintStream out) {
        out.println("Premi il pulsante del giocattolo e in seguito ad una forte esplosione la tua casa prende fuoco...tu e tuoi famigliari cercate invano di salvarvi e venite avvolti dalle fiamme...è stata una morte CALOROSA...addio!");
        System.exit(0);
    }
}
