package com.mycompany.turnbasedgame;

import java.io.Serializable;
import java.util.HashMap;

public class Item implements Serializable{
    private String name, type, rarity, description;
    private boolean equippable;
    private HashMap<String, Integer> stats;

    public static class Builder implements Serializable{
        private String name, type, rarity, description;
        private boolean equippable = false;
        private HashMap<String, Integer> stats = new HashMap<>();

        public Builder(String name) { this.name = name; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder rarity(String rarity) { this.rarity = rarity; return this; }
        public Builder equippable(boolean eq) { this.equippable = eq; return this; }
        public Builder addStat(String stat, int value) { this.stats.put(stat, value); return this; }
        public Builder description(String desc) { this.description = desc; return this; }
        public Item build() { return new Item(this); }
    }

    private Item(Builder b) {
        this.name = b.name;
        this.type = b.type;
        this.rarity = b.rarity;
        this.equippable = b.equippable;
        this.stats = b.stats;
        this.description = b.description;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public String getRarity() { return rarity; }
    public boolean isEquippable() { return equippable; }
    public HashMap<String, Integer> getStats() { return stats; }
    public String getDescription() { return description; }
}