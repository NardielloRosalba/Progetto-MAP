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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

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
public class PianetaGame extends GameDescription implements Serializable {

    private final int ID_ROOM_SALA_COMANDI = 0;
    private final int ID_ROOM_CORRIDOIO_X = 1;
    private final int ID_ROOM_CORRIDOIO_SUD = 2;
    private final int ID_ROOM_SALA_ELETTRICA = 3;
    private final int ID_ROOM_CORRIDOIO_NORD = 4;
    private final int ID_ROOM_INFERMERIA = 5;
    private final int ID_ROOM_SALA_TELECOMUNICAZIONI = 6;
    private final int ID_ROOM_SALA_ESTERNA = 7;

    private final int ID_OBJECT_ARMADIO = 0;
    private final int ID_OBJECT_TESSERA = 1;
    private final int ID_OBJECT_PORTA = 2;
    private final int ID_OBJECT_TORCIA = 3;
    private final int ID_OBJECT_CONTATORE = 4;
    private final int ID_OBJECT_LUCCHETTO = 5;
    private final int ID_OBJECT_PARETE = 6;
    private final int ID_OBJECT_CHIAVE = 7;
    private final int ID_OBJECT_LEVA = 8;
    private final int ID_OBJECT_COLLA = 9;
    private final int ID_OBJECT_CREPA = 10;
    private final int ID_OBJECT_CASSETTA = 11;
    private final int ID_OBJECT_PROTOCOLLI = 12;
    private final int ID_OBJECT_FORBICI = 13;
    private final int ID_OBJECT_BISTURI = 14;
    private final int ID_OBJECT_GARZE = 15;
    private final int ID_OBJECT_LACCIO = 16;
    private final int ID_OBJECT_LETTINO = 17;
    private final int ID_OBJECT_TAVOLO = 18;
    private final int ID_OBJECT_LIBRO = 19;
    private final int ID_OBJECT_FIALA1 = 20;
    private final int ID_OBJECT_FIALA2 = 21;
    private final int ID_OBJECT_FIALA3 = 22;
    private final int ID_OBJECT_FIALA4 = 23;

    private boolean eventTorcia = false;
    private boolean eventTorciaAccesa = false;
    private boolean eventAvvisoCrepa = false;

    private boolean missioneCorrente = false;
    private boolean missioneCrepa = false;
    private Thread timerGasTossico = new TimerGasTossico();

    private boolean timerActived = false;

    public static class TimerGasTossico extends Thread implements Serializable {

        int countDown = 10;
        private int taskCount = 0;

        public TimerGasTossico() {
        }

        @Override
        public void run() {
            while (this.taskCount != countDown) {
                try {            
                    System.out.println(avviso(this.countDown - this.taskCount));
                    Thread.sleep(5000);
                    this.taskCount++;
                } catch (InterruptedException ex) {
                    System.out.println("Ti sei salvato la vita!");
                    return;
                }
            }
            new PianetaGame().end(new StringBuilder("Sei morto!"));
        }

