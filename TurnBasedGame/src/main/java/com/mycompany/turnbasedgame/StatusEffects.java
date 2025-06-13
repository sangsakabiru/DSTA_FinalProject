package com.mycompany.turnbasedgame;

import java.io.Serializable;
import java.util.*;

public class StatusEffects implements Serializable {
    public static class EffectData implements Serializable {
        public int duration;
        public int value;

        public EffectData(int duration, int value) {
            this.duration = duration;
            this.value = value;
        }
    }

    public Map<String, EffectData> activeEffects = new HashMap<>();

    public void addEffect(String name, int duration, int value) {
        activeEffects.put(name, new EffectData(duration, value));
    }

    public boolean hasEffect(String name) {
        return activeEffects.containsKey(name);
    }

    public int getEffectDamage(String name) {
        EffectData data = activeEffects.get(name);
        if (data == null)
            return 0;
        if (name.equals("poison") || name.equals("burn") || name.equals("bleed") || name.equals("cold")) {
            return data.value;
        }
        return 0;
    }

    public void reduceAll() {
        List<String> toRemove = new ArrayList<>();
        for (var entry : activeEffects.entrySet()) {
            entry.getValue().duration--;
            if (entry.getValue().duration <= 0)
                toRemove.add(entry.getKey());
        }
        for (String key : toRemove)
            activeEffects.remove(key);
    }

    public Set<String> getActiveEffects() {
        return activeEffects.keySet();
    }

    public String getEffectSummary() {
        if (activeEffects == null || activeEffects.isEmpty())
            return "None";
        StringBuilder sb = new StringBuilder();
        for (java.util.Map.Entry<String, EffectData> entry : activeEffects.entrySet()) {
            sb.append(entry.getKey());
            if (entry.getValue().duration > 1)
                sb.append(" (").append(entry.getValue().duration).append(")");
            sb.append(", ");
        }
        if (sb.length() > 2)
            sb.setLength(sb.length() - 2);
        return sb.toString();
    }
}