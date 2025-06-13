package com.mycompany.turnbasedgame;

import java.io.Serializable;
import java.util.*;


public class Player implements Serializable { //Serializable for the game save function

    protected String name;
    protected String heroType;
    protected int hp, maxHp, mana, maxMana, exp, level;
    protected int physical, magical, defense;
    protected boolean defending = false;
    protected boolean stunned = false;
    protected Inventory inventory;
    protected SkillManager skillManager;
    protected StatusEffectManager statusEffects;

    public Player() {
        this("Hero", "Hero");
    }

    public Player(String name, String heroType) {
        this.name = name;
        this.heroType = heroType;
        this.level = 1;
        this.exp = 0;
        this.maxHp = 200;
        this.hp = maxHp;
        this.maxMana = 100;
        this.mana = maxMana;
        this.physical = 20;
        this.magical = 15;
        this.defense = 10;
        this.inventory = new Inventory();
        this.skillManager = new SkillManager();
        this.statusEffects = new StatusEffectManager(this);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhysical(int val) {
        this.physical = val;
    }

    public void setMagical(int val) {
        this.magical = val;
    }

    public void setDefense(int val) {
        this.defense = val;
    }

    public void setMaxHp(int val) {
        this.maxHp = val;
    }

    public void setHp(int val) {
        this.hp = val;
    }

    public void setMaxMana(int val) {
        this.maxMana = val;
    }

    public void setMana(int val) {
        this.mana = val;
    }

    public int attack() {
        return physical + (int) (Math.random() * 6);
    }

    public int calculateMagicalDamage() {
        return magical + (int) (Math.random() * 8);
    }

    public void takeDamage(int dmg) {
        hp -= Math.max(0, dmg - defense);
        if (hp < 0) {
            hp = 0;
        }
    }

    public void heal(int amount) {
        hp = Math.min(maxHp, hp + amount);
    }

    public void restoreMana(int amount) {
        mana = Math.min(maxMana, mana + amount);
    }

    public boolean consumeMana(int amount) {
        if (mana < amount) {
            return false;
        }
        mana -= amount;
        return true;
    }

    public void gainExperience(int amount) {
        exp += amount;
        while (exp >= 100) {
            exp -= 100;
            levelUp();
        }
    }

    private void levelUp() {
        level++;
        maxHp += 20;
        maxMana += 10;
        physical += 3;
        magical += 2;
        defense += 1;
        hp = maxHp;
        mana = maxMana;
    }

    public void processTurn() {
        statusEffects.processTurn();
    }

    public void displayCharacterSheet() {
        System.out.println("Name: " + name + "  Level: " + level + "  EXP: " + exp + "/100");
        System.out.println("HP: " + hp + "/" + maxHp + "  Mana: " + mana + "/" + maxMana);
        System.out.println("Physical: " + physical + "  Magical: " + magical + "  Defense: " + defense);
    }

    public boolean isAlive() {
        return hp > 0;
    }

    public int getHp() {
        return hp;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getMana() {
        return mana;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public StatusEffectManager getStatusEffects() {
        return statusEffects;
    }

    public void setDefending(boolean d) {
        defending = d;
    }

    public boolean isDefending() {
        return defending;
    }

    public void setStunned(boolean s) {
        this.stunned = s;
    }

    public boolean isStunned() {
        return stunned;
    }

    public int getDefense() {
        return defense;
    }

    public int getPhysical() {
        return physical;
    }

    public int getMagical() {
        return magical;
    }

    public String getHeroType() {
        return heroType;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }
}

class Skill implements Serializable{

    private int id;
    private String name;
    private String description;
    private int baseCooldown;

    public Skill(int id, String name, String description, int baseCooldown) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.baseCooldown = baseCooldown;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getBaseCooldown() {
        return baseCooldown;
    }
}

class Inventory implements Serializable{

    private List<Item> items = new ArrayList<>();
    private Item equippedWeapon;
    private Item equippedArmor;

    public List<Item> getItems() {
        return items;
    }

    public void addItem(Item item) {
        items.add(item);
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    public void equipItem(Item item) {
        if (item.getType().equals("weapon")) {
            equippedWeapon = item;
        } else if (item.getType().equals("armor")) {
            equippedArmor = item;
        }
    }

    public Item getEquippedWeapon() {
        return equippedWeapon;
    }

    public Item getEquippedArmor() {
        return equippedArmor;
    }
}

class SkillManager implements Serializable{

    private List<Skill> availableSkills = new ArrayList<>();
    private Map<Integer, Integer> skillCooldowns = new HashMap<>();

    public List<Skill> getAvailableSkills() {
        return availableSkills;
    }

    public boolean isSkillReady(int skillId) {
        return !skillCooldowns.containsKey(skillId) || skillCooldowns.get(skillId) <= 0;
    }

    public int getSkillCooldown(int skillId) {
        return skillCooldowns.getOrDefault(skillId, 0);
    }

    public void setSkillCooldown(int skillId, int cooldown) {
        skillCooldowns.put(skillId, cooldown);
    }

    public void reduceCooldowns() {
        for (Integer id : skillCooldowns.keySet()) {
            if (skillCooldowns.get(id) > 0) {
                skillCooldowns.put(id, skillCooldowns.get(id) - 1);
            }
        }
    }
}

class StatusEffect implements Serializable{
    private String type;
    private int duration;
    private int value;

    public StatusEffect(String type, int duration, int value) {
        this.type = type;
        this.duration = duration;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public int getDuration() {
        return duration;
    }

    public int getValue() {
        return value;
    }

    public void reduceDuration() {
        duration--;
    }
}

class StatusEffectManager implements Serializable {
    public Map<String, StatusEffect> activeEffects = new HashMap<>();
    private Object owner;

    public StatusEffectManager(Object owner) {
        this.owner = owner;
    }

    public void addEffect(String type, int duration, int value) {
        activeEffects.put(type, new StatusEffect(type, duration, value));
    }

    public boolean hasEffect(String effectName) {
        return activeEffects.containsKey(effectName);
    }

    public void processTurn() {
        Iterator<Map.Entry<String, StatusEffect>> it = activeEffects.entrySet().iterator();
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
                    if (owner instanceof Player) {
                        Player p = (Player) owner;
                        p.takeDamage(se.getValue());
                        System.out.println(p.name + " suffers " + se.getValue() + " damage from " + effect + "!");
                    } else if (owner instanceof Monster) {
                        Monster m = (Monster) owner;
                        m.takeDamage(se.getValue());
                        System.out.println(m.getName() + " suffers " + se.getValue() + " damage from " + effect + "!");
                    }
                    break;
                case "marked":
                    if (se.getDuration() == 1) {
                        if (owner instanceof Player) {
                            Player p = (Player) owner;
                            int dmg = p.getMaxHp() / 2;
                            if (p.getHp() < p.getMaxHp() / 2) {
                                dmg += p.getMaxHp() / 4;
                            }
                            p.takeDamage(dmg);
                            System.out.println(p.name + " is struck by Deadeye's Mark for " + dmg + " damage!");
                        } else if (owner instanceof Monster) {
                            Monster m = (Monster) owner;
                            int dmg = m.getMaxHp() / 2;
                            if (m.getHp() < m.getMaxHp() / 2) {
                                dmg += m.getMaxHp() / 4;
                            }
                            m.takeDamage(dmg);
                            System.out.println(m.getName() + " is struck by Deadeye's Mark for " + dmg + " damage!");
                        }
                    }
                    break;
                case "venom_blades":
                    break;
            }

            se.reduceDuration();
            if (se.getDuration() <= 0) {
                it.remove();
            }
        }

    }

    public String getEffectSummary() {
        if (activeEffects.isEmpty())
            return "None";
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, StatusEffect> entry : activeEffects.entrySet()) {
            sb.append(entry.getKey());
            if (entry.getValue().getDuration() > 1)
                sb.append(" (").append(entry.getValue().getDuration()).append(")");
            sb.append(", ");
        }
        if (sb.length() > 2)
            sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    public int getEffectValue(String effectName) {
        StatusEffect se = activeEffects.get(effectName);
        return se != null ? se.getValue() : 0;
    }
}
