/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure;

import di.uniba.map.b.adventure.parser.ParserOutput;
import di.uniba.map.b.adventure.type.AdvObject;
import di.uniba.map.b.adventure.type.Command;
import di.uniba.map.b.adventure.type.Inventory;
import di.uniba.map.b.adventure.type.Room;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gruppo LRR
 */
public abstract class GameDescription implements Serializable{

    private final List<Room> rooms = new ArrayList<>();

    private final List<Command> commands = new ArrayList<>();

   //private final List<AdvObject> inventory = new ArrayList<>();
    private final Inventory inventory  = new Inventory();
    
    private Room currentRoom;

    public List<Room> getRooms() {
        return rooms;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public abstract void init() throws Exception;

    public abstract String nextMove(ParserOutput p);
 
}
