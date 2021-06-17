/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package di.uniba.map.b.utils;

/**
 *
 * @author Le bimbe di Luca
 */
public class User {

    private String name;

    private int score;

    public User(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Utente{" + "name=" + name + ", score=" + score + '}';
    }
}
