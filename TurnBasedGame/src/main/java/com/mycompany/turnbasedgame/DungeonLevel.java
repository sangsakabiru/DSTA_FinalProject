package com.mycompany.turnbasedgame;

import java.io.Serializable;
import java.util.*;

public class DungeonLevel implements Serializable{

    private String name;
    private int level;
    private int currentStep;
    private int maxSteps;
    private Random random;
    private Monster[] monsterPool;
    private Item[] lootPool;
    private Monster boss;
    private boolean complete = false;
    private List<String> events; //pre-generated events
    private String theme;

    public DungeonLevel(String name, int level, int maxSteps, String theme) {
        this.name = name;
        this.level = level;
        this.currentStep = 0;
        this.maxSteps = maxSteps;
        this.theme = theme;
        this.random = new Random();
        initializeMonsterPool();
        initializeLootPool();
        initializeBoss();
        generateEvents();
    }

    //legacy constructor for backward compatibility
    public DungeonLevel(String name, int level, int maxSteps) {
        this(name, level, maxSteps, "default");
    }

    private void initializeMonsterPool() {
        List<Monster> monsters = new ArrayList<>();

        //base monsters (always present)
        monsters.add(new Monster("Goblin Rogue", 90 + level * 10, 40, 14 + level * 3, 7, 5));
        monsters.add(new Monster("Orc Brute", 120 + level * 15, 20, 18 + level * 4, 10, 2));
        monsters.add(new Monster("Shadow Mage", 80 + level * 8, 60, 12 + level * 3, 4, 14));
        monsters.add(new Monster("Stone Golem", 140 + level * 18, 10, 15 + level * 4, 15, 1));
        monsters.add(new Monster("Dire Wolf", 100 + level * 10, 30, 16 + level * 3, 8, 3));
        monsters.add(new Monster("Bandit Archer", 85 + level * 7, 25, 13 + level * 2, 6, 7));

        //specific monsters
        switch (theme.toLowerCase()) {
            case "undead":
                monsters.add(new Monster("Skeleton Warrior", 110 + level * 12, 0, 16 + level * 3, 12, 0));
                monsters.add(new Monster("Wraith", 70 + level * 8, 80, 10 + level * 2, 3, 18));
                monsters.add(new Monster("Zombie Berserker", 160 + level * 20, 0, 20 + level * 4, 8, 0));
                monsters.add(new Monster("Lich Apprentice", 90 + level * 10, 100, 12 + level * 2, 5, 20));
                break;
            case "dragon":
                monsters.add(new Monster("Dragonkin Warrior", 140 + level * 15, 60, 18 + level * 4, 14, 8));
                monsters.add(new Monster("Fire Salamander", 100 + level * 12, 40, 15 + level * 3, 6, 12));
                monsters.add(new Monster("Wyvern Scout", 120 + level * 10, 30, 17 + level * 3, 9, 6));
                monsters.add(new Monster("Dragon Cultist", 95 + level * 8, 70, 13 + level * 2, 7, 15));
                break;
            case "forest":
                monsters.add(new Monster("Treant Guardian", 180 + level * 25, 20, 14 + level * 3, 20, 5));
                monsters.add(new Monster("Dryad Enchantress", 85 + level * 8, 90, 11 + level * 2, 4, 22));
                monsters.add(new Monster("Giant Spider", 105 + level * 12, 0, 16 + level * 3, 8, 0));
                monsters.add(new Monster("Forest Troll", 150 + level * 18, 10, 19 + level * 4, 16, 2));
                break;
            case "ice":
                monsters.add(new Monster("Frost Giant", 170 + level * 20, 30, 17 + level * 4, 18, 6));
                monsters.add(new Monster("Ice Elemental", 90 + level * 10, 80, 12 + level * 2, 6, 20));
                monsters.add(new Monster("Yeti", 130 + level * 15, 20, 16 + level * 3, 14, 4));
                monsters.add(new Monster("Frozen Wraith", 80 + level * 8, 70, 11 + level * 2, 3, 18));
                break;
            case "volcanic":
                monsters.add(new Monster("Lava Golem", 160 + level * 22, 40, 18 + level * 4, 22, 8));
                monsters.add(new Monster("Fire Imp", 70 + level * 6, 60, 12 + level * 2, 4, 16));
                monsters.add(new Monster("Magma Hound", 115 + level * 12, 20, 17 + level * 3, 10, 8));
                monsters.add(new Monster("Ember Sprite", 65 + level * 5, 80, 10 + level * 2, 2, 20));
                break;
            case "abyss":
                monsters.add(new Monster("Demon Warrior", 145 + level * 18, 50, 19 + level * 4, 12, 12));
                monsters.add(new Monster("Shadow Fiend", 95 + level * 10, 90, 13 + level * 2, 5, 22));
                monsters.add(new Monster("Void Stalker", 120 + level * 14, 60, 16 + level * 3, 8, 14));
                monsters.add(new Monster("Chaos Beast", 135 + level * 16, 30, 18 + level * 3, 11, 7));
                break;
        }

        monsterPool = monsters.toArray(new Monster[0]);
    }