        public String avviso(int n) {
            if (n != 1) {
                return ("- Mancano " + (this.countDown - this.taskCount) + " minuti prima di morire a causa del gas!");
            } else {
                return ("- Manca " + (this.countDown - this.taskCount) + " minuto prima di morire a causa del gas!");
            }
        }
}

@Override
public void init() throws FileNotFoundException {
        //Commands
        Command nord = new Command(CommandType.NORD, "nord");
        nord.setAlias(new String[]{"n", "N", "Nord", "NORD"});
        getCommands().add(nord);

        Command inventory = new Command(CommandType.INVENTORY, "inventario");
        inventory.setAlias(new String[]{"inv", "i", "I"});
        getCommands().add(inventory);

        Command sud = new Command(CommandType.SOUTH, "sud");
        sud.setAlias(new String[]{"s", "S", "Sud", "SUD"});
        getCommands().add(sud);

        Command est = new Command(CommandType.EAST, "est");
        est.setAlias(new String[]{"e", "E", "Est", "EST"});
        getCommands().add(est);

        Command ovest = new Command(CommandType.WEST, "ovest");
        ovest.setAlias(new String[]{"o", "Ovest", "OVEST"});
        getCommands().add(ovest);

        Command save = new Command(CommandType.SAVE, "save");
        save.setAlias(new String[]{"SALVA", "salva"});
        getCommands().add(save);

        Command load = new Command(CommandType.LOAD, "load");
        load.setAlias(new String[]{"CARICA", "carica"});
        getCommands().add(load);

        Command end = new Command(CommandType.END, "end");
        end.setAlias(new String[]{"end", "fine", "esci", "muori", "ammazzati", "ucciditi", "suicidati", "exit"});
        getCommands().add(end);

        Command look = new Command(CommandType.LOOK_AT, "osserva");
        look.setAlias(new String[]{"guarda", "vedi", "trova", "cerca", "descrivi"});
        getCommands().add(look);

        Command pickup = new Command(CommandType.PICK_UP, "raccogli");
        pickup.setAlias(new String[]{"prendi"});
        getCommands().add(pickup);

        Command putDown = new Command(CommandType.PUT_DOWN, "lascia");
        putDown.setAlias(new String[]{"posa"});
        getCommands().add(putDown);

        Command turnOn = new Command(CommandType.TURN_ON, "accendi");
        turnOn.setAlias(new String[]{"attiva"});
        getCommands().add(turnOn);

        Command turnOff = new Command(CommandType.TURN_OFF, "spegni");
        turnOff.setAlias(new String[]{"disattiva"});
        getCommands().add(turnOff);

        Command open = new Command(CommandType.OPEN, "apri");
        open.setAlias(new String[]{"aprire"});
        getCommands().add(open);

        Command push = new Command(CommandType.PUSH, "premi");
        push.setAlias(new String[]{"spingi"});
        getCommands().add(push);

        Command use = new Command(CommandType.USE, "usa");
        use.setAlias(new String[]{"utilizza", "striscia", "ripara"});
        getCommands().add(use);
        
        Command drink = new Command(CommandType.DRINK, "bevi");
        drink.setAlias(new String[]{"ingoia"});
        getCommands().add(drink);

        Command pull = new Command(CommandType.PULL, "tira");
        pull.setAlias(new String[]{"alza", "solleva"});
        getCommands().add(pull);

        //Stanze lette da file aggiungi try catch
        Scanner fr;
        fr = new Scanner(new FileReader(".\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\Stanze.txt"));
        String titolo, descrizione = "";
        String[] prova;
        titolo = fr.nextLine();
        prova = fr.nextLine().split("\\.");
        for (String s : prova) {
            descrizione += (s + ".\n");
        }
        Room salaComandi = new Room(ID_ROOM_SALA_COMANDI, titolo, descrizione);
        //salaComandi.setLook("Sei nel corridoio, a nord vedi il bagno, a sud il soggiorno e ad ovest la tua cameretta, forse il gioco sarà lì?");

        descrizione = "";
        titolo = fr.nextLine();
        prova = fr.nextLine().split("\\.");
        for (String s : prova) {
            descrizione += (s + ".\n");
        }
        Room corridoioX = new Room(ID_ROOM_CORRIDOIO_X, titolo, descrizione);

        descrizione = "";
        titolo = fr.nextLine();
        prova = fr.nextLine().split("\\.");
        for (String s : prova) {
            descrizione += (s + ".\n");
        }
        Room corridoioSud = new Room(ID_ROOM_CORRIDOIO_SUD, titolo, descrizione);

        descrizione = "";
        titolo = fr.nextLine();
        prova = fr.nextLine().split("\\.");
        for (String s : prova) {
            descrizione += (s + ".\n");
        }
        Room salaElettrica = new Room(ID_ROOM_SALA_ELETTRICA, titolo, descrizione);

        descrizione = "";
        titolo = fr.nextLine();
        prova = fr.nextLine().split("\\.");
        for (String s : prova) {
            descrizione += (s + ".\n");
        }
        Room corridoioNord = new Room(ID_ROOM_CORRIDOIO_NORD, titolo, descrizione);

        descrizione = "";
        titolo = fr.nextLine();
        prova = fr.nextLine().split("\\.");
        for (String s : prova) {
            descrizione += (s + ".\n");
        }
        Room infermeria = new Room(ID_ROOM_INFERMERIA, titolo, descrizione);

        descrizione = "";
        titolo = fr.nextLine();
        prova = fr.nextLine().split("\\.");
        for (String s : prova) {
            descrizione += (s + ".\n");
        }
        Room stanzaTelecomunicazioni = new Room(ID_ROOM_SALA_TELECOMUNICAZIONI, titolo, descrizione);

        descrizione = "";
        titolo = fr.nextLine();
        prova = fr.nextLine().split("\\.");
        for (String s : prova) {
            descrizione += (s + ".\n");
        }
        Room stanzaEsterna = new Room(ID_ROOM_SALA_ESTERNA, titolo, descrizione);

        // Room yourRoom = new Room(4, "La tua cameratta", "Finalmente la tua cameretta! Questo luogo ti è così famigliare...ma non ricordi dove hai messo il nuovo regalo di zia Lina.");
        //yourRoom.setLook("C'è un armadio bianco, di solito conservi lì i tuoi giochi.");
        //maps
        salaComandi.setSouth(corridoioX);
        salaComandi.setLock(true);
        salaComandi.setLook(salaComandi.getDescription());

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
        Scanner obj;
        obj = new Scanner(new FileReader(".\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\Oggetti.txt"));

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObjectContainer armadio = new AdvObjectContainer(ID_OBJECT_ARMADIO, titolo, descrizione);
        armadio.setAlias(new String[]{"armadio"});
        armadio.setPickupable(false);
        armadio.setOpenable(true);
        salaComandi.getObjects().add(armadio);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject tessera = new AdvObject(ID_OBJECT_TESSERA, titolo, descrizione);
        tessera.setAlias(new String[]{"tessera", "carta", "pass"});
        //tessera.setPushable(true);
        tessera.setPickupable(true);
        tessera.setUsable(true);
        armadio.getList().add(tessera);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject protocolli = new AdvObject(ID_OBJECT_PROTOCOLLI, titolo, descrizione);
        protocolli.setAlias(new String[]{"protocolli", "protocolliNavigazione", "fogliNavigazione", "fogli"});
        protocolli.setPickupable(true);
        armadio.getList().add(protocolli);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject torcia = new AdvObject(ID_OBJECT_TORCIA, titolo, descrizione);
        torcia.setAlias(new String[]{"oggetto", "torcia"});
        torcia.setPickupable(true);
        torcia.setSiAccende(true);
        torcia.setAcceso(false);
        corridoioX.getObjects().add(torcia);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject porta = new AdvObject(ID_OBJECT_PORTA, titolo, descrizione);
        //porta.setOpenable(true);
        //porta.setOpen(false);
        porta.setAlias(new String[]{"porta", "portone", "porta sala"});
        salaComandi.getObjects().add(porta);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject contatore = new AdvObject(ID_OBJECT_CONTATORE, titolo, descrizione);
        contatore.setAlias(new String[]{"contatore", "contatoreElettrico"});
        salaElettrica.getObjects().add(contatore);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject lucchetto = new AdvObject(ID_OBJECT_LUCCHETTO, titolo, descrizione);
        lucchetto.setAlias(new String[]{"lucchetto"});
        lucchetto.setPickupable(false);
        lucchetto.setOpenable(true);
        lucchetto.setVisibile(false);
        salaElettrica.getObjects().add(lucchetto);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject parete = new AdvObject(ID_OBJECT_PARETE, titolo, descrizione);
        parete.setAlias(new String[]{"PareteFili", "parete", "fili"});
        salaElettrica.getObjects().add(parete);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject chiave = new AdvObject(ID_OBJECT_CHIAVE, titolo, descrizione);
        chiave.setAlias(new String[]{"chiave"});
        chiave.setPickupable(false);
        chiave.setVisibile(false);
        salaElettrica.getObjects().add(chiave);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject leva = new AdvObject(ID_OBJECT_LEVA, titolo, descrizione);
        leva.setAlias(new String[]{"leva"});
        leva.setPickupable(false);
        leva.setPullable(true);
        leva.setVisibile(false);
        salaElettrica.getObjects().add(leva);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObjectContainer cassetta = new AdvObjectContainer(ID_OBJECT_CASSETTA, titolo, descrizione);
        cassetta.setAlias(new String[]{"cassetta", "cassettaProntoSoccorso"});
        cassetta.setPickupable(false);
        cassetta.setOpenable(true);
        infermeria.getObjects().add(cassetta);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject colla = new AdvObject(ID_OBJECT_COLLA, titolo, descrizione);
        colla.setAlias(new String[]{"collaIndustiale", "superColla", "colla"});
        colla.setUsable(true);
        cassetta.getList().add(colla);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject forbici = new AdvObject(ID_OBJECT_FORBICI, titolo, descrizione);
        forbici.setAlias(new String[]{"forbici"});
        cassetta.getList().add(forbici);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject bisturi = new AdvObject(ID_OBJECT_BISTURI, titolo, descrizione);
        bisturi.setAlias(new String[]{"bisturi"});
        cassetta.getList().add(bisturi);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject garze = new AdvObject(ID_OBJECT_GARZE, titolo, descrizione);
        garze.setAlias(new String[]{"garze"});
        cassetta.getList().add(garze);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject laccio = new AdvObject(ID_OBJECT_LACCIO, titolo, descrizione);
        laccio.setAlias(new String[]{"laccio", "laccio emostatico"});
        cassetta.getList().add(laccio);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject crepa = new AdvObject(ID_OBJECT_CREPA, titolo, descrizione);
        crepa.setAlias(new String[]{"buco", "crepa"});
        crepa.setPickupable(false);
        corridoioNord.getObjects().add(crepa);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject lettino = new AdvObject(ID_OBJECT_LETTINO, titolo, descrizione);
        lettino.setAlias(new String[]{"lettino", "lettinoVisita", "lettoVisita", "letto"});
        lettino.setPickupable(false);
        infermeria.getObjects().add(lettino);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObjectContainer tavolo = new AdvObjectContainer(ID_OBJECT_TAVOLO, titolo, descrizione);
        tavolo.setAlias(new String[]{"tavolo", "tavolino"});
        tavolo.setPickupable(false);
        infermeria.getObjects().add(tavolo);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject libro = new AdvObject(ID_OBJECT_LIBRO, titolo, descrizione);
        libro.setAlias(new String[]{"libro", "libroFiale"});
        tavolo.getList().add(libro); //aggiungere comando leggi libro/ osserva libro

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject fiala1 = new AdvObject(ID_OBJECT_FIALA1, titolo, descrizione);
        fiala1.setDrinkable(true);
        fiala1.setAlias(new String[]{"fiala1", "pozione1", "fialaAcqua"});
        tavolo.getList().add(fiala1);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject fiala2 = new AdvObject(ID_OBJECT_FIALA2, titolo, descrizione);
        fiala2.setDrinkable(true);
        fiala2.setAlias(new String[]{"fiala2", "pozione2", "fialaAnimali"});
        tavolo.getList().add(fiala2);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject fiala3 = new AdvObject(ID_OBJECT_FIALA3, titolo, descrizione);
        fiala3.setAlias(new String[]{"fiala3", "pozione3", "fialagas", "fialaGasTossici"});
        fiala3.setDrinkable(true);
        tavolo.getList().add(fiala3);

        titolo = obj.nextLine();
        descrizione = obj.nextLine();
        AdvObject fiala4 = new AdvObject(ID_OBJECT_FIALA4, titolo, descrizione);
        fiala4.setDrinkable(true);
        fiala4.setAlias(new String[]{"fiala4", "pozione4", "fialaPensiero"});
        tavolo.getList().add(fiala4);

        //AGGIUNGO COMBINAZIONI PER OGNI STANZA
        salaComandi.addCombinazioni(porta, tessera);
        salaElettrica.addCombinazioni(chiave, lucchetto);
        corridoioNord.addCombinazioni(colla, crepa);

        //DESCRIZIONI STANZE IN SEGUITO AL COMANDO OSSERVA
        Scanner os;
        os = new Scanner(new FileReader(".\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\Osserva.txt"));
        String[] osserva;

        descrizione = "";
        osserva = os.nextLine().split("\\.");
        for (String s : osserva) {
            descrizione += (s + ".\n");
        }
        salaComandi.setLook(descrizione);

        descrizione = "";
        osserva = os.nextLine().split("\\.");
        for (String s : osserva) {
            descrizione += (s + ".\n");
        }
        corridoioX.setLook(descrizione);

        descrizione = "";
        osserva = os.nextLine().split("\\.");
        for (String s : osserva) {
            descrizione += (s + ".\n");
        }
        corridoioNord.setLook(descrizione);

        descrizione = "";
        osserva = os.nextLine().split("\\.");
        for (String s : osserva) {
            descrizione += (s + ".\n");
        }
        infermeria.setLook(descrizione);

        descrizione = "";
        osserva = os.nextLine().split("\\.");
        for (String s : osserva) {
            descrizione += (s + ".\n");
        }
        salaElettrica.setLook(descrizione);

        descrizione = "";
        osserva = os.nextLine().split("\\.");
        for (String s : osserva) {
            descrizione += (s + ".\n");
        }
        corridoioSud.setLook(descrizione);

        descrizione = "";
        osserva = os.nextLine().split("\\.");
        for (String s : osserva) {
            descrizione += (s + ".\n");
        }
        stanzaTelecomunicazioni.setLook(descrizione);

        descrizione = "";
        osserva = os.nextLine().split("\\.");
        for (String s : osserva) {
            descrizione += (s + ".\n");
        }
        stanzaEsterna.setLook(descrizione);

        fr.close();
        obj.close();
        os.close();
        setCurrentRoom(salaComandi);
    }

