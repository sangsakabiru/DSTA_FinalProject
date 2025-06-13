package com.mycompany.turnbasedgame;

import java.io.Serializable;

public class InputValidationException extends Exception implements Serializable{
    public InputValidationException(String message) {
        super(message);
    }
}