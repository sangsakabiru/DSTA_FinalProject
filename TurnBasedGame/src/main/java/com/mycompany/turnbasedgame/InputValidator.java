package com.mycompany.turnbasedgame;

import java.io.Serializable;
import java.util.Scanner;

public class InputValidator implements Serializable{
    public static int getIntInRange(Scanner scanner, int min, int max) throws InputValidationException {
        if (!scanner.hasNextInt()) {
            scanner.next(); //consume invalid input
            throw new InputValidationException("Input must be a number.");
        }
        int value = scanner.nextInt();
        if (value < min || value > max) {
            throw new InputValidationException("Input must be between " + min + " and " + max + ".");
        }
        return value;
    }
}