    @Override
public String nextMove(ParserOutput p) {
        StringBuilder output = new StringBuilder();

        if (p.getCommand() == null) {
            output.append("Non ho capito cosa devo fare! Prova con un altro comando.");
        } else {

            if (null != p.getCommand().getType()) {
                switch (p.getCommand().getType()) {
                    case NORD:
                        output = comandoNord(p, output);
                        break;
                    case SOUTH:
                        output = comandoSud(p, output);
                        break;
                    case EAST:
                        output = comandoEst(p, output);
                        break;
                    case WEST:
                        output = comandoOvest(p, output);
                        break;
                    case INVENTORY:
                        output = comandoInventory(p, output);
                        break;
                    case LOOK_AT:
                        if ((getCurrentRoom().getId() == ID_ROOM_SALA_COMANDI || getCurrentRoom().getId() == ID_ROOM_CORRIDOIO_X) || eventTorciaAccesa || missioneCorrente) {
                            output = comandoLookAt(p, output);
                        } else {
                            output.append("Non riesco a vedere nulla.\n"
                                    + "La luce dell'oblo' non arriva fin qui... Fai qualcosa!");
                        }
                        break;
                    case USE:
                        output = comandoUse(p, output);
                        break;
                    case DRINK:
                        output = comandoDrink(p, output);
                        break;
                    case PICK_UP:
                        output = comandoPickUp(p, output);
                        break;
                    case PUT_DOWN:
                        output = comandoPutDown(p, output);
                        break;
                    case OPEN:
                        output = comandoOpen(p, output);
                        break;
                    case PUSH:
                        output = comandoPush(p, output);
                        break;
                    case TURN_ON:
                        output = comandoTurnOn(p, output);
                        break;
                    case PULL:
                        output = comandoPull(p, output);
                        break;
                    case TURN_OFF:
                        output = comandoTurnOff(p, output);
                        break;
                    case LOAD:
                        checkTimer();
                    default:
                        break;
                }
            } else {
                output.append("Non ho capito cosa devo fare! Prova con un altro comando.");
            }
        }

        output.append("\n");
        return output.toString();
    }

