package com.mycompany.turnbasedgame;

import java.util.Random;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Iterator;

public class Monster implements Serializable{

    private String name;
    private int hp, maxHp, attack, physical, magical;
    private StatusEffectManager statusEffects;
    private BossMonster.BossSkillSet bossSkillSet = null;
    private boolean isStunned = false;
    protected boolean boss;
    protected int turnsSinceLastSkill = 0;
    protected String lastSkillUsed = "";

    public Monster(String name, int hp, int mana, int attack, int physical, int magical) {
        this.name = name;
        this.maxHp = hp;
        this.hp = hp;
        this.attack = attack;
        this.physical = physical;
        this.magical = magical;
        this.statusEffects = new StatusEffectManager(this);
    }

    public int attack() {
        return attack + new Random().nextInt(6);
    }

    public void takeDamage(int dmg) {
        hp -= dmg;
        if (hp < 0) {
            hp = 0;
        }
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    public void processTurn() {
        Iterator<Map.Entry<String, StatusEffect>> it = statusEffects.activeEffects.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, StatusEffect> entry = it.next();
            String effect = entry.getKey();
            StatusEffect se = entry.getValue();

            switch (effect) {
                case "bleed":
                case "poison":
                case "fireball_dot":
                case "burn":
                case "cold":
                    this.takeDamage(se.getValue());
                    System.out.println(name + " suffers " + se.getValue() + " damage from " + effect + "!");
                    break;
                case "marked":
                    if (se.getDuration() == 1) { // about to expire
                        int dmg = this.getMaxHealth() / 2;
                        if (this.getHealth() < this.getMaxHealth() / 2) {
                            dmg += this.getMaxHealth() / 4; // bonus if <50% HP
                        }
                        this.takeDamage(dmg);
                        System.out.println(name + " is struck by Deadeye's Mark for " + dmg + " damage!");
                    }
                    break;
                case "venom_blades":
                    //handled on attack just reduce duration
                    break;
            }

            se.reduceDuration();
            if (se.getDuration() <= 0) {
                it.remove();
            }
        }
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public int getHealth() {
        return hp;
    }

    public int getMaxHealth() {
        return maxHp;
    }

    public int getAttack() {
        return attack;
    }

    public String getName() {
        return name;
    }

    public StatusEffectManager getStatusEffects() {
        return statusEffects;
    }

    public void setBossSkillSet(BossMonster.BossSkillSet set) {
        this.bossSkillSet = set;
    }

    public BossMonster.BossSkillSet getBossSkillSet() {
        return bossSkillSet;
    }

    public int getDefense() {
        return physical;
    }

    public boolean isBoss() {
        return boss;
    }

    public int getTurnsSinceLastSkill() {
        return turnsSinceLastSkill;
    }

    public void incrementSkillTurnCounter() {
        turnsSinceLastSkill++;
    }

    public void resetSkillTurnCounter() {
        turnsSinceLastSkill = 0;
    }

    public String useBossSkill(Player hero) {
        int dmg = Math.max(20, hero.getMaxHp() / 5); //20 or 20% of hero HP
        hero.getStatusEffects().addEffect("stunned", 1, 0);
        hero.takeDamage(dmg);
        lastSkillUsed = "Bone Smash";
        return "Stuns you for 1 turn and deals " + dmg + " damage!";
    }

    public String getLastSkillUsed() {
        return lastSkillUsed;
    }

    public int getPhysical() {
        return physical;
    }

    public int getMagical() {
        return magical;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getExpReward() {
        return 40 + getMaxHp() / 8;
    }

    public int getBuffBonus() {
        return 0;
    }

    public int getDebuffPenalty() {
        return 0;
    }

    public int getDotDamage() {
        return 0;
    }

    public void setStunned(boolean stunned) {
        this.isStunned = stunned;
    }

    public boolean isStunned() {
        return isStunned;
    }
}

class BossMonster extends Monster implements Serializable{

    public static class BossSkill implements Serializable{

        private final String name;
        private final String description;
        private final int baseDamage;
        private final int cooldown;
        private int currentCooldown = 0;

        public BossSkill(String name, String description, int baseDamage, int cooldown) {
            this.name = name;
            this.description = description;
            this.baseDamage = baseDamage;
            this.cooldown = cooldown;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public int getBaseDamage() {
            return baseDamage;
        }

        public int getExpReward() {
            return (int) (baseDamage * 0.1); //10% of base damage as exp
        }

        public int getCooldown() {
            return cooldown;
        }

        public int getCurrentCooldown() {
            return currentCooldown;
        }

        public void setCurrentCooldown(int cd) {
            this.currentCooldown = cd;
        }

        public void reduceCooldown() {
            if (currentCooldown > 0) {
                currentCooldown--;
            }
        }

        public boolean isReady() {
            return currentCooldown == 0;
        }

        public void use() {
            this.currentCooldown = cooldown;
        }
    }

    public static class BossSkillSet implements Serializable{

        private final List<BossSkill> skills = new ArrayList<>();

        public BossSkillSet(BossSkill... skills) {
            this.skills.addAll(Arrays.asList(skills));
        }

        public List<BossSkill> getSkills() {
            return skills;
        }

        public void reduceAllCooldowns() {
            for (BossSkill skill : skills) {
                skill.reduceCooldown();
            }
        }

        public BossSkill getReadySkill() {
            for (BossSkill skill : skills) {
                if (skill.isReady()) {
                    return skill;
                }
            }
            return null;
        }
    }

    private BossSkillSet bossSkillSet;

    public BossMonster(String name, int hp, int mana, int attack, int physical, int magical, BossSkillSet skillSet) {
        super(name, hp, mana, attack, physical, magical);
        this.bossSkillSet = skillSet;
    }

    public BossSkillSet getBossSkillSet() {
        return bossSkillSet;
    }

    public void setBossSkillSet(BossSkillSet set) {
        this.bossSkillSet = set;
    }

    //factory method for themed bosses with skills, balanced damage and cooldowns
    public static BossMonster createBoss(String name, int level, String theme) {
        BossSkillSet skillSet;
        if (theme.equalsIgnoreCase("undead")) {
            skillSet = new BossSkillSet(
                    new BossSkill("Soul Drain",
                            "Steals life from the target, healing the Lich King for half the damage dealt.",
                            30 + level * 4, 3),
                    new BossSkill("Wail of the Damned", "Reduces all enemies’ attack power for 2 turns.", 0, 4),
                    new BossSkill("Raise Undead",
                            "Summons skeletal minions to absorb damage or deal light attacks for 3 turns.",
                            12 + level * 2, 5));
            return new BossMonster("Lich King", 400 + level * 50, 120, 25 + level * 2, 25 + level * 3,
                    45 + level * 5, skillSet);
        } else if (theme.equalsIgnoreCase("dragon")) {
            skillSet = new BossSkillSet(
                    new BossSkill("Inferno Breath", "Deals massive fire damage and burns the target for 2 turns.",
                            45 + level * 6, 4),
                    new BossSkill("Wing Tempest", "Knocks back all players and may stun them.", 25 + level * 3, 5),
                    new BossSkill("Molten Scales", "Increases defense and reflects melee damage for 2 turns.", 0, 6));
            return new BossMonster("Ancient Dragon", 480 + level * 60, 140, 30 + level * 2, 32 + level * 4,
                    38 + level * 4, skillSet);
        } else if (theme.equalsIgnoreCase("forest")) {
            skillSet = new BossSkillSet(
                    new BossSkill("Roots of Binding", "Immobilizes a target and reduces their dodge for 2 turns.", 0,
                            4),
                    new BossSkill("Nature’s Resilience",
                            "Heals a portion of the Treant’s health and grants resistance for 2 turns.", 0, 5),
                    new BossSkill("Thorn Burst", "Damages all enemies and may cause bleed.", 22 + level * 3, 4));
            return new BossMonster("Elder Treant", 420 + level * 55, 110, 35+ level * 2, 30 + level * 4,
                    40 + level * 6, skillSet);
        } else if (theme.equalsIgnoreCase("ice")) {
            skillSet = new BossSkillSet(
                    new BossSkill("Glacial Slam", "Powerful melee attack that can freeze the target.", 38 + level * 5,
                            3),
                    new BossSkill("Frozen Aura", "Reduces enemy speed and deals cold damage for 2 turns.",
                            10 + level * 2, 5),
                    new BossSkill("Ice Spikes", "Pierces armor and slows enemies.", 28 + level * 4, 4),
                    new BossSkill("Cryo Overload", "Blizzard that deals AoE damage over 2 turns, can freeze.",
                            20 + level * 3, 6));
            return new BossMonster("Frost Titan", 430 + level * 52, 130, 40 + level * 2, 28 + level * 4,
                    50 + level * 7, skillSet);
        } else if (theme.equalsIgnoreCase("volcanic")) {
            skillSet = new BossSkillSet(
                    new BossSkill("Lava Eruption", "Damages all enemies and leaves burning zones.", 40 + level * 5, 4),
                    new BossSkill("Molten Charge", "High-speed dash, massive damage in a line.", 50 + level * 7, 6),
                    new BossSkill("Crackling Fury", "Rage state: more damage/attack speed, less defense for 2 turns.",
                            0, 6),
                    new BossSkill("Smoldering Earth", "Reduces healing and burns anyone who stands still.",
                            12 + level * 2, 5));
            return new BossMonster("Volcanic Behemoth", 470 + level * 58, 125, 40 + level * 3, 35 + level * 5,
                    42 + level * 5, skillSet);
        } else if (theme.equalsIgnoreCase("abyss")) {
            skillSet = new BossSkillSet(
                    new BossSkill("Void Lash", "Dark energy bypasses armor, chance to silence.", 36 + level * 5, 3),
                    new BossSkill("Abyssal Grasp", "Tentacles restrain all enemies, lowering resistances.",
                            18 + level * 3, 5),
                    new BossSkill("Reality Fracture", "Random effects: teleports, reversed controls, etc.", 0, 7),
                    new BossSkill("Endless Hunger", "Absorbs souls to restore health and power.", 0, 6));
            return new BossMonster("Abyssal Overlord", 500 + level * 65, 150, 45 + level * 4, 38 + level * 6,
                    55 + level * 8, skillSet);
        }
        // default boss
        skillSet = new BossSkillSet(
                new BossSkill("Overlord's Strike", "A devastating attack that tests the strength of any challenger.",
                        40 + level * 5, 4));
        return new BossMonster("Dungeon Overlord", 400 + level * 40, 100, 30 + level * 2, 25 + level * 3,
                35 + level * 4, skillSet);
    }

    public boolean isBoss() {
        return boss;
    }

    public int getTurnsSinceLastSkill() {
        return turnsSinceLastSkill;
    }

    public void incrementSkillTurnCounter() {
        turnsSinceLastSkill++;
    }

    public void resetSkillTurnCounter() {
        turnsSinceLastSkill = 0;
    }

    public String getLastSkillUsed() {
        return lastSkillUsed;
    }

    public int getBuffBonus() {
        int bonus = 0;
        if (getStatusEffects().hasEffect("rage"))
            bonus += 10;
        if (getStatusEffects().hasEffect("defense_up"))
            bonus += 8;
        return bonus;
    }

    public int getDebuffPenalty() {
        int penalty = 0;
        if (getStatusEffects().hasEffect("weakened"))
            penalty += 4;
        if (getStatusEffects().hasEffect("defense_down"))
            penalty += 8;
        return penalty;
    }

    public int getDotDamage() {
        int dot = 0;
        for (String effect : new String[] { "poison", "burn", "bleed", "cold" }) {
            dot += getStatusEffects().getEffectValue(effect);
        }
        return dot;
    }

    public String useBossSkill(Player hero) {
        hero.getStatusEffects().addEffect("stunned", 1, 0);
        hero.takeDamage(30);
        lastSkillUsed = "Bone Smash";
        return "Stuns you for 1 turn and deals 30 damage!";
    }
}
