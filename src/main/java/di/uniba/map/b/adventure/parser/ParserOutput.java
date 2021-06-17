/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.adventure.parser;

import di.uniba.map.b.adventure.type.AdvObject;
import di.uniba.map.b.adventure.type.AdvObjectContainer;
import di.uniba.map.b.adventure.type.Command;

/**
 *
 * @author le bimbe di Luca
 */

public class ParserOutput {

    private Command command;

    private AdvObject object;
    
    private AdvObjectContainer objectContainer; //contenitore
    
    private AdvObject invObject;

    public ParserOutput(Command command, AdvObject object) {
        this.command = command;
        this.object = object;
    }

    public ParserOutput(Command command, AdvObject object, AdvObject invObject) {
        this.command = command;
        this.object = object;
        this.invObject = invObject;
    }
    
    public ParserOutput(Command command, AdvObject object1, AdvObject invObject, AdvObjectContainer objectContainer) {
        this.command = command;
        this.object = object1;
        this.objectContainer = objectContainer;
        this.invObject = invObject;
    }

    public Command getCommand() {
        return this.command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public AdvObject getObject() {
        return this.object;
    }
    
    public AdvObjectContainer getObject2() {
        return this.objectContainer;
    }

    public void setObject(AdvObject object) {
        this.object = object;
    }
    
    public void setObject2(AdvObjectContainer object) {
        this.objectContainer = object;
    }

    public AdvObject getInvObject() {
        return invObject;
    }

    public void setInvObject(AdvObject invObject) {
        this.invObject = invObject;
    }

}