    private StringBuilder comandoNord(ParserOutput p, StringBuilder output) {
        boolean noroom = false;
        boolean move = false;
        boolean roomLocked = false;
        boolean moveLocked = false;

        if (getCurrentRoom().isLock()) {
            roomLocked = true;
        }
        if (getCurrentRoom().getNorth() != null && !roomLocked) {
            setCurrentRoom(getCurrentRoom().getNorth());
            move = true;
        } else if (getCurrentRoom().getNorth() != null && roomLocked) {
            moveLocked = true;
        } else {
            noroom = true;
        }
        output = checkMovement(output, noroom, move, roomLocked);
        return output;
    }

    private StringBuilder comandoSud(ParserOutput p, StringBuilder output) {
        boolean noroom = false;
        boolean move = false;
        boolean roomLocked = false;
        boolean moveLocked = false;

        if (getCurrentRoom().isLock()) {
            roomLocked = true;
        }
        if (getCurrentRoom().getSouth() != null && !roomLocked) {
            setCurrentRoom(getCurrentRoom().getSouth());
            move = true;
        } else if (getCurrentRoom().getSouth() != null && roomLocked) {
            moveLocked = true;
        } else {
            noroom = true;
        }
        output = checkMovement(output, noroom, move, roomLocked);
        return output;
    }

    private StringBuilder comandoOvest(ParserOutput p, StringBuilder output) {
        boolean noroom = false;
        boolean move = false;
        boolean roomLocked = false;
        boolean moveLocked = false;

        if (getCurrentRoom().isLock()) {
            roomLocked = true;
        }
        if (getCurrentRoom().getWest() != null && !roomLocked) {
            setCurrentRoom(getCurrentRoom().getWest());
            move = true;
        } else if (getCurrentRoom().getWest() != null && roomLocked) {
            moveLocked = true;
        } else {
            noroom = true;
        }
        output = checkMovement(output, noroom, move, roomLocked);
        return output;
    }

    private StringBuilder comandoEst(ParserOutput p, StringBuilder output) {
        boolean noroom = false;
        boolean move = false;
        boolean roomLocked = false;
        boolean moveLocked = false;

        if (getCurrentRoom().isLock()) {
            roomLocked = true;
        }
        if (getCurrentRoom().getEast() != null && !roomLocked) {
            setCurrentRoom(getCurrentRoom().getEast());
            move = true;
        } else if (getCurrentRoom().getEast() != null && roomLocked) {
            moveLocked = true;
        } else {
            noroom = true;
        }
        output = checkMovement(output, noroom, move, roomLocked);
        return output;
    }

