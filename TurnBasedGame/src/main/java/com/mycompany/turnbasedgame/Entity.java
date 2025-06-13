package com.mycompany.turnbasedgame;

import java.io.Serializable;

public abstract class Entity implements Serializable{
    protected StatusEffectManager statusEffects;

    public Entity() {
        statusEffects = new StatusEffectManager(this);
    }

    public StatusEffectManager getStatusEffects() {
        return statusEffects;
    }

    public abstract void takeDamage(int dmg);
}