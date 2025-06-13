package com.mycompany.turnbasedgame;

import java.io.Serializable;
import java.util.*;

public abstract class Hero extends Player implements Serializable{
    public Hero() {
        super();
    }

    public abstract void initializeSkills();

    public abstract String getHeroType();

    public int getBuffBonus() {
        int bonus = 0;
        if (getStatusEffects().hasEffect("rage"))
            bonus += 10;
        if (getStatusEffects().hasEffect("berserk"))
            bonus += 15;
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
}

class Wizard extends Hero implements Serializable{
    public Wizard() {
        super();
        this.setName("Wizard");
        this.setPhysical(10);
        this.setMagical(30);
        this.setDefense(10);
        this.setMaxHp(160);
        this.setHp(160);
        this.setMaxMana(160);
        this.setMana(160);
    }

    @Override
    public void initializeSkills() {
        SkillManager sm = this.getSkillManager();
        sm.getAvailableSkills().clear();
        sm.getAvailableSkills().add(new Skill(1, "Spellchain", "Next two spells are empowered", 5));
        sm.getAvailableSkills().add(new Skill(2, "Sparkweave", "Next attack deals bonus magic damage", 4));
        sm.getAvailableSkills().add(new Skill(3, "Fireball", "Deals damage over 3 turns", 6));
        sm.getAvailableSkills().add(new Skill(4, "Arcane Reflux", "Absorb and reflect attacks in 3 turns", 10));
    }

    @Override
    public String getHeroType() {
        return "Wizard";
    }
}

class Warrior extends Hero implements Serializable{
    public Warrior() {
        super();
        this.setName("Warrior");
        this.setPhysical(28);
        this.setMagical(8);
        this.setDefense(18);
        this.setMaxHp(240);
        this.setHp(240);
        this.setMaxMana(90);
        this.setMana(90);
    }

    @Override
    public void initializeSkills() {
        SkillManager sm = this.getSkillManager();
        sm.getAvailableSkills().clear();
        sm.getAvailableSkills().add(new Skill(1, "Bullrush", "Charge, stun and deal bonus damage", 5));
        sm.getAvailableSkills().add(new Skill(2, "Iron Guard", "Reduce and reflect damage next turn", 4));
        sm.getAvailableSkills().add(new Skill(3, "Piercing Sword", "Deals damage over 3 turns", 6));
        sm.getAvailableSkills().add(new Skill(4, "Wrath Unbound", "Berserker state for 3 turns", 10));
    }

    @Override
    public String getHeroType() {
        return "Warrior";
    }
}

class Archer extends Hero implements Serializable{
    public Archer() {
        super();
        this.setName("Archer");
        this.setPhysical(26);
        this.setMagical(10);
        this.setDefense(12);
        this.setMaxHp(200);
        this.setHp(200);
        this.setMaxMana(110);
        this.setMana(110);
    }

    @Override
    public void initializeSkills() {
        SkillManager sm = this.getSkillManager();
        sm.getAvailableSkills().clear();
        sm.getAvailableSkills().add(new Skill(1, "Piercing Arrow", "Ignores armor, +30% crit chance", 5));
        sm.getAvailableSkills().add(new Skill(2, "Entrapment Field", "Roots/stuns enemy for 3 turns", 7));
        sm.getAvailableSkills().add(new Skill(3, "Flurry of Thorns", "Barrage of arrows, stacking bleed", 6));
        sm.getAvailableSkills().add(
                new Skill(4, "Deadeye's Mark", "After 2 turns, deals massive damage (bonus if target <50% HP)", 10));
    }

    @Override
    public String getHeroType() {
        return "Archer";
    }
}

class Assassin extends Hero implements Serializable{
    public Assassin() {
        super();
        this.setName("Assassin");
        this.setPhysical(28);
        this.setMagical(8);
        this.setDefense(10);
        this.setMaxHp(180);
        this.setHp(180);
        this.setMaxMana(120);
        this.setMana(120);
    }

    @Override
    public void initializeSkills() {
        SkillManager sm = this.getSkillManager();
        sm.getAvailableSkills().clear();
        sm.getAvailableSkills()
                .add(new Skill(1, "Shadow Step", "20% chance to evade next attack (CD: 4, Mana: 10)", 4));
        sm.getAvailableSkills()
                .add(new Skill(2, "Venom Blades", "Next 3 attacks apply poison (3 turns, CD: 7, Mana: 16)", 7));
        sm.getAvailableSkills()
                .add(new Skill(3, "Heartpiercer", "Bonus damage if target is poisoned/slowed (CD: 5, Mana: 14)", 5));
        sm.getAvailableSkills()
                .add(new Skill(4, "Death Lotus", "Become untargetable for 4 turns (CD: 12, Mana: 32)", 12));
    }

    @Override
    public String getHeroType() {
        return "Assassin";

    }
}