    private StringBuilder checkMovement(StringBuilder output, boolean noroom, boolean move, boolean roomLocked) {
        if (noroom) {
            Random random = new Random();
            int randomChoice = random.nextInt(3);
            switch (randomChoice) {
                case 0:
                    output.append("Da quella parte non si puo' andare, c'e' un muro! Non hai ancora acquisito i poteri per oltrepassare i muri...\n");
                    break;
                case 1:
                    output.append("Puoi provare a prendere a testate la parete quanto vuoi, ma quel muro non crollera'...\n");
                    break;
                case 2:
                    output.append("Un vicolo cieco, da qui non si puo' proprio andare da nessuna parte. Ti conviene cambiare direzione...\n");
            }
        } else if (move) {

            output.append("           ////  ").append(getCurrentRoom().getName()).append("   ////");
            output.append("\n=====================================================================\n");
            if ((getCurrentRoom().getId() == ID_ROOM_SALA_COMANDI || getCurrentRoom().getId() == ID_ROOM_CORRIDOIO_X) || eventTorciaAccesa || missioneCorrente) {
                output.append("\n").append(getCurrentRoom().getDescription()).append("\n");
            } else {
                output.append("Non riesco a vedere nulla.\n"
                        + "La luce dell'oblo' non arriva fin qui... Fai qualcosa!");
            }
            //oppure aggiungere una seconda descrizione se la stanza è stata già visitata
            // o se ha compiuto una missione!!!
            if (getCurrentRoom().getId() == ID_ROOM_CORRIDOIO_X && eventTorcia == false) {
                /*try {
                    attivaThread();
                } catch (InterruptedException ex) {
                    output.append("Errore nel thread ").append(ex.getMessage());
                }*/
                output.append("\n\n_________!!!ATTENZIONE!!!_________ \n"
                        + "Sei inciampato su un oggetto! \n");
            }
            if (getCurrentRoom().getId() == ID_ROOM_CORRIDOIO_NORD && eventAvvisoCrepa == false) {
                eventAvvisoCrepa = true;
                timerGasTossico.start();
                timerActived = true;
                getCurrentRoom().setDescription("La navicella si e' danneggiata in seguito allo scontro.\nE' presente un crepa da cui entra"
                        + " un gas tossico che puo' nuocere alla tua salute.\nSei nel 'Corridoio Nord'!\nE' possibile dirigerti a sud o a est in cui e' presente una porta.");
            }
        } else if (roomLocked) {
            output.append("La stanza è bloccata prima di cambiare stanza dovresti fare qualcosa\n");
        }
        return output;
    }

    private StringBuilder comandoTurnOn(ParserOutput p, StringBuilder output) {
        if (p.getInvObject() == null) {
            output.append("Devi specificare l'oggetto da accendere o devi averlo nell'inventario");
        } else {
            if (p.getInvObject().isSiAccende()) {
                if (p.getInvObject().isAcceso()) {
                    output.append(p.getInvObject().getName()).append(" e' gia' acceso. Quante volte intendi accenderlo??");
                } else {
                    p.getInvObject().setAcceso(true);
                    output.append(p.getInvObject().getName()).append(" e' stato acceso");
                    if (p.getInvObject().getId() == ID_OBJECT_TORCIA) {
                        output.append("\nAdesso riesci a vedere meglio l’ambiente che ti circonda");
                        eventTorciaAccesa = true;
                    }
                }
            } else {
                output.append("Non è possibile accendere l'oggetto!");
            }
        }
        return output;
    }

    private StringBuilder comandoTurnOff(ParserOutput p, StringBuilder output) {
        if (p.getInvObject() == null) {
            output.append("Devi specificare l'oggetto da spegnere o devi averlo nell'inventario");
        } else {
            if (p.getInvObject().isSiAccende()) {
                if (p.getInvObject().isAcceso()) {
                    p.getInvObject().setAcceso(false);
                    output.append("L'oggetto ").append(p.getInvObject().getName()).append(" e' stato spento");
                    if (p.getInvObject().getId() == ID_OBJECT_TORCIA) {
                        eventTorciaAccesa = false;
                    }
                } else {
                    output.append("L'oggetto ").append(p.getInvObject().getName()).append(" e' gia' spento. Stai perdendo la memoria??");
                }
            } else {
                output.append("Non è possibile spegnere l'oggetto!");
            }
        }
        return output;
    }

    private StringBuilder comandoInventory(ParserOutput p, StringBuilder output) {
        output.append("Nel tuo inventario ci sono:\n");
        for (AdvObject o : getInventory()) {
            output.append(o.getName()).append(": ").append(o.getDescription()).append("\n");
        }
        return output;
    }