    private void initializeLootPool() {
        List<Item> loot = new ArrayList<>();

        //bsase consumables
        loot.add(new Item.Builder("Greater Healing Potion")
                .type("consumable").rarity("rare")
                .addStat("health", 50)
                .description("Restores 50 HP").build());

        loot.add(new Item.Builder("Mana Elixir")
                .type("consumable").rarity("rare")
                .addStat("mana", 40)
                .description("Restores 40 Mana").build());

        loot.add(new Item.Builder("Fire Bomb")
                .type("consumable").rarity("rare")
                .addStat("damage", 30)
                .description("Deals 30 fire damage to all enemies").build());

        // Base equipment
        loot.add(new Item.Builder("Steel Sword")
                .type("weapon").equippable(true).rarity("uncommon")
                .addStat("damage", 15)
                .description("A sharp blade favored by warriors.").build());

        loot.add(new Item.Builder("Enchanted Cloak")
                .type("armor").equippable(true).rarity("rare")
                .addStat("defense", 10)
                .description("A cloak that shimmers with magical energy.").build());

        //theme-specific loot
        switch (theme.toLowerCase()) {
            case "undead":
                loot.add(new Item.Builder("Bone Sword")
                        .type("weapon").equippable(true).rarity("rare")
                        .addStat("damage", 18).addStat("magical", 5)
                        .description("A sword carved from ancient bones.").build());

                loot.add(new Item.Builder("Soul Shield")
                        .type("armor").equippable(true).rarity("rare")
                        .addStat("defense", 12).addStat("magical", 3)
                        .description("A shield that absorbs spiritual energy.").build());

                loot.add(new Item.Builder("Necromancer's Tome")
                        .type("consumable").rarity("epic")
                        .addStat("magical", 15)
                        .description("Permanently increases magical power by 15.").build());
                break;

            case "dragon":
                loot.add(new Item.Builder("Dragonscale Armor")
                        .type("armor").equippable(true).rarity("epic")
                        .addStat("defense", 20).addStat("physical", 5)
                        .description("Armor forged from dragon scales.").build());

                loot.add(new Item.Builder("Flame Sword")
                        .type("weapon").equippable(true).rarity("epic")
                        .addStat("damage", 25).addStat("magical", 8)
                        .description("A sword wreathed in eternal flames.").build());

                loot.add(new Item.Builder("Dragon's Blood Elixir")
                        .type("consumable").rarity("legendary")
                        .addStat("health", 100).addStat("physical", 10)
                        .description("Permanently increases max HP by 100 and physical power by 10.").build());
                break;

            case "forest":
                loot.add(new Item.Builder("Living Wood Staff")
                        .type("weapon").equippable(true).rarity("rare")
                        .addStat("magical", 20).addStat("mana", 30)
                        .description("A staff that pulses with natural magic.").build());

                loot.add(new Item.Builder("Ranger's Bow")
                        .type("weapon").equippable(true).rarity("rare")
                        .addStat("physical", 18).addStat("damage", 12)
                        .description("A bow blessed by forest spirits.").build());
                break;

            case "ice":
                loot.add(new Item.Builder("Frost Blade")
                        .type("weapon").equippable(true).rarity("rare")
                        .addStat("damage", 20).addStat("magical", 6)
                        .description("A blade that never melts.").build());

                loot.add(new Item.Builder("Ice Crystal")
                        .type("consumable").rarity("rare")
                        .addStat("mana", 60)
                        .description("Restores 60 mana with icy power.").build());
                break;

            case "volcanic":
                loot.add(new Item.Builder("Molten Hammer")
                        .type("weapon").equippable(true).rarity("epic")
                        .addStat("damage", 28).addStat("physical", 10)
                        .description("A hammer forged in volcanic fire.").build());

                loot.add(new Item.Builder("Lava Stone")
                        .type("consumable").rarity("rare")
                        .addStat("damage", 45)
                        .description("Unleashes devastating fire damage.").build());
                break;

            case "abyss":
                loot.add(new Item.Builder("Void Blade")
                        .type("weapon").equippable(true).rarity("legendary")
                        .addStat("damage", 35).addStat("magical", 12)
                        .description("A weapon from the depths of darkness.").build());

                loot.add(new Item.Builder("Shadow Cloak")
                        .type("armor").equippable(true).rarity("epic")
                        .addStat("defense", 15).addStat("physical", 8)
                        .description("Grants protection from the void.").build());
                break;
        }

        lootPool = loot.toArray(new Item[0]);
    }

    private void initializeBoss() {
        boss = BossMonster.createBoss(name, level, theme);
    }

    private void generateEvents() {
        events = new ArrayList<>();
        for (int i = 0; i < maxSteps; i++) {
            double roll = random.nextDouble();
            if (roll < 0.5) {
                events.add("monster");
            } else if (roll < 0.75) {
                events.add("loot");
            } else if (roll < 0.9) {
                events.add("empty");
            } else {
                events.add("special"); //new special events
            }
        }
        events.add("boss");
    }

    public String processStep() {
        if (currentStep >= events.size()) {
            complete = true;
            return "empty";
        }
        String event = events.get(currentStep);
        currentStep++;
        if (event.equals("boss")) {
            complete = true;
        }
        return event;
    }

    public String peekNextEvent() {
        if (currentStep >= events.size()) {
            return "empty";
        }
        return events.get(currentStep);
    }

    public Monster getRandomMonster() {
        return monsterPool[random.nextInt(monsterPool.length)];
    }

    public Item getRandomLoot() {
        return lootPool[random.nextInt(lootPool.length)];
    }

    public Monster getBoss() {
        return boss;
    }

    // New method for special events
    public String getSpecialEvent() {
        String[] specialEvents = {
                "merchant", "shrine", "trap", "puzzle", "treasure_room"
        };
        return specialEvents[random.nextInt(specialEvents.length)];
    }

    public boolean isComplete() {
        return complete || currentStep >= events.size();
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }

    public void reset() {
        this.currentStep = 0;
        this.complete = false;
        generateEvents();
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public int getCurrentStep() {
        return currentStep;
    }

    public int getMaxSteps() {
        return maxSteps;
    }

    public String getTheme() {
        return theme;
    }
}
