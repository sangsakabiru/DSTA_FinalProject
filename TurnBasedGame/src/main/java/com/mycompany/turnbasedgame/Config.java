package com.mycompany.turnbasedgame;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Properties;

//used chatgpt for enhancement and suggested for config game file so i can edit the default value of monster dmg... player dmg... so it is easy to modify without recompiling the code

public class Config implements Serializable{    
    private static Properties props = new Properties();

    static {
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("game-config.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            System.err.println("Could not load config: " + e.getMessage());
        }
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static double getDouble(String key, double defaultValue) {
        try {
            return Double.parseDouble(props.getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}