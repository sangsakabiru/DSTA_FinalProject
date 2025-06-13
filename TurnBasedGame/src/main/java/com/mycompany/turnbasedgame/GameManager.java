package com.mycompany.turnbasedgame;

import java.util.List;
import java.util.Scanner;
import java.io.*;

public class GameManager implements Serializable {

    private transient Scanner scanner;
    private boolean gameRunning = true;
    public boolean[] dungeonCompleted;
    private static final String SAVE_FILE = "C:\\Users\\Admin\\Documents\\NetBeansProjects\\TurnBasedGame\\src\\main\\java\\com\\mycompany\\turnbasedgame\\game_save.dat";
    public Hero hero;
    public DungeonLevel[] dungeons;
    public int currentDungeonIndex = 0;
    public Monster currentEnemy;

    //skill mana costs (by skill id)
    private int getSkillManaCost(int skillId, Hero hero) {
        if (hero instanceof Wizard) {
            switch (skillId) {
                case 1:
                    return 15; //spellchain
                case 2:
                    return 10; //sparkweave
                case 3:
                    return 18; //fireball
                case 4:
                    return 30; //arcane Reflux
            }
        } else if (hero instanceof Warrior) {
            switch (skillId) {
                case 1:
                    return 10; //bullrush
                case 2:
                    return 8; //iron Guard
                case 3:
                    return 12; //piercing Sword
                case 4:
                    return 20; //wrath Unbound
            }
        } else if (hero instanceof Archer) {
            switch (skillId) {
                case 1:
                    return 14; //piercing arrow
                case 2:
                    return 18; //entrapment field
                case 3:
                    return 16; //flurry of thorns
                case 4:
                    return 28; //deadeyes mark
            }
        } else if (hero instanceof Assassin) {
            switch (skillId) {
                case 1:
                    return 10; //shadow step
                case 2:
                    return 16; //venom blades
                case 3:
                    return 14; //heartpiercer
                case 4:
                    return 32; //death lotus
            }
        }
        return 10;
    }

    public GameManager() {
        this.scanner = new Scanner(System.in);
        initializeDungeons();
        this.hero = selectHero();
        this.hero.initializeSkills();
        dungeonCompleted = new boolean[dungeons.length];
    }

    private void initializeDungeons() {
        this.dungeons = new DungeonLevel[]{
            //tier 1 dungeons
            new DungeonLevel("Shadow Crypt", 1, 5, "undead"),
            new DungeonLevel("Whispering Woods", 1, 6, "forest"),
            new DungeonLevel("Frozen Caverns", 1, 5, "ice"),
            //tier 2 dungeons
            new DungeonLevel("Dragonspire Keep", 2, 7, "dragon"),
            new DungeonLevel("Molten Depths", 2, 8, "volcanic"),
            new DungeonLevel("Bandit Stronghold", 2, 6, "default"),
            //tier 3 dungeons
            new DungeonLevel("Abyssal Sanctum", 3, 10, "abyss"),
            new DungeonLevel("Crystal Mines", 3, 9, "ice"),
            new DungeonLevel("Ancient Ruins", 3, 8, "undead"),
            //tier 4 dungeons
            new DungeonLevel("Dragon's Lair", 4, 12, "dragon"),
            new DungeonLevel("Void Citadel", 4, 11, "abyss"),
            new DungeonLevel("World Tree", 4, 10, "forest")
        };
        this.dungeonCompleted = new boolean[dungeons.length];
    }

    public void startGame() {
        System.out.println("=== ENHANCED DUNGEON CRAWLER RPG ===");
        System.out.println("Welcome, adventurer! Your journey begins...\n");

        while (gameRunning) {
            displayMainMenu();
        }
    }