    private StringBuilder comandoLookAt(ParserOutput p, StringBuilder output) {
        if (getCurrentRoom().getLook() != null && p.getObject() == null && p.getInvObject() == null && p.getObject2() == null) {
            output.append(getCurrentRoom().getDescription()).append("\n");
            output.append(getCurrentRoom().getLook());
            if (!getCurrentRoom().getObjects().isEmpty()) {
                output.append("\nLa stanza contiene: ");
                for (AdvObject object : getCurrentRoom().getObjects()) {
                    if (object.isVisibile()) {
                        output.append("\n").append(object.getName());
                    }
                }
            }
        } else if (getCurrentRoom().getLook() == null && p.getObject() == null && p.getInvObject() == null && p.getObject2() == null) {
            //aggiungere il fatto che vengano elencati gli elementi che contiene e dove è possibile andare
            output.append("Non c'è niente di interessante qui.");
        } else if (p.getObject() != null && p.getObject().getDescription() != null && p.getObject().isVisibile() == true && p.getInvObject() == null && p.getObject2() == null) {
            output.append(p.getObject().getDescription());
            if (p.getObject().getId() == ID_OBJECT_PARETE) {
                getCurrentRoom().cercaObject(ID_OBJECT_CHIAVE).setVisibile(true);
                getCurrentRoom().cercaObject(ID_OBJECT_CHIAVE).setPickupable(true);
            }
            if (p.getObject().getId() == ID_OBJECT_CONTATORE) {
                getCurrentRoom().cercaObject(ID_OBJECT_LUCCHETTO).setVisibile(true);
            }
            if (p.getObject() instanceof AdvObjectContainer && (!p.getObject().isOpenable() || (p.getObject().isOpenable() && p.getObject().isOpen()))) {
                AdvObjectContainer c = (AdvObjectContainer) p.getObject();
                if (!c.getList().isEmpty()) {
                    output.append("\n").append(c.getName()).append(" possiede:");
                    Iterator<AdvObject> it = c.getList().iterator();
                    while (it.hasNext()) {
                        AdvObject next = it.next();
                        output.append("  '").append(next.getName()).append("'");
                    }
                } else {
                    output.append("ha nulla");
                }
            }
        } else if (p.getInvObject() != null && p.getInvObject().getDescription() != null) {
            output.append(p.getInvObject().getDescription());
        } else if (p.getObject() != null && p.getObject().getDescription() != null && p.getObject2() != null) {
            if ((p.getObject2().isOpenable() && p.getObject2().isOpen()) || !p.getObject2().isOpenable()) {
                output.append(p.getObject().getDescription());
            } else {
                output.append("L'oggetto ").append(p.getObject2().getName()).append(" dovresti prima aprirlo.");
            }
        } else {
            output.append("Non è possibile osservare nulla!");
        }
        return output;
    }

    private StringBuilder comandoUse(ParserOutput p, StringBuilder output) {
        if (p.getInvObject() == null) {
            output.append("Devi specificare l'oggetto da usare o devi averlo nell'inventario\n");
        } else {
            if (p.getInvObject().isUsable()) {
                if (p.getObject() == null) {
                    output.append("Con cosa vorresti usare ").append(p.getInvObject().getName()).append(" ! Non posso leggerti nel pensiero!");
                } else {
                    if (getCurrentRoom().vediCombinazioni(p.getInvObject(), p.getObject())) {
                        if (p.getObject().getId() == ID_OBJECT_PORTA) {
                            getCurrentRoom().setLock(false);
                            output.append("Hai aperto tutte le porte della navicella");
                        }
                        if (p.getObject().getId() == ID_OBJECT_CREPA) {
                            missioneCrepa = true;
                            output.append("Hai riparato la crepa. Complimenti!!");
                            getCurrentRoom().setDescription("Sei nel 'Corridoio Nord'! E' possibile dirigerti a sud e a est.");
                            getCurrentRoom().getObjects().remove(getCurrentRoom().cercaObject(ID_OBJECT_CREPA));
                        }
                        getCurrentRoom().togliCombinazione(p.getInvObject(), p.getObject());
                    } else {
                        output.append("Non è possibile usare questi oggetti insieme!");
                    }
                }
            } else {
                output.append("Non è possibile usare l'oggetto!");
            }
        }
        return output;
    }
     
    private StringBuilder comandoDrink(ParserOutput p, StringBuilder output) {
        if (p.getInvObject() == null && p.getObject() == null && p.getObject2() == null) {
            output.append("Devi specificare l'oggetto");
        } else if (p.getInvObject() == null && p.getObject() != null && p.getObject2() == null){
            output.append("Devi avere l'oggetto nell'inventario");
        } else if (p.getInvObject() != null && p.getObject() == null && p.getObject2() == null) {
            if (p.getInvObject().isDrinkable()) {
                if (p.getInvObject().isBevuta()) {
                    output.append(p.getInvObject().getName()).append(" e' stata gia' bevuta!");
                } else {
                    output.append("Hai bevuto ").append(p.getInvObject().getName());
                    p.getInvObject().setBevuta(true);
                    if (p.getInvObject().getId() == ID_OBJECT_FIALA3){
                        if (timerGasTossico.isAlive()) {
                            timerGasTossico.interrupt();
                            output.append("\nBravo, sei riuscito a salvarti la vita!");
                            output.append("\nSono vivo? " + timerGasTossico.isAlive());
                        }
                    }
                }
            } else {
                output.append(p.getInvObject().getName()).append(" non si puo' bere");
            }
        }
        return output;
    }

