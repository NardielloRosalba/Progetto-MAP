/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.games;

import di.uniba.map.b.adventure.Database;
import di.uniba.map.b.adventure.GameDescription;
import di.uniba.map.b.adventure.parser.ParserOutput;
import di.uniba.map.b.adventure.type.AdvObject;
import di.uniba.map.b.adventure.type.AdvObjectContainer;
import di.uniba.map.b.adventure.type.Command;
import di.uniba.map.b.adventure.type.CommandType;
import di.uniba.map.b.adventure.type.Room;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author le bimbe di Luca
 */
public class PianetaGame extends GameDescription {

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
    private final int ID_OBJECT_RICETRASMITTENTE = 24;
    private final int ID_OBJECT_DEPOSITO = 25;
    private final int ID_OBJECT_TUTA = 26;
    private final int ID_OBJECT_ANFIBI = 27;
    private final int ID_OBJECT_ESTINTORE = 28;
    private final int ID_OBJECT_PANNELLO = 29;
    private final int ID_OBJECT_OBLO = 30;

    private boolean eventTorcia = false;
    private boolean eventTorciaAccesa = false;
    private boolean eventAvvisoCrepa = false;

    private boolean missionCorrente = false;
    private boolean missionCrepa = false;
    private boolean missioncontactReset = false;
    
    private int score = 0;
    
    private TimerAvvisoMorte toxicGasTimer = new TimerAvvisoMorte("del gas tossico!");

    private TimerAvvisoMorte oxygenTimer = new TimerAvvisoMorte("dell'abbassamento livelli di ossigeno!");

    private TimerAvvisoMorte impactTimer = new TimerAvvisoMorte(3, "dello scontro sulla Terra");
     
    static class TimerAvvisoMorte extends Thread implements Serializable{

        private int countDown = 10;
        private int taskCount = 0;
        private final String cause;
        private boolean timerActived = false;
        private boolean suspendTemporary = false;
        
        public TimerAvvisoMorte(int num, String causaMorte) {
            this.countDown = num;
            this.cause = causaMorte;
        }

        public boolean isSuspendTemporary() {
            return suspendTemporary;
        }

        public void setSuspendTemporary(boolean suspendTemporary) {
            this.suspendTemporary = suspendTemporary;
        }
        
        public TimerAvvisoMorte(String causaMorte) {
            this.cause = causaMorte;
        }
        
        @Override
        public void run() {
            this.timerActived = true;
            while (this.taskCount != countDown) {
                try {
                    System.out.println(notice(this.countDown - this.taskCount));
                    Thread.sleep(10000);
                    this.taskCount++;
                } catch (InterruptedException ex) {
                    if(!isSuspendTemporary()){
                        System.out.println("Ti sei salvato la vita!");
                        this.timerActived = false;
                    }
                    return;
                }
            }
            new PianetaGame().end(new StringBuilder("Sei morto!"));
        }

        public String notice(int n) {
            if (n != 1) {
                return ("- Mancano " + (this.countDown - this.taskCount) + " minuti prima di morire a cause " + this.cause);
            } else {
                return ("- Manca " + (this.countDown - this.taskCount) + " minuto prima di morire a cause " + this.cause);
            }
        }

