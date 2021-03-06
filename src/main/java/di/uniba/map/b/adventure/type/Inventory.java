/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author le bimbe di Luca
 */

public class Inventory implements Serializable{

    private List<AdvObject> list = new ArrayList<>();

    public List<AdvObject> getList() {
        return list;
    }

    public void setList(List<AdvObject> list) {
        this.list = list;
    }

    public void add(AdvObject o) {
        list.add(o);
    }

    public void remove(AdvObject o) {
        list.remove(o);
    }
    
    public AdvObject cercaObject(int index) {
        for (AdvObject object : this.list) {
            if (object.getId() == index) {
                return object;
            }
        }
        return null;
    }
}
