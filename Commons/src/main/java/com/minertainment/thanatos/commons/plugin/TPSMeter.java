package com.minertainment.thanatos.commons.plugin;

public enum TPSMeter {

    HIGHEST(999, 18, 3),
    HIGH(17, 15, 2),
    MEDIUM(14, 11, 1),
    LOW(0, 10, 0);

    private int upper, lower, weight;

    TPSMeter(int upper, int lower, int weight) {
        this.upper = upper;
        this.lower = lower;
        this.weight = weight;
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

    public boolean isHigherThan(TPSMeter meter) {
        return (getWeight() > meter.getWeight());
    }

    public static TPSMeter fromTPS(double tps) {
        for(TPSMeter meter : TPSMeter.values()) {
            if(meter.getUpper() > tps && meter.getLower() < tps) {
                return meter;
            }
        }
        return null;
    }

}