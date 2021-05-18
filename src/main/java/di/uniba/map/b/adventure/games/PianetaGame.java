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
import java.io.FileReader;
import java.io.PrintStream;
import java.util.Iterator;
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
    
    
    @Override
    public void init() throws Exception {
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
        ovest.setAlias(new String[]{"o", "O", "Ovest", "OVEST"});
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

        Command open = new Command(CommandType.OPEN, "apri");
        open.setAlias(new String[]{});
        getCommands().add(open);

        Command push = new Command(CommandType.PUSH, "premi");
        push.setAlias(new String[]{"spingi", "attiva"});
        getCommands().add(push);

        Command use = new Command(CommandType.USE, "usa");
        use.setAlias(new String[]{"utilizza"});
        getCommands().add(use);

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

        //aggiungere nel file
        AdvObject porta = new AdvObject(ID_OBJECT_PORTA, "Porta Sala Comandi", "E' presente una scritta 'Star Enterprise'");
        porta.setOpenable(true);
        porta.setOpen(false);
        porta.setAlias(new String[]{"porta", "portone", "porta sala"});
        salaComandi.getObjects().add(porta);
        
        //AGGIUNGO COMBINAZIONI PER OGNI STANZA
        salaComandi.addCombinazioni(porta, tessera);
        
        /*
        AdvObject battery = new AdvObject(1, "batteria", "Un pacco di batterie, chissà se sono cariche.");
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
         */ //set starting room
         
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
    public void nextMove(ParserOutput p, PrintStream out) {
        if (p.getCommand() == null) {
            out.println("Non ho capito cosa devo fare! Prova con un altro comando.");
        } else {
            //move
            boolean noroom = false;
            boolean move = false;
            boolean roomLocked = false;
            boolean moveLocked = false;
            if (null != p.getCommand().getType()) switch (p.getCommand().getType()) {
                case NORD:
                    if (getCurrentRoom().isLock()) {
                        roomLocked = true;
                    }   if (getCurrentRoom().getNorth() != null && !roomLocked) {
                        setCurrentRoom(getCurrentRoom().getNorth());
                        move = true;
                    } else if (getCurrentRoom().getNorth() != null && roomLocked) {
                        moveLocked = true;
                    } else {
                        noroom = true;
                    }   break;
                case SOUTH:
                    if (getCurrentRoom().isLock()) {
                        roomLocked = true;
                    }   if (getCurrentRoom().getSouth() != null && !roomLocked) {
                        setCurrentRoom(getCurrentRoom().getSouth());
                        move = true;
                    } else if (getCurrentRoom().getSouth() != null && roomLocked) {
                        moveLocked = true;
                    } else {
                        noroom = true;
                    }   break;
                case EAST:
                    if (getCurrentRoom().isLock()) {
                        roomLocked = true;
                    }   if (getCurrentRoom().getEast() != null && !roomLocked) {
                        setCurrentRoom(getCurrentRoom().getEast());
                        move = true;
                    } else if (getCurrentRoom().getEast() != null && roomLocked) {
                        moveLocked = true;
                    } else {
                        noroom = true;
                    }   break;
                case WEST:
                    if (getCurrentRoom().isLock()) {
                        roomLocked = true;
                    }   if (getCurrentRoom().getWest() != null && !roomLocked) {
                        setCurrentRoom(getCurrentRoom().getWest());
                        move = true;
                    } else if (getCurrentRoom().getWest() != null && roomLocked) {
                        moveLocked = true;
                    } else {
                        noroom = true;
                    }   break;
                case INVENTORY:
                    out.println("Nel tuo inventario ci sono:");
                    for (AdvObject o : getInventory()) {
                        out.println(o.getName() + ": " + o.getDescription());
                    }   break;
                case LOOK_AT:
                    if (getCurrentRoom().getLook() != null && p.getObject() == null) {
                        out.println(getCurrentRoom().getLook());
                    } else if (getCurrentRoom().getLook() == null && p.getObject() == null) {
                        //aggiungere il fatto che vengano elencati gli elementi che contiene e dove è possibile andare
                        out.println("Non c'è niente di interessante qui.");
                    } else if (p.getObject() != null && p.getObject().getDescription() != null) {
                        out.println(p.getObject().getDescription());
                    } else {
                        out.println("Non è possibile osservare nulla!");
                    }   break;
                case USE:
                    if(p.getInvObject()==null){
                        out.println("Devi specificare l'oggetto da usare o devi averlo nell'inventario");
                    }else{
                        if (p.getInvObject().isUsable()){
                            if(p.getObject()==null){
                                out.println("Con cosa vorresti usare "+ p.getInvObject().getName()+" ! Non posso leggerti nel pensiero!");
                            } else {
                                if(getCurrentRoom().vediCombinazioni(p.getInvObject(), p.getObject())){
                                    if(p.getObject().getId()==ID_OBJECT_PORTA){
                                        p.getObject().setOpenable(true);
                                        getCurrentRoom().setLock(false);
                                        out.println("Hai aperto tutte le porte della navicella");
                                        
                                        
                                    }
                                }else{
                                    out.println("Non è possibile usare questi oggetti insieme!");
                                }
                            }
                        }else{
                            out.println("Non è possibile usare l'oggetto!");
                        }
                    }   break;
                case PICK_UP:
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
                    }   break;
                case OPEN:
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
                                        //qui poi li inserisce nella lista degli oggetti della stanza!SBAGLIATO!
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
                    }   break;
                case PUSH:
                    //ricerca oggetti pushabili
                    if (p.getObject() != null && p.getObject().isPushable()) {
                        out.println("Hai premuto: " + p.getObject().getName());
                        /*if (p.getObject().getId() == 3) {
                        end(out);
                        }*/
                    } else if (p.getInvObject() != null && p.getInvObject().isPushable()) {
                        out.println("Hai premuto: " + p.getInvObject().getName());
                        if (p.getInvObject().getId() == 3) {
                            end(out);
                        }
                    } else {
                        out.println("Non ci sono oggetti che puoi premere qui.");
                    }   break;
                default:
                    break;
            }

            if (noroom) {
                out.println("Da quella parte non si può andare c'è un muro! Non hai ancora acquisito i poteri per oltrepassare i muri...");
            } else if (move) {
                out.println(getCurrentRoom().getName());
                out.println("================================================");
                out.println(getCurrentRoom().getDescription());
            } else if (roomLocked && moveLocked) {
                System.out.println("prima di cambiare stanza devi fare qualcosa");
            }
        }
    }

    private void end(PrintStream out) {
        out.println("Premi il pulsante del giocattolo e in seguito ad una forte esplosione la tua casa prende fuoco...tu e tuoi famigliari cercate invano di salvarvi e venite avvolti dalle fiamme...è stata una morte CALOROSA...addio!");
        System.exit(0);
    }
}