        public boolean isTimerActived() {
            return timerActived;
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
        pickup.setAlias(new String[]{"prendi", "indossa"});
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
        Scanner fileRooms = new Scanner(new FileReader(".\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\Stanze.txt"));
        String title, description = "";
        String[] prova;
        title = fileRooms.nextLine();
        prova = fileRooms.nextLine().split("\\.");
        for (String s : prova) {
            description += (s + ".\n");
        }
        Room controlRoom = new Room(ID_ROOM_SALA_COMANDI, title, description);

        description = "";
        title = fileRooms.nextLine();
        prova = fileRooms.nextLine().split("\\.");
        for (String s : prova) {
            description += (s + ".\n");
        }
        Room xCorridor = new Room(ID_ROOM_CORRIDOIO_X, title, description);

        description = "";
        title = fileRooms.nextLine();
        prova = fileRooms.nextLine().split("\\.");
        for (String s : prova) {
            description += (s + ".\n");
        }
        Room southCorridor = new Room(ID_ROOM_CORRIDOIO_SUD, title, description);

        description = "";
        title = fileRooms.nextLine();
        prova = fileRooms.nextLine().split("\\.");
        for (String s : prova) {
            description += (s + ".\n");
        }
        Room electricalRoom = new Room(ID_ROOM_SALA_ELETTRICA, title, description);

        description = "";
        title = fileRooms.nextLine();
        prova = fileRooms.nextLine().split("\\.");
        for (String s : prova) {
            description += (s + ".\n");
        }
        Room northCorridor = new Room(ID_ROOM_CORRIDOIO_NORD, title, description);

        description = "";
        title = fileRooms.nextLine();
        prova = fileRooms.nextLine().split("\\.");
        for (String s : prova) {
            description += (s + ".\n");
        }
        Room infirmaryRoom = new Room(ID_ROOM_INFERMERIA, title, description);

        description = "";
        title = fileRooms.nextLine();
        prova = fileRooms.nextLine().split("\\.");
        for (String s : prova) {
            description += (s + ".\n");
        }
        Room telecommunicationsRoom = new Room(ID_ROOM_SALA_TELECOMUNICAZIONI, title, description);

        description = "";
        title = fileRooms.nextLine();
        prova = fileRooms.nextLine().split("\\.");
        for (String s : prova) {
            description += (s + ".\n");
        }
        Room externalRoom = new Room(ID_ROOM_SALA_ESTERNA, title, description);

        //maps
        controlRoom.setSouth(xCorridor);
        controlRoom.setLock(true);
        controlRoom.setLook(controlRoom.getDescription());

        xCorridor.setWest(controlRoom);
        xCorridor.setNorth(northCorridor);
        xCorridor.setSouth(southCorridor);
        xCorridor.setEast(telecommunicationsRoom);

        northCorridor.setEast(infirmaryRoom);
        northCorridor.setSouth(xCorridor);

        infirmaryRoom.setSouth(telecommunicationsRoom);
        infirmaryRoom.setWest(northCorridor);

        electricalRoom.setEast(southCorridor);

        southCorridor.setWest(electricalRoom);
        southCorridor.setNorth(xCorridor);

        telecommunicationsRoom.setNorth(infirmaryRoom);
        telecommunicationsRoom.setSouth(externalRoom);
        telecommunicationsRoom.setWest(xCorridor);

        externalRoom.setNorth(telecommunicationsRoom);

        getRooms().add(controlRoom);
        getRooms().add(xCorridor);
        getRooms().add(southCorridor);
        getRooms().add(electricalRoom);
        getRooms().add(infirmaryRoom);
        getRooms().add(northCorridor);
        getRooms().add(telecommunicationsRoom);
        getRooms().add(externalRoom);

        //obejcts
        //AdvObjectContainer wardrobeObj = new AdvObjectContainer(1, );
        Scanner fileObject = new Scanner(new FileReader(".\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\Oggetti.txt"));

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObjectContainer wardrobeObj = new AdvObjectContainer(ID_OBJECT_ARMADIO, title, description);
        wardrobeObj.setAlias(new String[]{"armadio"});
        wardrobeObj.setPickupable(false);
        wardrobeObj.setOpenable(true);
        controlRoom.getObjects().add(wardrobeObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject cardObj = new AdvObject(ID_OBJECT_TESSERA, title, description);
        cardObj.setAlias(new String[]{"tessera", "carta", "pass"});
        cardObj.setUsable(true);
        wardrobeObj.getList().add(cardObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject protocollObj = new AdvObject(ID_OBJECT_PROTOCOLLI, title, description);
        protocollObj.setAlias(new String[]{"protocolli", "protocollinavigazione", "foglinavigazione", "fogli"});
        wardrobeObj.getList().add(protocollObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject torchObj = new AdvObject(ID_OBJECT_TORCIA, title, description);
        torchObj.setAlias(new String[]{"oggetto", "torcia"});
        torchObj.setTurnOn(true);
        torchObj.setOn(false);
        xCorridor.getObjects().add(torchObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject doorObj = new AdvObject(ID_OBJECT_PORTA, title, description);
        doorObj.setOpenable(true);
        doorObj.setPickupable(false);
        doorObj.setAlias(new String[]{"porta", "portone", "portasala"});
        controlRoom.getObjects().add(doorObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject counterObj = new AdvObject(ID_OBJECT_CONTATORE, title, description);
        counterObj.setAlias(new String[]{"contatore", "contatoreelettrico"});
        counterObj.setPickupable(false);
        electricalRoom.getObjects().add(counterObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject padlockObj = new AdvObject(ID_OBJECT_LUCCHETTO, title, description);
        padlockObj.setAlias(new String[]{"lucchetto"});
        padlockObj.setPickupable(false);
        padlockObj.setOpenable(true);
        padlockObj.setVisible(false);
        electricalRoom.getObjects().add(padlockObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject wallObj = new AdvObject(ID_OBJECT_PARETE, title, description);
        wallObj.setAlias(new String[]{"paretefili", "parete", "fili"});
        wallObj.setPickupable(false);
        electricalRoom.getObjects().add(wallObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject keyObj = new AdvObject(ID_OBJECT_CHIAVE, title, description);
        keyObj.setAlias(new String[]{"chiave"});
        keyObj.setPickupable(false);
        keyObj.setVisible(false);
        electricalRoom.getObjects().add(keyObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject leverObj = new AdvObject(ID_OBJECT_LEVA, title, description);
        leverObj.setAlias(new String[]{"leva"});
        leverObj.setPickupable(false);
        leverObj.setPullable(true);
        leverObj.setVisible(false);
        electricalRoom.getObjects().add(leverObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObjectContainer cassetteObj = new AdvObjectContainer(ID_OBJECT_CASSETTA, title, description);
        cassetteObj.setAlias(new String[]{"cassetta", "cassettaprontosoccorso"});
        cassetteObj.setPickupable(false);
        cassetteObj.setOpenable(true);
        infirmaryRoom.getObjects().add(cassetteObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject glueObj = new AdvObject(ID_OBJECT_COLLA, title, description);
        glueObj.setAlias(new String[]{"collaindustiale", "supercolla", "colla"});
        glueObj.setUsable(true);
        cassetteObj.getList().add(glueObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject scissorsObj = new AdvObject(ID_OBJECT_FORBICI, title, description);
        scissorsObj.setAlias(new String[]{"forbici"});
        cassetteObj.getList().add(scissorsObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject scalpelObj = new AdvObject(ID_OBJECT_BISTURI, title, description);
        scalpelObj.setAlias(new String[]{"bisturi"});
        cassetteObj.getList().add(scalpelObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject gauzeObj = new AdvObject(ID_OBJECT_GARZE, title, description);
        gauzeObj.setAlias(new String[]{"garze"});
        cassetteObj.getList().add(gauzeObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject snareObj = new AdvObject(ID_OBJECT_LACCIO, title, description);
        snareObj.setAlias(new String[]{"laccio", "laccioemostatico"});
        cassetteObj.getList().add(snareObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject crackObj = new AdvObject(ID_OBJECT_CREPA, title, description);
        crackObj.setAlias(new String[]{"buco", "crepa"});
        crackObj.setPickupable(false);
        northCorridor.getObjects().add(crackObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject bedObj = new AdvObject(ID_OBJECT_LETTINO, title, description);
        bedObj.setAlias(new String[]{"lettino", "lettinovisita", "lettovisita", "letto"});
        bedObj.setPickupable(false);
        infirmaryRoom.getObjects().add(bedObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObjectContainer tableObj = new AdvObjectContainer(ID_OBJECT_TAVOLO, title, description);
        tableObj.setAlias(new String[]{"tavolo", "tavolino"});
        tableObj.setPickupable(false);
        infirmaryRoom.getObjects().add(tableObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject bookObj = new AdvObject(ID_OBJECT_LIBRO, title, description);
        bookObj.setAlias(new String[]{"libro", "librofiale"});
        tableObj.getList().add(bookObj); //aggiungere comando leggi libro/ osserva libro

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject phialObj1 = new AdvObject(ID_OBJECT_FIALA1, title, description);
        phialObj1.setDrinkable(true);
        phialObj1.setAlias(new String[]{"fiala1", "pozione1", "fialaacqua"});
        tableObj.getList().add(phialObj1);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject phialObj2 = new AdvObject(ID_OBJECT_FIALA2, title, description);
        phialObj2.setDrinkable(true);
        phialObj2.setAlias(new String[]{"fiala2", "pozione2", "fialaanimali"});
        tableObj.getList().add(phialObj2);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject phialObj3 = new AdvObject(ID_OBJECT_FIALA3, title, description);
        phialObj3.setAlias(new String[]{"fiala3", "pozione3", "fialagas", "fialagastossici", "fialagastossico"});
        phialObj3.setDrinkable(true);
        tableObj.getList().add(phialObj3);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject phialObj4 = new AdvObject(ID_OBJECT_FIALA4, title, description);
        phialObj4.setDrinkable(true);
        phialObj4.setAlias(new String[]{"fiala4", "pozione4", "fialapensiero"});
        tableObj.getList().add(phialObj4);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject transceiverObj = new AdvObject(ID_OBJECT_RICETRASMITTENTE, title, description);
        transceiverObj.setPickupable(false);
        transceiverObj.setVisible(false);
        transceiverObj.setAlias(new String[]{"ricetrasmittente", "radio", "radiotrasmittente", "ricetrasmettitore"});
        telecommunicationsRoom.getObjects().add(transceiverObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObjectContainer storageObj = new AdvObjectContainer(ID_OBJECT_DEPOSITO, title, description);
        storageObj.setPickupable(false);
        storageObj.setOpenable(true);
        storageObj.setAlias(new String[]{"deposito", "magazzino"});
        telecommunicationsRoom.getObjects().add(storageObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject suitObj = new AdvObject(ID_OBJECT_TUTA, title, description);
        suitObj.setAlias(new String[]{"tutadaastronauta", "tutaastronauta", "tuta"});
        storageObj.getList().add(suitObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject bootsObj = new AdvObject(ID_OBJECT_ANFIBI, title, description);
        bootsObj.setUsable(true);
        bootsObj.setAlias(new String[]{"anfibi", "stivali", "scarpe"});
        storageObj.getList().add(bootsObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject fireExtinguisherObj = new AdvObject(ID_OBJECT_ESTINTORE, title, description);
        fireExtinguisherObj.setUsable(true);
        fireExtinguisherObj.setAlias(new String[]{"estintore"});
        storageObj.getList().add(fireExtinguisherObj);

        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject panelObj = new AdvObject(ID_OBJECT_PANNELLO, title, description);
        panelObj.setAlias(new String[]{"pannello", "pannelloemergenza"});
        panelObj.setPickupable(false);
        externalRoom.getObjects().add(panelObj);
        
        title = fileObject.nextLine();
        description = fileObject.nextLine();
        AdvObject oblo = new AdvObject(ID_OBJECT_OBLO, title, description);
        oblo.setAlias(new String[]{"oblo"});
        oblo.setPickupable(false);
        controlRoom.getObjects().add(oblo);

        //AGGIUNGO COMBINAZIONI PER OGNI STANZA
        controlRoom.addCombinazioni(doorObj, cardObj);
        electricalRoom.addCombinazioni(keyObj, padlockObj);
        northCorridor.addCombinazioni(glueObj, crackObj);

        //DESCRIZIONI STANZE IN SEGUITO AL COMANDO OSSERVA
        Scanner os;
        os = new Scanner(new FileReader(".\\src\\main\\java\\di\\uniba\\map\\b\\adventure\\resources\\Osserva.txt"));
        String[] osserva;

        description = "";
        osserva = os.nextLine().split("\\.");
        for (String s : osserva) {
            description += (s + ".\n");
        }
        controlRoom.setLook(description);

        description = "";
        osserva = os.nextLine().split("\\.");
        for (String s : osserva) {
            description += (s + ".\n");
        }
        xCorridor.setLook(description);

        description = "";
        osserva = os.nextLine().split("\\.");
        for (String s : osserva) {
            description += (s + ".\n");
        }
        northCorridor.setLook(description);

        description = "";
        osserva = os.nextLine().split("\\.");
        for (String s : osserva) {
            description += (s + ".\n");
        }
        infirmaryRoom.setLook(description);

        description = "";
        osserva = os.nextLine().split("\\.");
        for (String s : osserva) {
            description += (s + ".\n");
        }
        electricalRoom.setLook(description);

        description = "";
        osserva = os.nextLine().split("\\.");
        for (String s : osserva) {
            description += (s + ".\n");
        }
        southCorridor.setLook(description);

        description = "";
        osserva = os.nextLine().split("\\.");
        for (String s : osserva) {
            description += (s + ".\n");
        }
        telecommunicationsRoom.setLook(description);

        description = "";
        osserva = os.nextLine().split("\\.");
        for (String s : osserva) {
            description += (s + ".\n");
        }
        externalRoom.setLook(description);

        fileRooms.close();
        fileObject.close();
        os.close();
        setCurrentRoom(controlRoom);
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
                        output = commandNord(p, output);
                        break;
                    case SOUTH:
                        output = commandSud(p, output);
                        break;
                    case EAST:
                        output = commandEst(p, output);
                        break;
                    case WEST:
                        output = commandOvest(p, output);
                        break;
                    case INVENTORY:
                        output = commandInventory(p, output);
                        break;
                    case LOOK_AT:
                        if ((getCurrentRoom().getId() == ID_ROOM_SALA_COMANDI || getCurrentRoom().getId() == ID_ROOM_CORRIDOIO_X) || eventTorciaAccesa || missionCorrente) {
                            output = commandLookAt(p, output);
                        } else {
                            output.append("Non riesco a vedere nulla.\n"
                                    + "La luce dell'oblo' non arriva fin qui... Fai qualcosa!");
                        }
                        break;
                    case USE:
                        output = commandUse(p, output);
                        break;
                    case DRINK:
                        output = commandDrink(p, output);
                        break;
                    case PICK_UP:
                        output = commandPickUp(p, output);
                        break;
                    case PUT_DOWN:
                        output = commandPutDown(p, output);
                        break;
                    case OPEN:
                        output = commandOpen(p, output);
                        break;
                    case TURN_ON:
                        output = commandTurnOn(p, output);
                        break;
                    case PULL:
                        output = commandPull(p, output);
                        break;
                    case TURN_OFF:
                        output = commandTurnOff(p, output);
                        break;       
                    case LOAD:
                        checkTimer();
                        break;
                    case SAVE:
                        try {
                            Database db = new Database();
                            try {
                                db.saving(this);
                                db.getInfo();
                            } catch (IOException ex) {
                                Logger.getLogger(PianetaGame.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ClassNotFoundException ex) {
                                Logger.getLogger(PianetaGame.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(PianetaGame.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    break;
                    case END:
                        end(output.append("Addio!"));
                        break;
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

    private StringBuilder commandNord(ParserOutput p, StringBuilder output) {
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

    private StringBuilder commandSud(ParserOutput p, StringBuilder output) {
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

    private StringBuilder commandOvest(ParserOutput p, StringBuilder output) {
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

    private StringBuilder commandEst(ParserOutput p, StringBuilder output) {
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
            if ((getCurrentRoom().getId() == ID_ROOM_SALA_COMANDI || getCurrentRoom().getId() == ID_ROOM_CORRIDOIO_X) || eventTorciaAccesa || missionCorrente) {
                output.append("\n").append(getCurrentRoom().getDescription()).append("\n");
            } else {
                output.append("Non riesco a vedere nulla.\n"
                        + "La luce dell'oblo' non arriva fin qui... Fai qualcosa!");
            }
            if (getCurrentRoom().getId() == ID_ROOM_CORRIDOIO_X && eventTorcia == false) {
                output.append("\n\n_________!!!ATTENZIONE!!!_________ \n"
                        + "Sei inciampato su un oggetto! \n");
            }
            if (getCurrentRoom().getId() == ID_ROOM_CORRIDOIO_NORD && eventAvvisoCrepa == false) {
                eventAvvisoCrepa = true;
                if (getInventory().cercaObject(ID_OBJECT_FIALA3) != null) {
                    AdvObject fiala = getInventory().cercaObject(ID_OBJECT_FIALA3);
                    if (fiala.isDrinked()) {
                        output.append("Il gas tossico non puo' nuocere alla tua salute perche' hai bevuto prima la fiala\n"
                                + "Sei immune!");
                        this.score = 1000;
                    } else {
                        toxicGasTimer.start();
                    }
                } else {
                    toxicGasTimer.start();
                }
                getCurrentRoom().setDescription("La navicella si e' danneggiata in seguito allo scontro.\nE' presente un crepa da cui entra"
                        + " un gas tossico che puo' nuocere alla tua salute.\nSei nel 'Corridoio Nord'!\nE' possibile dirigerti a sud o a est in cui e' presente una porta.");
            }
            if (getCurrentRoom().getId() == ID_ROOM_SALA_ESTERNA) {
                if (getInventory().cercaObject(ID_OBJECT_TUTA) == null) {
                    output.append("Hai sbagliato ad uscire senza tuta.\n Sei morto!");
                    this.end(output);
                }
                    impactTimer.start();
            }
        } else if (roomLocked) {
            output.append("La stanza è bloccata prima di cambiare stanza dovresti fare qualcosa\n");
        }
        return output;
    }

    private StringBuilder commandTurnOn(ParserOutput p, StringBuilder output) {
        if (p.getInvObject() == null) {
            output.append("Devi specificare l'oggetto da accendere o devi averlo nell'inventario");
        } else {
            if (p.getInvObject().isTurnOn()) {
                if (p.getInvObject().isOn()) {
                    output.append(p.getInvObject().getName()).append(" e' gia' acceso. Quante volte intendi accenderlo??");
                } else {
                    p.getInvObject().setOn(true);
                    output.append(p.getInvObject().getName()).append(" e' stato acceso");
                    if (p.getInvObject().getId() == ID_OBJECT_TORCIA) {
                        output.append("\nAdesso riesci a vedere meglio l’ambiente che ti circonda");
                        this.score = 20;
                        eventTorciaAccesa = true;
                    }
                }
            } else {
                output.append("Non è possibile accendere l'oggetto!");
            }
        }
        return output;
    }

    private StringBuilder commandTurnOff(ParserOutput p, StringBuilder output) {
        if (p.getInvObject() == null) {
            output.append("Devi specificare l'oggetto da spegnere o devi averlo nell'inventario");
        } else {
            if (p.getInvObject().isTurnOn()) {
                if (p.getInvObject().isOn()) {
                    p.getInvObject().setOn(false);
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

    private StringBuilder commandInventory(ParserOutput p, StringBuilder output) {
        output.append("Nel tuo inventario ci sono:\n");
        for (AdvObject o : getInventory().getList()) {
            output.append(o.getName()).append(": ").append(o.getDescription()).append("\n");
        }
        return output;
    }

    private StringBuilder commandLookAt(ParserOutput p, StringBuilder output) {
        if (getCurrentRoom().getLook() != null && p.getObject() == null && p.getInvObject() == null && p.getObject2() == null) {
            output.append(getCurrentRoom().getDescription()).append("\n");
            output.append(getCurrentRoom().getLook());
            if (!getCurrentRoom().getObjects().isEmpty()) {
                output.append("\nLa stanza contiene: ");
                for (AdvObject object : getCurrentRoom().getObjects()) {
                    if (object.isVisible()) {
                        output.append("\n").append(object.getName());
                    }
                }
            }
        } else if (getCurrentRoom().getLook() == null && p.getObject() == null && p.getInvObject() == null && p.getObject2() == null) {
            output.append("Non c'è niente di interessante qui.");
        } else if (p.getObject() != null && p.getObject().getDescription() != null && p.getObject().isVisible() == true && p.getInvObject() == null && p.getObject2() == null) {
            if (p.getObject().getId() == ID_OBJECT_PARETE) {
                getCurrentRoom().cercaObject(ID_OBJECT_CHIAVE).setVisible(true);
                getCurrentRoom().cercaObject(ID_OBJECT_CHIAVE).setPickupable(true);
            }
            if (p.getObject().getId() == ID_OBJECT_CONTATORE) {
                getCurrentRoom().cercaObject(ID_OBJECT_LUCCHETTO).setVisible(true);
            }
            if (p.getObject().getId() == ID_OBJECT_RICETRASMITTENTE) {
                if (!missioncontactReset) {
                    Scanner scanner = new Scanner(System.in);
                    System.out.println("Vuoi ripristinare i contatti col tuo pianeta?");
                    while (scanner.hasNextLine()) {
                        String risposta = scanner.nextLine().toLowerCase();
                        if (risposta.equals("si")) {
                            contactReset();
                            break;
                        } else if (risposta.equals("no")) {
                            break;
                        } else {
                            System.out.println("Inserire SOLO \"si\" o \"no\" \n");
                        }
                    }
                } else {
                    System.out.println("Hai ricevuto il seguente messaggio: \n");
                    planetMessage();
                }
            }
            if (p.getObject().getId() == ID_OBJECT_PANNELLO) {
                System.out.println(output);
                Scanner scanner = new Scanner(System.in);
                System.out.println("Vuoi attivare l'atterraggio d'emergenza?");
                while (scanner.hasNextLine()) {
                    String risposta = scanner.nextLine().toLowerCase();
                    if (risposta.equals("si")) {
                        emergencyLanding(output);
                        break;
                    } else if (risposta.equals("no")) {
                        break;
                    } else {
                        System.out.println("Inserire SOLO \"si\" o \"no\" \n");
                    }
                }
            } else {
                output.append(p.getObject().getDescription());
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

    private void contactReset() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("sequenza: ''11 22 .. 30 22 44''\n");
        System.out.println("Quale numero inserisco? (scrivi solo il numero)\n");
        while (scanner.hasNextLine()) {
            String risposta = scanner.nextLine().trim();
            if (Integer.parseInt(risposta) == 15) {
                System.out.println("Contatti ripristinati\n");
                this.score = 500;
                missioncontactReset = true;
                System.out.println("Hai ricevuto il seguente messaggio: \n");
                planetMessage();
                if (getInventory().cercaObject(ID_OBJECT_TUTA) == null) {
                        oxygenTimer.start();
                } else {
                    System.out.println("Grazie alla tuta che hai preso prima riesci a respirare ancora");
                }
                getCurrentRoom().cercaObject(ID_OBJECT_RICETRASMITTENTE).setDescription("serve per entrare in contatto col tuo pianeta");
                break;
            } else {
                System.out.println("Non valido.\n");
                break;
            }
        }
    }

    private void emergencyLanding(StringBuilder output) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Per attivare l'atterraggio d'emergenza dovrai rispondere alla seguente domanda:\n"
                + "\"Attraversa il vetro ma senza romperlo. Cosa è?\" \n");
        System.out.println("Quale parola inserisco? (scrivi solo la parola)\n");
        while (scanner.hasNextLine()) {
            String risposta = scanner.nextLine().trim();
            if (risposta.equals("luce")) {
                this.score = 550;
                System.out.println("Hai attivato l'atterraggio d'emergenza\n");
                impactTimer.interrupt();
                //scriviamo qualcosa per dire che e' finito il gioco
                System.out.println("Gioco Finito, alla prossima!");
                this.end(output);
                break;
            } else {
                System.out.println("Parola non valida.\n");
                break;
            }
        }
    }

    private void planetMessage() {
        System.out.println("\"Salve Capitan Hector!\n"
                + "Finalmente siamo riusciti a localizzarti, avevamo quasi perso le speranze! \n"
                + "Attenzione! La tua navicella sta per entrare nell’Atmosfera del nostro pianeta e si sta per incendiare.\n"
                + "I valori dell’ossigeno presente nella tua navicella si stanno abbassando rapidamente.\n"
                + "Ti sollecitiamo di ripristinare l’impianto dell’ossigeno e ritornare sulla tua rotta. Buona Fortuna!\n"
                + "Speriamo di rivederti al più presto… \"\n");
    }

    private StringBuilder commandUse(ParserOutput p, StringBuilder output) {
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
                            this.score = 100;
                            output.append("Hai aperto tutte le porte della navicella");
                        }
                        if (p.getObject().getId() == ID_OBJECT_CREPA) {
                            missionCrepa = true;
                            this.score = 100;
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

    private StringBuilder commandDrink(ParserOutput p, StringBuilder output) {
        if (p.getInvObject() == null && p.getObject() == null && p.getObject2() == null) {
            output.append("Devi specificare l'oggetto");
        } else if (p.getInvObject() == null && p.getObject() != null && p.getObject2() == null) {
            output.append("Devi avere l'oggetto nell'inventario");
        } else if (p.getInvObject() != null && p.getObject() == null && p.getObject2() == null) {
            if (p.getInvObject().isDrinkable()) {
                if (p.getInvObject().isDrinked()) {
                    output.append(p.getInvObject().getName()).append(" e' stata gia' bevuta!");
                } else {
                    output.append("Hai bevuto ").append(p.getInvObject().getName());
                    p.getInvObject().setDrinked(true);
                    if (p.getInvObject().getId() == ID_OBJECT_FIALA3) {
                        if (toxicGasTimer.isAlive()) {
                            toxicGasTimer.interrupt();
                            this.score = 100;
                            output.append("\nBravo!");
                        }
                    }
                }
            } else {
                output.append(p.getInvObject().getName()).append(" non si puo' bere");
            }
        }
        return output;
    }

    private StringBuilder commandPickUp(ParserOutput p, StringBuilder output) {
        if (p.getObject() != null && p.getObject2() == null && p.getInvObject() == null) {
            if (p.getObject().isPickupable()) {
                getInventory().add(p.getObject());
                if (p.getObject().getId() == ID_OBJECT_PARETE) {
                    output.append("Dove hai visto che puoi prendere in mano i cavi? bravo sei morto");
                    this.end(output);
                }
                if (p.getObject().getId() == ID_OBJECT_TORCIA) {
                    eventTorcia = true;
                    this.score = 10;
                }
                getCurrentRoom().getObjects().remove(p.getObject());
                output.append("Hai raccolto: ").append(p.getObject().getDescription());
                output.append("\nE' stato inserito nell'inventario");
            } else {
                output.append("Non puoi raccogliere questo oggetto.");
            }
        } else if (p.getObject() != null && p.getObject2() != null && p.getInvObject() == null) {
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
                            if (p.getObject().getId() == ID_OBJECT_TUTA && oxygenTimer.isAlive()) {
                                oxygenTimer.interrupt();
                                this.score = 150;
                            }
                        }
                    }
                    output.append("\n");
                } else {
                    output.append(p.getObject2().getName()).append(" non contiene nulla.");
                }
            }
        } else if (p.getObject() == null && p.getObject2() == null && p.getInvObject() != null) {
            output.append("L'hai gia' preso!");
        } else {
            output.append("Non c'è niente da raccogliere qui.");
        }
        return output;
    }

    private StringBuilder commandPutDown(ParserOutput p, StringBuilder output) {
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

    private StringBuilder commandOpen(ParserOutput p, StringBuilder output) {
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
                    output.append("Hai aperto: ").append(p.getObject().getName()).append("\n");
                    p.getObject().setOpen(true);
                    if (p.getObject() instanceof AdvObjectContainer) { //oggetto contenitore
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
                    }
                } else if (p.getObject().isOpenable() && p.getObject().isOpen()) {
                    output.append("E' gia' aperto ").append(p.getObject().getName());
                } else {
                    output.append("Non puoi aprire questo oggetto.");
                }
            } else if (p.getObject() != null && p.getInvObject() != null && p.getObject2() == null) {
                if (getCurrentRoom().vediCombinazioni(p.getInvObject(), p.getObject())) {
                    if (p.getObject().isOpenable() && p.getObject().isOpen()) {
                        output.append("L'oggetto ").append(p.getObject().getName()).append(" e' gia' aperto").append("\n");
                    } else if (p.getObject().isOpenable() && p.getObject().isOpen() == false) {
                        output.append("L'oggetto ").append(p.getObject().getName()).append(" e' stato aperto").append("\n");
                        p.getObject().setOpen(true);
                        if(p.getObject().getId() == ID_OBJECT_PORTA){
                            this.score = 100;
                            output.append("Hai aperto tutte le porte della navicella");
                            getCurrentRoom().setLock(false);
                        }
                        getCurrentRoom().togliCombinazione(p.getInvObject(), p.getObject());
                        if (p.getObject().getId() == ID_OBJECT_LUCCHETTO) {
                            getCurrentRoom().cercaObject(ID_OBJECT_LEVA).setVisible(true);
                            p.getObject().setDescription("Un semplice lucchetto");
                            getCurrentRoom().cercaObject(ID_OBJECT_CONTATORE).setDescription("Al centro di esso e' presente una leva che e' possibile alzare");
                        }
                    } else if (p.getObject().isOpenable() && p.getObject().isOpen()) {
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

    public int getScore() {
        return score;
    }
    
    private StringBuilder commandPull(ParserOutput p, StringBuilder output) {
        if (p.getObject() != null && p.getObject().isPullable() && p.getObject().isVisible()) {
            if (p.getObject().isPull() == true) {
                output.append("L'oggetto ").append(p.getObject().getName()).append(" era stato gia' alzato.\nHai perso la memoria per caso?");
            } else {
                p.getObject().setPull(true);
                output.append("L'oggetto ").append(p.getObject().getName()).append(" e' stato alzato");
                if (ID_OBJECT_LEVA == p.getObject().getId()) {
                    this.score = 100;
                    output.append("\nAdesso sei riuscito ad accedere le luci della navicella.");
                    missionCorrente = true;
                    p.getObject().setDescription("Semplice leva alzata");
                    for (Room room : getRooms()) {
                        if (room.getId() == ID_ROOM_SALA_COMANDI) {
                            room.setLook("Riesci a vedere tutti i comandi che però"
                                    + " non sono in funzione, dall'oblo riesci a vedere il cielo stellato, ma mi raccomando, non perdere tempo!!");
                        }
                        if (room.getId() == ID_ROOM_CORRIDOIO_X) {
                            room.setLook("E' un semplice corridoio, cosa ti aspettavi?! Continua con le tue missioni!!");
                        }
                        if (room.getId() == ID_ROOM_SALA_TELECOMUNICAZIONI) {
                            room.cercaObject(ID_OBJECT_RICETRASMITTENTE).setVisible(true);
                        }
                    }
                }
            }
        } else if (p.getInvObject() != null && p.getInvObject().isPullable()) {
            if (p.getInvObject().isPull() == true) {
                output.append(p.getInvObject().getName()).append(" era stato gia' alzato.\nHai perso la memoria per caso?");
            } else {
                p.getInvObject().setPull(true);
                output.append(p.getInvObject().getName()).append(" e' stato alzato");
            }
        } else {
            output.append("Non ci sono oggetti che puoi alzare qui.\n");
        }
        return output;
    }
    
    public void checkTimer(){
        if(toxicGasTimer.isTimerActived()){
            toxicGasTimer.setSuspendTemporary(false);
            toxicGasTimer.start();
            System.out.println("timer gas tossico ATTIVATO ");
        }
        if(oxygenTimer.isTimerActived()){
            oxygenTimer.setSuspendTemporary(false);
            oxygenTimer.start();
            System.out.println("timer ossigeno ATTIVATO ");
        }
        if(impactTimer.isTimerActived()){
            impactTimer.setSuspendTemporary(false);
            impactTimer.start();
            System.out.println("timer scontro sulla terra ATTIVATO ");
        }
    }
    
    public void stopThread(){
        if(toxicGasTimer.isTimerActived()){
            toxicGasTimer.setSuspendTemporary(true);
            toxicGasTimer.interrupt();
            System.out.println("Timer gas tossico sospeso");
        }
        if(oxygenTimer.isTimerActived()){
            oxygenTimer.setSuspendTemporary(true);
            oxygenTimer.interrupt();
            System.out.println("Timer ossigeno sospeso");
        }
        if(impactTimer.isTimerActived()){
            impactTimer.setSuspendTemporary(true);
            impactTimer.interrupt();
            System.out.println("Timer scontro sulla terra sospeso");
        }
    }
        
    private void end(StringBuilder output) {
        System.out.println(output);
        System.exit(0);
    }
}