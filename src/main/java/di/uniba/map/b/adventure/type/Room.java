/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

public class Room {

    private final int id;

    private String name;

    private String description;

    private String look;

    private boolean visible = true;

    private boolean lock = false;

    private Room south = null;

    private Room north = null;

    private Room east = null;

    private Room west = null;

    private final List<AdvObject> objects = new ArrayList<>();

    private final Map<AdvObject, AdvObject> combinazioni;

    public Room(int id) {
        this.id = id;
        this.combinazioni = new HashMap<>();
    }

    public Room(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.combinazioni = new HashMap<>();
    }

    public int getId() {
        return id;
    }
    
    public void addCombinazioni(AdvObject ogg1, AdvObject ogg2) {
        combinazioni.put(ogg1, ogg2);
    }

    public boolean vediCombinazioni(AdvObject ogg1, AdvObject ogg2) {
        boolean giusta = false;
        for (Map.Entry<AdvObject, AdvObject> e : combinazioni.entrySet()) { //combinazione ogg2 e ogg1
            if (e.getValue() == ogg1) {
                giusta = true;
            }
        }
        if (giusta == false) {
            for (Map.Entry<AdvObject, AdvObject> e : combinazioni.entrySet()) { //combinazione ogg1 e ogg2
                if (e.getValue() == ogg2) {
                    giusta = true;
                }
            }
        }
        return giusta;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public Room getSouth() {
        return south;
    }

    public void setSouth(Room south) {
        this.south = south;
    }

    public Room getNorth() {
        return north;
    }

    public Map<AdvObject, AdvObject> getCombinazioni() {
        return combinazioni;
    }

    public void setNorth(Room north) {
        this.north = north;
    }

    public Room getEast() {
        return east;
    }

    public void setEast(Room east) {
        this.east = east;
    }

    public Room getWest() {
        return west;
    }

    public void setWest(Room west) {
        this.west = west;
    }

    public List<AdvObject> getObjects() {
        return objects;
    }

    public AdvObject cercaObject(int index) {
        for (AdvObject object : this.objects) {
            if (object.getId()== index){
                return object;
            }
        }
        return null;
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + this.id;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Room other = (Room) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    public String getLook() {
        return look;
    }

    public void setLook(String look) {
        this.look = look;
    }

    public boolean isLock() {
        return lock;
    }

    public void setLock(boolean lock) {
        this.lock = lock;
    }

}
