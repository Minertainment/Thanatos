package com.minertainment.thanatos.commons.plugin;

import net.md_5.bungee.api.ChatColor;

public enum TPSMeter {

    HIGHEST(999, 18, 4, ChatColor.GREEN),
    HIGH(17, 15, 3, ChatColor.YELLOW),
    MEDIUM(14, 11, 2, ChatColor.GOLD),
    LOW(10, 0, 1, ChatColor.RED),
    ERR(0, -999, 0, ChatColor.BLACK);

    private int upper, lower, weight;
    private ChatColor color;

    TPSMeter(int upper, int lower, int weight, ChatColor color) {
        this.upper = upper;
        this.lower = lower;
        this.weight = weight;
        this.color = color;
    }

    public int getUpper() {
        return upper;
    }

    public int getLower() {
        return lower;
    }

    public int getWeight() {
        return weight;
    }

    public ChatColor getColor() {
        return color;
    }

    public boolean isHigherThan(TPSMeter meter) {
        return (getWeight() > meter.getWeight());
    }

    public static TPSMeter fromTPS(double tps) {
        for(TPSMeter meter : TPSMeter.values()) {
            if(meter.getUpper() > tps && meter.getLower() < tps) {
                return meter;
            }
        }
        return ERR;
    }

}