/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.turnbasedgame;

import java.io.Serializable;
import java.util.Random;

public class NameGenerator implements Serializable{

    private String[] firstNames = {"Aragorn", "Legolas", "Gimli", "Boromir", "Gandalf"};
    private String[] lastNames = {"the Brave", "the Wise", "the Strong", "the Swift", "the Bold"};
    private Random random;

    public NameGenerator() {
        this.random = new Random();
    }

    public String generateName() {
        String firstName = firstNames[random.nextInt(firstNames.length)];
        String lastName = lastNames[random.nextInt(lastNames.length)];
        return firstName + " " + lastName;
    }
}
