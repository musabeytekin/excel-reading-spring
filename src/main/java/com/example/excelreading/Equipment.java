package com.example.excelreading;

public enum Equipment {
    MANUAL_WHEELCHAIR("Manual Tekerlekli Sandalye", 1.5),
    ELECTRIC_WHEELCHAIR("Akülü Sandalye", 2.0),
    GOLF_CART("Golf Aracı", 5.0),
    AMBULIFT("Ambulift (Medcar)", 10.0);

    private final String name;
    private final double speed;

    Equipment(String name, double speed) {
        this.name = name;
        this.speed = speed;
    }

    public String getName() {
        return name;
    }

    public double getSpeed() {
        return speed;
    }
}