    private void displayMainMenu() {
        System.out.println("\n===== MAIN MENU =====");
        System.out.println("1. Start Dungeon Crawl");
        System.out.println("2. Manage Character");
        System.out.println("3. Select Hero");
        System.out.println("4. Fountain (Restore HP/Mana)");
        System.out.println("5. Dungeon Progress");
        System.out.println("6. Save Game");
        System.out.println("7. Load Game");
        System.out.println("8. Exit Game");
        System.out.print("Choose an option: ");

        int choice = -1;
        while (choice == -1) {
            try {
                choice = InputValidator.getIntInRange(scanner, 1, 8);
            } catch (InputValidationException e) {
                System.out.println(e.getMessage());
                System.out.print("Try again: ");
                scanner.nextLine();
            }
        }

        switch (choice) {
            case 1:
                selectDungeon();
                break;
            case 2:
                hero.displayCharacterSheet();
                break;
            case 3:
                this.hero = selectHero();
                this.hero.initializeSkills();
                break;
            case 4:
                hero.heal(hero.getMaxHp());
                hero.restoreMana(hero.getMaxMana());
                System.out.println("You are fully healed and your mana is restored at the Fountain!");
                break;
            case 5:
                displayDungeonProgress();
                break;
            case 6:
                saveGame();
                break;
            case 7:
                loadGame();
                break;
            case 8:
                gameRunning = false;
                System.out.println("Exiting game...");
                break;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private void selectDungeon() {
        System.out.println("\n===== SELECT DUNGEON =====");

        //group dungeons by tier
        System.out.println("TIER 1 DUNGEONS (Beginner):");
        for (int i = 0; i < 3; i++) {
            String status = dungeonCompleted[i] ? " [COMPLETED]" : "";
            System.out.println((i + 1) + ". " + dungeons[i].getName()
                    + " (Level " + dungeons[i].getLevel() + ", " + dungeons[i].getTheme() + ")" + status);
        }

        System.out.println("\nTIER 2 DUNGEONS (Intermediate):");
        for (int i = 3; i < 6; i++) {
            String status = dungeonCompleted[i] ? " [COMPLETED]" : "";
            System.out.println((i + 1) + ". " + dungeons[i].getName()
                    + " (Level " + dungeons[i].getLevel() + ", " + dungeons[i].getTheme() + ")" + status);
        }

        System.out.println("\nTIER 3 DUNGEONS (Advanced):");
        for (int i = 6; i < 9; i++) {
            String status = dungeonCompleted[i] ? " [COMPLETED]" : "";
            System.out.println((i + 1) + ". " + dungeons[i].getName()
                    + " (Level " + dungeons[i].getLevel() + ", " + dungeons[i].getTheme() + ")" + status);
        }

        System.out.println("\nTIER 4 DUNGEONS (Master):");
        for (int i = 9; i < 12; i++) {
            String status = dungeonCompleted[i] ? " [COMPLETED]" : "";
            System.out.println((i + 1) + ". " + dungeons[i].getName()
                    + " (Level " + dungeons[i].getLevel() + ", " + dungeons[i].getTheme() + ")" + status);
        }

        System.out.println("\n" + (dungeons.length + 1) + ". Back to Main Menu");
        System.out.print("Choose a dungeon: ");

        int choice = -1;
        while (choice == -1) {
            try {
                choice = InputValidator.getIntInRange(scanner, 1, dungeons.length + 1);
            } catch (InputValidationException e) {
                System.out.println(e.getMessage());
                System.out.print("Try again: ");
                scanner.nextLine();
            }
        }

        if (choice <= dungeons.length) {
            currentDungeonIndex = choice - 1;
            startDungeonCrawl();
        }
    }

    private void displayDungeonProgress() {
        System.out.println("\n===== DUNGEON PROGRESS =====");
        int completed = 0;

        for (int i = 0; i < dungeons.length; i++) {
            String status = dungeonCompleted[i] ? "✓ COMPLETED" : "○ Not Completed";
            System.out.println(dungeons[i].getName() + " - " + status);
            if (dungeonCompleted[i]) {
                completed++;
            }
        }

        System.out.println("\nOverall Progress: " + completed + "/" + dungeons.length
                + " (" + (completed * 100 / dungeons.length) + "%)");

        if (completed == dungeons.length) {
            System.out.println("CONGRATULATIONS! You've completed all dungeons!");
            System.out.println("You are a true Dungeon Master!");
        }
    }

    private void startDungeonCrawl() {
        DungeonLevel dungeon = dungeons[currentDungeonIndex];
        dungeon.reset();
        int peekCooldown = 0;

        System.out.println("\nEntering " + dungeon.getName() + " (Level " + dungeon.getLevel()
                + ", Theme: " + dungeon.getTheme() + ")...");

        while (!dungeon.isComplete() && hero.isAlive()) {
            System.out.println("\nDungeon Step " + (dungeon.getCurrentStep() + 1) + "/" + dungeon.getMaxSteps());
            String nextEvent = dungeon.peekNextEvent();
            System.out.println("Choose your action:");
            System.out.println("1. Advance");
            System.out.println("2. Rest (restore small HP/Mana)");
            System.out.println("3. Scout (peek next event, costs 15 mana, usable every 2 turns)"
                    + (peekCooldown > 0 ? " [CD: " + peekCooldown + "]" : ""));
            if ("monster".equals(nextEvent)) {
                System.out.println("4. Skip Monster (costs 50 mana)");
            }

            int maxOption = "monster".equals(nextEvent) ? 4 : 3;
            int action = -1;
            while (action == -1) {
                try {
                    action = InputValidator.getIntInRange(scanner, 1, maxOption);
                } catch (InputValidationException e) {
                    System.out.println(e.getMessage());
                    System.out.print("Try again: ");
                    scanner.nextLine();
                }
            }

            switch (action) {
                case 1:
                    handleDungeonEvent(dungeon.processStep(), dungeon);
                    if (peekCooldown > 0) {
                        peekCooldown--;
                    }
                    break;
                case 2:
                    int restHp = Math.min(30, hero.getMaxHp() - hero.getHp());
                    int restMana = Math.min(15, hero.getMaxMana() - hero.getMana());
                    hero.heal(restHp);
                    hero.restoreMana(restMana);
                    System.out.println("You rest and recover " + restHp + " HP and " + restMana + " Mana.");
                    if (peekCooldown > 0) {
                        peekCooldown--;
                    }
                    break;
                case 3:
                    if (peekCooldown > 0) {
                        System.out.println("Scout is on cooldown for " + peekCooldown + " more turn(s)!");
                        break;
                    }
                    if (hero.getMana() < 15) {
                        System.out.println("Not enough mana to scout! (Requires 15 mana)");
                        break;
                    }
                    hero.consumeMana(15);
                    String peek = dungeon.peekNextEvent();
                    System.out.println("You scout ahead and see: "
                            + (peek.equals("monster") ? "A monster lurking!"
                            : peek.equals("loot") ? "A treasure glimmering!"
                            : peek.equals("boss") ? "A powerful boss awaits!"
                            : peek.equals("special") ? "Something unusual..."
                            : "Nothing of interest."));
                    peekCooldown = 2;
                    break;
                case 4:
                    if (!"monster".equals(nextEvent)) {
                        System.out.println("You can only skip if a monster is ahead!");
                        break;
                    }
                    if (hero.getMana() < 50) {
                        System.out.println("Not enough mana to skip the monster! (Requires 50 mana)");
                        break;
                    }
                    hero.consumeMana(50);
                    dungeon.processStep();
                    System.out.println("You sneak past the monster, but it costs you energy!");
                    if (peekCooldown > 0) {
                        peekCooldown--;
                    }
                    break;
            }
        }

        if (!hero.isAlive()) {
            System.out.println("You have fallen in the dungeon...");
        } else {
            System.out.println("You cleared " + dungeon.getName() + "!");
            dungeonCompleted[currentDungeonIndex] = true;

            //reward
            int bonusExp = 100 + (dungeon.getLevel() * 25);
            hero.gainExperience(bonusExp);
            System.out.println("Completion Bonus: " + bonusExp + " EXP!");
        }
    }

    private void handleDungeonEvent(String event, DungeonLevel dungeon) {
        switch (event) {
            case "monster":
                Monster monster = dungeon.getRandomMonster();
                System.out.println("A wild " + monster.getName() + " appears!");
                startCombat(monster);
                break;
            case "loot":
                Item loot = dungeon.getRandomLoot();
                hero.getInventory().addItem(loot);
                System.out.println("You found: " + loot.getName() + " (" + loot.getDescription() + ")");
                break;
            case "empty":
                System.out.println("The path is eerily quiet... nothing happens.");
                break;
            case "special":
                handleSpecialEvent(dungeon);
                break;
            case "boss":
                Monster boss = dungeon.getBoss();
                System.out.println("Boss Encounter! " + boss.getName() + " emerges!");
                startCombat(boss);
                dungeon.setComplete(true);
                break;
        }
    }

    private void handleSpecialEvent(DungeonLevel dungeon) {
        String specialEvent = dungeon.getSpecialEvent();

        switch (specialEvent) {
            case "merchant":
                System.out.println("A mysterious merchant appears!");
                System.out.println("He offers to trade a rare item for 50 gold... but you have no gold!");
                System.out.println("Instead, he gives you a small gift and disappears.");
                hero.heal(25);
                hero.restoreMana(20);
                break;

            case "shrine":
                System.out.println("You discover an ancient shrine!");
                System.out.println("Its power permanently enhances your abilities!");
                //permanent stat boost
                hero.setPhysical(hero.getPhysical() + 2);
                hero.setMagical(hero.getMagical() + 2);
                System.out.println("Physical and Magical power increased by 2!");
                break;

            case "trap":
                System.out.println("You step on a pressure plate! A trap activates!");
                System.out.println("Choose your reaction:");
                System.out.println("1. Dodge (requires 20 mana)");
                System.out.println("2. Shield (take reduced damage)");
                System.out.println("3. Tank it (take full damage)");

                int trapChoice = -1;
                while (trapChoice == -1) {
                    try {
                        trapChoice = InputValidator.getIntInRange(scanner, 1, 3);
                    } catch (InputValidationException e) {
                        System.out.println(e.getMessage());
                        System.out.print("Try again: ");
                        scanner.nextLine();
                    }
                }

                switch (trapChoice) {
                    case 1:
                        if (hero.getMana() >= 20) {
                            hero.consumeMana(20);
                            System.out.println("You dodge the trap completely!");
                        } else {
                            System.out.println("Not enough mana! You take the full hit!");
                            hero.takeDamage(25);
                        }
                        break;
                    case 2:
                        hero.takeDamage(12);
                        System.out.println("You brace yourself and take 12 damage!");
                        break;
                    case 3:
                        hero.takeDamage(25);
                        System.out.println("You take the full trap damage: 25 HP!");
                        break;
                }
                break;

            case "puzzle":
                System.out.println("You encounter an ancient puzzle!");
                System.out.println(
                        "A riddle appears on the wall: 'I am not alive, but I grow; I don't have lungs, but I need air.'");
                System.out.println("What am I?");
                System.out.println("1. Fire");
                System.out.println("2. Plant");
                System.out.println("3. Wind");
                System.out.println("4. Skip puzzle");

                int puzzleChoice = -1;
                while (puzzleChoice == -1) {
                    try {
                        puzzleChoice = InputValidator.getIntInRange(scanner, 1, 4);
                    } catch (InputValidationException e) {
                        System.out.println(e.getMessage());
                        System.out.print("Try again: ");
                        scanner.nextLine();
                    }
                }

                if (puzzleChoice == 1) { // Fire is correct
                    System.out.println("Correct! The puzzle rewards you with a treasure!");
                    Item puzzleReward = new Item.Builder("Wisdom Gem")
                            .type("consumable").rarity("rare")
                            .addStat("experience", 75)
                            .description("Grants 75 experience points when used").build();
                    hero.getInventory().addItem(puzzleReward);
                    hero.gainExperience(75);
                    System.out.println("You gained 75 experience!");
                } else if (puzzleChoice == 4) {
                    System.out.println("You decide to skip the puzzle and move on.");
                } else {
                    System.out.println("Incorrect... The puzzle mechanism triggers a minor trap!");
                    hero.takeDamage(10);
                    System.out.println("You take 10 damage from the trap!");
                }
                break;

            case "treasure_room":
                System.out.println("You discover a hidden treasure room!");
                System.out.println("Three chests lie before you, but you can only open one:");
                System.out.println("1. Golden Chest (looks valuable)");
                System.out.println("2. Silver Chest (moderate value)");
                System.out.println("3. Wooden Chest (simple but sturdy)");

                int chestChoice = -1;
                while (chestChoice == -1) {
                    try {
                        chestChoice = InputValidator.getIntInRange(scanner, 1, 3);
                    } catch (InputValidationException e) {
                        System.out.println(e.getMessage());
                        System.out.print("Try again: ");
                        scanner.nextLine();
                    }
                }

                //random rewards based on choice
                switch (chestChoice) {
                    case 1: //golden chest - high risk/reward
                        if (Math.random() < 0.6) {
                            Item goldReward = new Item.Builder("Epic Elixir")
                                    .type("consumable").rarity("epic")
                                    .addStat("health", 100).addStat("mana", 50)
                                    .description("Fully restores health and mana").build();
                            hero.getInventory().addItem(goldReward);
                            System.out.println("Jackpot! You found an Epic Elixir!");
                        } else {
                            System.out.println("The chest was trapped! You take 20 damage!");
                            hero.takeDamage(20);
                        }
                        break;
                    case 2: //silver chest - moderate reward
                        Item silverReward = new Item.Builder("Quality Potion")
                                .type("consumable").rarity("uncommon")
                                .addStat("health", 50)
                                .description("Restores 50 HP").build();
                        hero.getInventory().addItem(silverReward);
                        System.out.println("You found a Quality Healing Potion!");
                        break;
                    case 3: //wooden chest - guaranteed small reward
                        hero.heal(30);
                        hero.restoreMana(20);
                        System.out.println("You found some supplies! Restored 30 HP and 20 Mana!");
                        break;
                }
                break;
        }
    }

    private void startCombat(Monster enemy) {
        TurnQueue turnQueue = new TurnQueue();
        boolean playerFirst = true;
        turnQueue.initializeCombat(playerFirst);

        while (hero.isAlive() && enemy.isAlive()) {
            if (turnQueue.isPlayerTurn()) {
                playerCombatTurn(enemy);
            } else {
                enemyCombatTurn(enemy);
            }
            turnQueue.getNextTurn();
        }

        if (hero.isAlive()) {
            System.out.println("You defeated " + enemy.getName() + "!");
            hero.gainExperience(40 + enemy.getMaxHealth() / 8);
        }
    }

    public void playerCombatTurn(Monster enemy) {
        System.out.println("\nYour HP: " + hero.getHp() + "/" + hero.getMaxHp()
                + " | Mana: " + hero.getMana() + "/" + hero.getMaxMana());
        System.out.println(enemy.getName() + " HP: " + enemy.getHealth() + "/" + enemy.getMaxHealth());
        System.out.println("Choose action: 1) Attack  2) Use Skill  3) Use Item  4) Defend");

        int action = -1;
        while (action == -1) {
            try {
                action = InputValidator.getIntInRange(scanner, 1, 4);
            } catch (InputValidationException e) {
                System.out.println(e.getMessage());
                System.out.print("Try again: ");
                scanner.nextLine();
            }
        }

        switch (action) {
            case 1: //normal attack
                int dmg = hero.attack();
                enemy.takeDamage(dmg);
                System.out.println("You attack and deal " + dmg + " damage!");
                //venom blades poison
                if (hero.getStatusEffects().hasEffect("venom_blades")) {
                    enemy.getStatusEffects().addEffect("poison", 3, hero.getPhysical() / 3 + 3);
                    System.out.println("Your venom blades poison the enemy!");
                    //reduce venom blades duration by 1
                    StatusEffectManager sm = hero.getStatusEffects();
                    StatusEffect venom = sm.activeEffects.get("venom_blades");
                    venom.reduceDuration();
                    if (venom.getDuration() <= 0) {
                        sm.activeEffects.remove("venom_blades");
                    }
                }
                break;
            case 2:
                useSkillMenu(enemy);
                break;
            case 3:
                useItemMenu();
                break;
            case 4:
                hero.setDefending(true);
                System.out.println("You brace for the next attack!");
                break;
        }

        hero.processTurn();
        enemy.processTurn();
        hero.getSkillManager().reduceCooldowns();
    }

    private void enemyCombatTurn(Monster enemy) {
        if (enemy.isStunned()) {
            System.out.println(enemy.getName() + " is stunned and skips its turn!");
            enemy.setStunned(false);
            return;
        }

        if (enemy instanceof BossMonster) {
            BossMonster boss = (BossMonster) enemy;
            BossMonster.BossSkillSet skillSet = boss.getBossSkillSet();
            BossMonster.BossSkill skill = skillSet.getReadySkill();
            if (skill != null) {
                System.out.println(boss.getName() + " uses " + skill.getName() + "!");
                System.out.println(skill.getDescription());

                switch (skill.getName()) {
                    //lich king
                    case "Soul Drain":
                        hero.takeDamage(skill.getBaseDamage());
                        int heal = skill.getBaseDamage() / 2;
                        enemy.heal(heal);
                        System.out.println("You take " + skill.getBaseDamage() + " damage! " + boss.getName()
                                + " heals for " + heal + "!");
                        break;
                    case "Wail of the Damned":
                        hero.getStatusEffects().addEffect("weakened", 2, 4); // -4 attack for 2 turns
                        System.out.println("Your attack power is reduced!");
                        break;
                    case "Raise Undead":
                        //summon minions for simplicity, boss gains a shield for 3 turns
                        enemy.getStatusEffects().addEffect("shield", 3, 15); //absorb 15 damage per hit for 3 turns
                        System.out.println("Skeletal minions absorb damage for the boss!");
                        break;

                    //ancient dragon
                    case "Inferno Breath":
                        hero.takeDamage(skill.getBaseDamage());
                        hero.getStatusEffects().addEffect("burn", 2, 8); //8 burn damage for 2 turns
                        System.out.println("You are burned for 2 turns!");
                        break;
                    case "Wing Tempest":
                        hero.setStunned(true);
                        System.out.println("You are knocked back and stunned for 1 turn!");
                        break;
                    case "Molten Scales":
                        enemy.getStatusEffects().addEffect("reflect", 2, 8); //reflect 8 melee damage for 2 turns
                        enemy.getStatusEffects().addEffect("defense_up", 2, 8); //+8 defense for 2 turns
                        System.out.println(boss.getName() + "'s defense increases and reflects melee damage!");
                        break;

                    //elder treant
                    case "Roots of Binding":
                        hero.getStatusEffects().addEffect("root", 2, 0); //immobilized for 2 turns
                        System.out.println("You are immobilized and cannot dodge!");
                        break;
                    case "Nature’s Resilience":
                        enemy.heal(40);
                        enemy.getStatusEffects().addEffect("resist", 2, 8); //+8 resistance for 2 turns
                        System.out.println(boss.getName() + " heals and gains resistance!");
                        break;
                    case "Thorn Burst":
                        hero.takeDamage(skill.getBaseDamage());
                        hero.getStatusEffects().addEffect("bleed", 2, 6); //6 bleed damage for 2 turns
                        System.out.println("You are hit by thorns and start bleeding!");
                        break;

                    //frost titan
                    case "Glacial Slam":
                        hero.takeDamage(skill.getBaseDamage());
                        if (Math.random() < 0.4) { //40% freeze chance
                            hero.getStatusEffects().addEffect("frozen", 1, 0);
                            System.out.println("You are frozen and skip your next turn!");
                        }
                        break;
                    case "Frozen Aura":
                        hero.getStatusEffects().addEffect("slow", 2, 0);
                        hero.getStatusEffects().addEffect("cold", 2, 7); //7 cold damage for 2 turns
                        System.out.println("You are slowed and take cold damage!");
                        break;
                    case "Ice Spikes":
                        hero.takeDamage(skill.getBaseDamage());
                        hero.getStatusEffects().addEffect("slow", 2, 0);
                        System.out.println("You are pierced and slowed!");
                        break;
                    case "Cryo Overload":
                        hero.takeDamage(skill.getBaseDamage());
                        hero.getStatusEffects().addEffect("cold", 2, 10); //10 cold damage for 2 turns
                        if (Math.random() < 0.3) {
                            hero.getStatusEffects().addEffect("frozen", 1, 0);
                            System.out.println("You are frozen by the blizzard!");
                        }
                        break;

                    //volcanic behemoth
                    case "Lava Eruption":
                        hero.takeDamage(skill.getBaseDamage());
                        hero.getStatusEffects().addEffect("burn", 2, 10);
                        System.out.println("You are burned by lava!");
                        break;
                    case "Molten Charge":
                        hero.takeDamage(skill.getBaseDamage());
                        System.out.println("You are hit by a molten charge!");
                        break;
                    case "Crackling Fury":
                        enemy.getStatusEffects().addEffect("rage", 2, 10); //+10 attack, -8 defense for 2 turns
                        enemy.getStatusEffects().addEffect("defense_down", 2, 8);
                        System.out.println(boss.getName() + " enters a rage state!");
                        break;
                    case "Smoldering Earth":
                        hero.getStatusEffects().addEffect("healing_down", 2, 0);
                        hero.getStatusEffects().addEffect("burn", 2, 6);
                        System.out.println("Your healing is reduced and you are burned!");
                        break;

                    //abyssal overlord
                    case "Void Lash":
                        hero.takeDamage(skill.getBaseDamage());
                        if (Math.random() < 0.3) {
                            hero.getStatusEffects().addEffect("silence", 1, 0);
                            System.out.println("You are silenced and cannot use skills!");
                        }
                        break;
                    case "Abyssal Grasp":
                        hero.takeDamage(skill.getBaseDamage());
                        hero.getStatusEffects().addEffect("resist_down", 2, 8); //-8 resistance for 2 turns
                        System.out.println("Your resistances are lowered!");
                        break;
                    case "Reality Fracture":
                        //random effect 50% chance to stun, 50% chance to swap HP and Mana
                        if (Math.random() < 0.5) {
                            hero.setStunned(true);
                            System.out.println("Reality fractures! You are stunned!");
                        } else {
                            int temp = hero.getHp();
                            hero.setHp(hero.getMana());
                            hero.setMana(temp);
                            System.out.println("Reality fractures! Your HP and Mana are swapped!");
                        }
                        break;
                    case "Endless Hunger":
                        enemy.heal(40);
                        System.out.println(boss.getName() + " absorbs souls and restores health!");
                        break;

                    //default
                    default:
                        hero.takeDamage(skill.getBaseDamage());
                        System.out.println("You take " + skill.getBaseDamage() + " damage!");
                        break;
                }

                skill.use(); //set skill on cooldown
            } else {
                //no skill ready then do a normal attack
                int dmg = boss.attack();
                hero.takeDamage(dmg);
                System.out.println(boss.getName() + " attacks for " + dmg + " damage!");
            }
            skillSet.reduceAllCooldowns();
        } else {
            //normal monster logic
            int dmg = enemy.attack();
            hero.takeDamage(dmg);
            System.out.println(enemy.getName() + " attacks for " + dmg + " damage!");
        }

        //assassins untargetable
        if (hero.getStatusEffects().hasEffect("untargetable")) {
            System.out.println("You are untargetable and evade all attacks this turn!");
            hero.processTurn();
            enemy.processTurn();
            hero.getSkillManager().reduceCooldowns();
            return;
        }

        //assassins evade
        if (hero.getStatusEffects().hasEffect("evade")) {
            int chance = hero.getStatusEffects().activeEffects.get("evade").getValue();
            if (Math.random() * 100 < chance) {
                System.out.println("You evade the attack thanks to Shadow Step!");
                hero.getStatusEffects().activeEffects.remove("evade");
                hero.processTurn();
                enemy.processTurn();
                hero.getSkillManager().reduceCooldowns();
                return;
            }
            //remove evade after one use
            hero.getStatusEffects().activeEffects.remove("evade");
        }

        int dmg = Math.max(1, enemy.attack() - hero.getDefense() / 3);
        if (hero.isDefending()) {
            dmg /= 2;
            hero.setDefending(false);
            System.out.println("You defend and reduce the damage!");
        }

        hero.takeDamage(dmg);
        System.out.println(enemy.getName() + " attacks and deals " + dmg + " damage!");

        hero.processTurn();
        enemy.processTurn();
        hero.getSkillManager().reduceCooldowns();
    }

    private void useSkillMenu(Monster enemy) {
        var skills = hero.getSkillManager().getAvailableSkills();
        if (skills.isEmpty()) {
            System.out.println("No skills available!");
            return;
        }

        System.out.println("Choose a skill:");
        for (int i = 0; i < skills.size(); i++) {
            Skill s = skills.get(i);
            int manaCost = getSkillManaCost(s.getId(), hero);
            System.out.println((i + 1) + ". " + s.getName() + " - " + s.getDescription()
                    + " (Mana: " + manaCost + ", CD: " + hero.getSkillManager().getSkillCooldown(s.getId()) + ")");
        }

        int idx = -1;
        while (idx == -1) {
            try {
                idx = InputValidator.getIntInRange(scanner, 1, skills.size()) - 1;
            } catch (InputValidationException e) {
                System.out.println(e.getMessage());
                System.out.print("Try again: ");
                scanner.nextLine();
            }
        }

        Skill skill = skills.get(idx);
        int manaCost = getSkillManaCost(skill.getId(), hero);

        if (!hero.getSkillManager().isSkillReady(skill.getId())) {
            System.out.println("Skill is on cooldown!");
            return;
        }

        if (hero.getMana() < manaCost) {
            System.out.println("Not enough mana!");
            return;
        }

        hero.consumeMana(manaCost);
        executeSkill(skill, enemy);
        hero.getSkillManager().setSkillCooldown(skill.getId(), skill.getBaseCooldown());
    }

    private void executeSkill(Skill skill, Monster enemy) {
        if (hero instanceof Wizard) {
            executeWizardSkill(skill, enemy);
        } else if (hero instanceof Warrior) {
            executeWarriorSkill(skill, enemy);
        } else if (hero instanceof Archer) {
            executeArcherSkill(skill, enemy);
        } else if (hero instanceof Assassin) {
            executeAssassinSkill(skill, enemy);
        }
    }

    private String executeWizardSkill(Skill skill, Monster enemy) {
        switch (skill.getName()) {
            case "Spellchain":
                return "You cast Spellchain! Your next two spells are empowered.";
            case "Sparkweave":
                return "You cast Sparkweave! Your next attack will deal bonus magic damage.";
            case "Fireball":
                int dot = hero.calculateMagicalDamage() / 2 + 6;
                enemy.getStatusEffects().addEffect("poison", 3, dot);
                return "You hurl a Fireball! " + enemy.getName() + " will take " + dot + " damage for 3 turns.";
            case "Arcane Reflux":
                return "You prepare Arcane Reflux! All attacks will be absorbed and redirected in 3 turns.";
            default:
                return "You use a mysterious spell!";
        }
    }

    private String executeWarriorSkill(Skill skill, Monster enemy) {
        switch (skill.getName()) {
            case "Bullrush":
                int bullrushDmg = hero.attack() + 12;
                enemy.takeDamage(bullrushDmg);
                enemy.setStunned(true);
                return "You charge with Bullrush, dealing" + bullrushDmg
                        + "< damage and stunning the enemy for 1 turn!";
            case "Iron Guard":
                hero.setDefending(true);
                return "You raise your guard, reducing and reflecting damage next turn.";
            case "Piercing Sword":
                int dot = hero.attack() / 2 + 5;
                enemy.getStatusEffects().addEffect("poison", 3, dot);
                return "You strike with Piercing Sword! " + enemy.getName() + " will take " + dot
                        + " damage for 3 turns.";
            case "Wrath Unbound":
                return "You enter a berserker state! Attack up, lifesteal, and CC immunity for 3 turns.";
            default:
                return "You use a powerful warrior skill!";
        }
    }

    private String executeAssassinSkill(Skill skill, Monster enemy) {
        switch (skill.getName()) {
            case "Shadow Step":
                hero.getStatusEffects().addEffect("evade", 1, 20); //20% evade next attack
                return "You vanish into the shadows! 20% chance to evade the next attack.";
            case "Venom Blades":
                hero.getStatusEffects().addEffect("venom_blades", 3, 0);
                return "You coat your blades in venom! Next 3 attacks apply poison.";
            case "Heartpiercer":
                boolean poisoned = enemy.getStatusEffects().hasEffect("poison");
                boolean slowed = enemy.getStatusEffects().hasEffect("root");
                int bonus = (poisoned || slowed) ? 18 : 0;
                int hpDmg = hero.attack() + bonus;
                enemy.takeDamage(hpDmg);
                return "You strike with Heartpiercer! "
                        + (bonus > 0 ? "Bonus damage for poisoned/slowed enemy! " : "")
                        + "Dealt " + hpDmg + " damage.";
            case "Death Lotus":
                hero.getStatusEffects().addEffect("untargetable", 4, 0);
                return "You become untargetable for 4 turns!";
            default:
                return "You use a mysterious assassin technique!";
        }
    }

    private String executeArcherSkill(Skill skill, Monster enemy) {
        switch (skill.getName()) {
            case "Piercing Arrow":
                int baseDmg = hero.attack() + 8;
                int ignoreArmor = Math.max(0, enemy.getDefense() - 8);
                int crit = (Math.random() < 0.3) ? (int) (baseDmg * 1.5) : baseDmg;
                int totalDmg = crit + ignoreArmor;
                enemy.takeDamage(totalDmg);
                return "You fire a Piercing Arrow! "
                        + (crit > baseDmg ? "Critical hit! " : "")
                        + "Dealt " + totalDmg + " damage (ignores armor).";
            case "Entrapment Field":
                enemy.setStunned(true);
                enemy.getStatusEffects().addEffect("root", 3, 0);
                return "You deploy an Entrapment Field! Enemy is rooted/stunned for 3 turns.";
            case "Flurry of Thorns":
                int hits = 3 + (int) (Math.random() * 2);
                int total = 0;
                for (int i = 0; i < hits; i++) {
                    int hitDmg = hero.attack() / 3 + 3;
                    total += hitDmg;
                    enemy.getStatusEffects().addEffect("bleed", 2, hitDmg / 2);
                }
                enemy.takeDamage(total);
                return "You unleash a Flurry of Thorns! " + hits + " hits for " + total
                        + " total damage and stacking bleed.";
            case "Deadeye's Mark":
                enemy.getStatusEffects().addEffect("marked", 2, 0);
                return "You mark your target. In 2 turns, unleash a devastating arrow!";
            default:
                return "You use a mysterious archer technique!";
        }
    }

    private void useItemMenu() {
        var items = hero.getInventory().getItems();
        if (items.isEmpty()) {
            System.out.println("No items in inventory!");
            return;
        }
        System.out.println("Choose an item to use:");
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            System.out.println((i + 1) + ". " + item.getName() + " (" + item.getDescription() + ")");
        }
        int idx = -1;
        while (idx == -1) {
            try {
                idx = InputValidator.getIntInRange(scanner, 1, items.size()) - 1;
            } catch (InputValidationException e) {
                System.out.println(e.getMessage());
                System.out.print("Try again: ");
                scanner.nextLine();
            }
        }
        Item item = items.get(idx);
        if (item.getType().equals("consumable")) {
            if (item.getStats().containsKey("health")) {
                hero.heal(item.getStats().get("health"));
                System.out.println("You healed for " + item.getStats().get("health") + " HP!");
            }
            if (item.getStats().containsKey("mana")) {
                hero.restoreMana(item.getStats().get("mana"));
                System.out.println("You restored " + item.getStats().get("mana") + " mana!");
            }
            hero.getInventory().removeItem(item);
        } else if (item.isEquippable()) {
            hero.getInventory().equipItem(item);
            System.out.println("You equipped " + item.getName() + "!");
        } else {
            System.out.println("Cannot use this item!");
        }
    }

    private Hero selectHero() {
        System.out.println("Choose your hero:");
        System.out.println("1. Wizard (magic)");
        System.out.println("2. Warrior (physical)");
        System.out.println("3. Archer (physical)");
        System.out.println("4. Assassin (physical)");
        int choice = -1;
        while (choice == -1) {
            try {
                choice = InputValidator.getIntInRange(scanner, 1, 4);
            } catch (InputValidationException e) {
                System.out.println(e.getMessage());
                System.out.print("Try again: ");
                scanner.nextLine();
            }
        }
        switch (choice) {
            case 1:
                return new Wizard();
            case 2:
                return new Warrior();
            case 3:
                return new Archer();
            case 4:
                return new Assassin();
            default:
                return new Wizard();
        }
    }

    public void saveGame() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SAVE_FILE))) {
            out.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static GameManager loadGame() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(SAVE_FILE))) {
            return (GameManager) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public String playerAttack() {
        int dmg = hero.attack();
        currentEnemy.takeDamage(dmg);
        return "You attack and deal " + dmg + " damage!";
    }

    public List<String> getHeroSkillNames() {
        return hero.getSkillManager().getAvailableSkills().stream().map(Skill::getName).toList();
    }

    public String playerUseSkill(String skillName) {
        var skills = hero.getSkillManager().getAvailableSkills();
        Skill skill = skills.stream().filter(s -> s.getName().equals(skillName)).findFirst().orElse(null);
        if (skill == null) {
            return "Skill not found!";
        }
        int manaCost = getSkillManaCost(skill.getId(), hero);
        if (!hero.getSkillManager().isSkillReady(skill.getId())) {
            return "Skill is on cooldown!";
        }
        if (hero.getMana() < manaCost) {
            return "Not enough mana!";
        }

        hero.consumeMana(manaCost);
        hero.getSkillManager().setSkillCooldown(skill.getId(), skill.getBaseCooldown());

        if (hero instanceof Wizard) {
            return executeWizardSkill(skill, currentEnemy);
        }
        if (hero instanceof Warrior) {
            return executeWarriorSkill(skill, currentEnemy);
        }
        if (hero instanceof Archer) {
            return executeArcherSkill(skill, currentEnemy);
        }
        if (hero instanceof Assassin) {
            return executeAssassinSkill(skill, currentEnemy);
        }
        return "Skill used!";
    }

    public List<String> getHeroItemNames() {
        return hero.getInventory().getItems().stream().map(Item::getName).toList();
    }

    public String playerUseItem(String itemName) {
        Item item = hero.getInventory().getItems().stream()
                .filter(i -> i.getName().equals(itemName)).findFirst().orElse(null);
        if (item == null) {
            return "Item not found!";
        }
        StringBuilder result = new StringBuilder("Used " + item.getName() + "!");
        if (item.getStats().containsKey("health")) {
            int heal = item.getStats().get("health");
            hero.heal(heal);
            result.append(" Restored ").append(heal).append(" HP.");
        }
        if (item.getStats().containsKey("mana")) {
            int mana = item.getStats().get("mana");
            hero.restoreMana(mana);
            result.append(" Restored ").append(mana).append(" Mana.");
        }
        if (item.getStats().containsKey("damage")) {
            int dmg = item.getStats().get("damage");
            if (currentEnemy != null && currentEnemy.isAlive()) {
                currentEnemy.takeDamage(dmg);
                result.append(" Dealt ").append(dmg).append(" damage to enemy.");
            }
        }
        //remove item after use if consumable
        if ("consumable".equals(item.getType())) {
            hero.getInventory().removeItem(item);
        }
        return result.toString();
    }

    public String playerDefend() {
        hero.setDefending(true);
        return "You brace for the next attack!";
    }

    public String getCurrentEnemyName() {
        return currentEnemy.getName();
    }

    public int getCurrentEnemyHp() {
        return currentEnemy.getHp();
    }

    public int getCurrentEnemyMaxHp() {
        return currentEnemy.getMaxHp();
    }

    public boolean[] getDungeonCompleted() {
        return dungeonCompleted;
    }

    public String enemyTurn() {
        if (!currentEnemy.isAlive()) {
            return "";
        }
        StringBuilder log = new StringBuilder();

        // Example: Boss uses a skill every 3 turns
        if (currentEnemy.isBoss() && currentEnemy.getTurnsSinceLastSkill() >= 3) {
            String skillResult = currentEnemy.useBossSkill(hero);
            log.append(" ").append(currentEnemy.getName()).append(" uses")
                    .append(currentEnemy.getLastSkillUsed()).append("! ").append(skillResult).append("\n");
            currentEnemy.resetSkillTurnCounter();
        } else {
            int dmg = currentEnemy.attack();
            if (hero.isDefending()) {
                dmg /= 2;
                log.append("You brace yourself and reduce the damage!\n");
                hero.setDefending(false);
            }
            hero.takeDamage(dmg);
            log.append(currentEnemy.getName()).append(" attacks for ").append(dmg).append(" damage.\n");
            currentEnemy.incrementSkillTurnCounter();
        }

        log.append(applyStatusEffects(hero, "You"));
        log.append(applyStatusEffects(currentEnemy, currentEnemy.getName()));

        return log.toString();
    }

    public String getHeroStatusEffects() {
        return hero.getStatusEffects().getEffectSummary();
    }

    public Player getHero() {
        return this.hero;
    }

    public Monster getCurrentEnemy() {
        return this.currentEnemy;
    }

    public String getEnemyStatusEffects() {
        Monster enemy = getCurrentEnemy();
        if (enemy != null && enemy.getStatusEffects() != null) {
            return enemy.getStatusEffects().getEffectSummary();
        }
        return "";
    }

    public boolean isBattleOver() {
        return getCurrentEnemy() == null || !getCurrentEnemy().isAlive() || !getHero().isAlive();
    }

    public DungeonLevel getCurrentDungeon() {
        return dungeons[currentDungeonIndex];
    }

    public void setCurrentDungeonIndex(int idx) {
        currentDungeonIndex = idx;
    }

    private String applyStatusEffects(Object entity, String name) {
        StringBuilder log = new StringBuilder();
        StatusEffectManager effects;
        if (entity instanceof Player) {
            effects = ((Player) entity).getStatusEffects();
        } else if (entity instanceof Monster) {
            effects = ((Monster) entity).getStatusEffects();
        } else {
            return "";
        }
        for (String effect : effects.activeEffects.keySet()) {
            int effectDmg = effects.getEffectValue(effect);
            if (effectDmg > 0) {
                if (entity instanceof Player) {
                    ((Player) entity).takeDamage(effectDmg);
                }
                if (entity instanceof Monster) {
                    ((Monster) entity).takeDamage(effectDmg);
                }
                log.append(name).append(" suffers").append(effectDmg).append(" damage from ")
                        .append(effect).append("</b>!\n");
            }
            if (effect.equals("stunned")) {
                log.append(name).append(" is stunned and cannot act!\n");
            }
            if (effect.equals("root")) {
                log.append(name).append(" is rooted and cannot move!\n");
            }
            if (effect.equals("burn")) {
                log.append(name).append(" is burned</b>!\n");
            }
        }
        effects.processTurn();
        return log.toString();
    }

    public int getHeroDPT() {
        int base = hero.getPhysical();
        base += hero.getBuffBonus();
        base -= hero.getDebuffPenalty();
        base += hero.getDotDamage();
        return Math.max(0, base);
    }

    public int getEnemyDPT() {
        int base = currentEnemy.getPhysical();
        base += currentEnemy.getBuffBonus();
        base -= currentEnemy.getDebuffPenalty();
        base += currentEnemy.getDotDamage();
        return Math.max(0, base);
    }
}