    private StringBuilder comandoPickUp(ParserOutput p, StringBuilder output) {
        if (p.getObject() != null && p.getObject2() == null) {
            if (p.getObject().isPickupable()) {
                getInventory().add(p.getObject());
                if (p.getObject().getId() == ID_OBJECT_PARETE) {
                    output.append("Dove hai visto che puoi prendere in mano i cavi? bravo sei morto");
                    this.end(output);
                }
                if (p.getObject().getId() == ID_OBJECT_TORCIA) {
                    eventTorcia = true;
                }
                getCurrentRoom().getObjects().remove(p.getObject());
                output.append("Hai raccolto: ").append(p.getObject().getDescription());
                output.append("\nE' stato inserito nell'inventario");
            } else {
                output.append("Non puoi raccogliere questo oggetto.");
            }
        } else if (p.getObject() != null && p.getObject2() != null) {
            if (p.getObject2().isOpenable() && !(p.getObject2().isOpen())) {
                output.append(p.getObject2().getName()).append(" e' chiuso. Aprilo prima!");
            } else { //caso in cui non sia apribile(es tavolo con pozioni) o e' gia aperto 
                if (!p.getObject2().getList().isEmpty()) {
                    Iterator<AdvObject> it = p.getObject2().getList().iterator();
                    while (it.hasNext()) {
                        AdvObject next = it.next();
                        //System.out.println("primo oggetto chiesto "+ p.getObject().getId() + "iteratore "+ next.getId());
                        if (p.getObject().getId() == next.getId()) {
                            getInventory().add(next);
                            it.remove();
                            output.append("Hai preso: ").append(p.getObject().getName());
                            output.append("\nVerra' inserito nell'inventario");
                        }
                    }
                    output.append("\n");
                } else {
                    output.append(p.getObject2().getName()).append(" non contiene nulla.");
                }
            }
        } else {
            output.append("Non c'è niente da raccogliere qui.");
        }
        return output;
    }

    private StringBuilder comandoPutDown(ParserOutput p, StringBuilder output) {
        if (p.getObject() == null && p.getObject2() == null && p.getInvObject() == null) {
            output.append("Devi specificare l'oggetto da lasciare nella stanza!");
        } else if (p.getObject() != null && p.getObject2() == null && p.getInvObject() == null) {
            output.append("E' gia' presente nella stanza!");
        } else if (p.getObject() == null && p.getObject2() == null && p.getInvObject() != null) {
            getInventory().remove(p.getInvObject());
            getCurrentRoom().getObjects().add(p.getInvObject());
            output.append("L'oggetto ").append(p.getInvObject().getName()).append(" e' stato lasciato nella stanza.");
        } else if (p.getObject() != null && p.getObject2() != null && p.getInvObject() == null) {
            output.append("Dovresti prima prendere l'oggetto ").append(p.getObject().getName()).append(" da ").append(p.getObject2().getName());
        } else if (p.getObject() != null && p.getObject2() == null && p.getInvObject() != null) {
            if (p.getObject() instanceof AdvObjectContainer) {
                if (p.getObject().isOpenable() && !(p.getObject().isOpen())) {
                    output.append(p.getObject().getName()).append(" e' chiuso. Aprilo prima!");
                } else { //caso in cui non sia apribile(es tavolo con pozioni) o e' gia aperto
                    getInventory().remove(p.getInvObject());
                    output.append("Hai rimosso dall'inventario l'oggetto ").append(p.getInvObject().getName());
                    AdvObjectContainer c = (AdvObjectContainer) p.getObject();
                    c.getList().add(p.getInvObject());
                }
            } else {
                output.append("Non puoi lasciare niente.");
            }
        } else {
            output.append("Non c'e' niente da lasciare qui.");
        }
        return output;
    }

    //cercare di rendere questo metodo meno lungo
    //semplificando qualcosa!
    private StringBuilder comandoOpen(ParserOutput p, StringBuilder output) {
        /*ATTENZIONE: quando un oggetto contenitore viene aperto, tutti gli oggetti contenuti
                    * vengongo inseriti nella stanza o nell'inventario a seconda di dove si trova l'oggetto contenitore.
                    * Questa soluzione NON va bene poiché quando un oggetto contenitore viene richiuso è complicato
                    * non rendere più disponibili gli oggetti contenuti rimuovendoli dalla stanza o dall'invetario.
                    * Trovare altra soluzione.
         */
        if (p.getObject() == null && p.getInvObject() == null && p.getObject2() == null) {
            output.append("Non c'è niente da aprire qui.\n");
        } else {
            if (p.getObject() == null && p.getInvObject() != null && p.getObject2() == null) {
                //quando apro un oggetto dell'inventario
                output.append("Non puoi aprire gli oggetti dell'inventario.");
            } else if (p.getObject() != null && p.getInvObject() == null && p.getObject2() == null) {
                //if (getCurrentRoom().vediCombinazioni_(p.getObject()) && p.getObject().isOpenable()) { (stava prima)
                if (p.getObject().isOpenable() && getCurrentRoom().vediCombinazioni_(p.getObject())) {
                    output.append("L'oggetto ").append(p.getObject().getName()).append(" non si puo' aprire in questo modo!");
                } else if (p.getObject().isOpenable() && p.getObject().isOpen() == false) {
                    if (p.getObject() instanceof AdvObjectContainer) { //oggetto contenitore
                        output.append("Hai aperto: ").append(p.getObject().getName()).append("\n");
                        p.getObject().setOpen(true);
                        AdvObjectContainer c = (AdvObjectContainer) p.getObject();
                        if (!c.getList().isEmpty()) {
                            output.append(c.getName()).append(" contiene:");
                            Iterator<AdvObject> it = c.getList().iterator(); //prendo gli oggetti che contiene
                            //qui poi li inserisce nella lista degli oggetti della stanza!SBAGLIATO!
                            while (it.hasNext()) {
                                AdvObject next = it.next();
                                //getCurrentRoom().getObjects().add(next);
                                output.append("  ").append(next.getName());
                                //it.remove();
                            }
                        }
                    } else if (p.getObject().isOpenable() && p.getObject().isOpen()) {
                        output.append("E' gia' aperto ").append(p.getObject().getName());
                    } else {
                            output.append("Hai aperto: ").append(p.getObject().getName());
                            p.getObject().setOpen(true);
                    }
                } else {
                    output.append("Non puoi aprire questo oggetto.");
                }
            } else if (p.getObject() != null && p.getInvObject() != null && p.getObject2() == null) {
                if (getCurrentRoom().vediCombinazioni(p.getInvObject(), p.getObject())) {
                    if (p.getObject().isOpenable() && p.getObject().isOpen()) {
                        output.append("L'oggetto ").append(p.getObject().getName()).append(" e' gia' aperto");
                    } else if (p.getObject().isOpenable() && p.getObject().isOpen() == false) {
                        output.append("L'oggetto ").append(p.getObject().getName()).append(" e' stato aperto");
                        getCurrentRoom().togliCombinazione(p.getInvObject(), p.getObject());
                        if (p.getObject().getId() == ID_OBJECT_LUCCHETTO) {
                            getCurrentRoom().cercaObject(ID_OBJECT_LEVA).setVisibile(true);
                            p.getObject().setDescription("Un semplice lucchetto");
                        }
                    } else if (p.getObject().isOpenable() && p.getObject().isOpen()){
                        output.append("E' gia' aperto!");
                    } else {
                        output.append("Non puoi fare cosi'!");
                    }
                    
                } else {
                    output.append("Non puoi fare cosi'!");
                }
            } else {
                if (p.getInvObject() != null) {
                    if (p.getInvObject().isOpenable() && p.getInvObject().isOpen() == false) {
                        if (p.getInvObject() instanceof AdvObjectContainer) {
                            AdvObjectContainer c = (AdvObjectContainer) p.getInvObject();
                            if (!c.getList().isEmpty()) {
                                output.append(c.getName()).append(" contiene:");
                                Iterator<AdvObject> it = c.getList().iterator();
                                while (it.hasNext()) {
                                    AdvObject next = it.next();
                                    getInventory().add(next);
                                    output.append(" ").append(next.getName()).append("\n");
                                    it.remove();
                                }
                                output.append("\n");
                            }
                        } else {
                            p.getInvObject().setOpen(true);
                        }
                        output.append("Hai aperto nel tuo inventario: ").append(p.getInvObject().getName());
                    } else {
                        output.append("Non puoi aprire questo oggetto.\n");
                    }
                } else { //caso in cui vuole aprire qualcosa nel contenitore
                    output.append("Non puoi aprire oggetti da li'... dovresti prima prenderli");
                }
            }
        }
        return output;
    }

