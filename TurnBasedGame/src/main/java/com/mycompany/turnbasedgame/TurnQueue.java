package com.mycompany.turnbasedgame;

import java.io.Serializable;
//import java.util.LinkedList;
import java.util.Queue;

public class TurnQueue implements Serializable{
    private Queue<String> turnOrder;
    private static final String PLAYER = "PLAYER";
    private static final String ENEMY = "ENEMY";

    public TurnQueue() {
//        this.turnOrder = new LinkedList<>();
        initializeCombat(true); //player will always be first
    }

    public void initializeCombat(boolean playerFirst) {
        turnOrder.clear();
        if (playerFirst) {
            turnOrder.offer(PLAYER);
            turnOrder.offer(ENEMY);
        } else {
            turnOrder.offer(ENEMY);
            turnOrder.offer(PLAYER);
        }
    }

    public String getNextTurn() {
        if (turnOrder.isEmpty()) {
            initializeCombat(true); //reset if queue is somehow empty
        }
        String current = turnOrder.poll();
        turnOrder.offer(current);
        return current;
    }

    public boolean isPlayerTurn() {
        return !turnOrder.isEmpty() && PLAYER.equals(turnOrder.peek());
    }

    public void clearQueue() {
        turnOrder.clear();
    }
}