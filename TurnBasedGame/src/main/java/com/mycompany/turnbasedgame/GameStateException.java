package com.mycompany.turnbasedgame;

import java.io.Serializable;

//chatgpt suggested this to find bugs or errors easily
public class GameStateException extends Exception implements Serializable{
    public GameStateException(String message) {
        super(message);
    }
}