    private StringBuilder comandoPush(ParserOutput p, StringBuilder output) {
        //ricerca oggetti pushabili
        if (p.getObject() != null && p.getObject().isPushable()) {
            output.append("Hai premuto: ").append(p.getObject().getName()).append("\n");
            /*if (p.getObject().getId() == 3) {
                        end(out);
                        }*/
        } else if (p.getInvObject() != null && p.getInvObject().isPushable()) {
            output.append("Hai premuto: ").append(p.getInvObject().getName()).append("\n");
            /*if (p.getInvObject().getId() == 3) {
                                end(out);
                            }*/
        } else {
            output.append("Non ci sono oggetti che puoi premere qui.\n");
        }
        return output;
    }

    private StringBuilder comandoPull(ParserOutput p, StringBuilder output) {
        //ricerca oggetti pullabili
        if (p.getObject() != null && p.getObject().isPullable() && p.getObject().isVisibile()) {
            if (p.getObject().isPull() == true) {
                output.append("L'oggetto ").append(p.getObject().getName()).append(" era stato gia' alzato.\nHai perso la memoria per caso?");
            } else {
                p.getObject().setPull(true);
                output.append("L'oggetto ").append(p.getObject().getName()).append(" e' stato alzato");
                if (ID_OBJECT_LEVA == p.getObject().getId()) {
                    output.append("\nAdesso sei riuscito ad accedere le luci della navicella.");
                    missioneCorrente = true;
                    for (Room room : getRooms()) {
                        if (room.getId() == ID_ROOM_SALA_COMANDI) {
                            room.setLook("Riesci a vedere tutti i comandi che però"
                                    + " non sono in funzione, dall'oblo riesci a vedere il cielo stellato, ma mi raccomando, non perdere tempo!!");
                        }
                        if (room.getId() == ID_ROOM_CORRIDOIO_X) {
                            room.setLook("E' un semplice corridoio, cosa ti aspettavi?! Continua con le tue missioni!!");
                        }
                    }
                }
            }
        } else if (p.getInvObject() != null && p.getInvObject().isPushable()) {
            if (p.getInvObject().isPull() == true) {
                output.append(p.getInvObject().getName()).append(" era stato gia' alzato.\nHai perso la memoria per caso?");
            } else {
                p.getInvObject().setPull(true);
                output.append(p.getInvObject().getName()).append(" e' stato alzato");
                if (ID_OBJECT_LEVA == p.getInvObject().getId()) {
                    output.append("\nAdesso sei riuscito ad accedere le luci della navicella.");
                    //cambiare tutte le descrizioni!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                }
            }
        } else {
            output.append("Non ci sono oggetti che puoi alzare qui.\n");
        }
        return output;
    }

    public void checkTimer(){
        if(timerActived){
            /*System.out.println(TimerGasTossico.activeCount());//1
            System.out.println(TimerGasTossico.currentThread().getThreadGroup());
            System.out.println(TimerGasTossico.currentThread().getId());//1
            System.out.print("\n");*/
            if(TimerGasTossico.activeCount() == 1){//
                //System.out.println(TimerGasTossico.currentThread());
                timerGasTossico.start();
            }
            /*System.out.println(TimerGasTossico.activeCount());//1
            System.out.println(TimerGasTossico.currentThread().getThreadGroup());
            System.out.println(TimerGasTossico.currentThread().getId());//1*/
        }
}
    
    private void end(StringBuilder output) {
        System.out.println(output);
        System.exit(0);
    }

}
