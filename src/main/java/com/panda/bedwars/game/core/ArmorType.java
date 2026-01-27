package com.panda.bedwars.game.core;

public enum ArmorType {
    DIAMOND(3),
    IRON(2),
    CHAINMAIL(1),
    LEATHER(0);

    int tier;

    ArmorType(int tier) {
        this.tier = tier;
    }

    public int compare(ArmorType other) {
        return tier - other.tier;
    }
}
