package org.example.Enums;

public enum CardType {

    DAGGER("Weapon", 5, "D5"),
    HORSE("Weapon", 10, "H10"),
    SWORD("Weapon", 10, "S10"),
    BATTLE_AXE("Weapon", 15, "B15"),
    LANCE("Weapon", 20, "L20"),
    EXCALIBUR("Weapon", 30, "E30"),

    F5("Foe", 5, "F5"),
    F10("Foe", 10, "F10"),
    F15("Foe", 15, "F15"),
    F20("Foe", 20, "F20"),
    F25("Foe", 25, "F25"),
    F30("Foe", 30, "F30"),
    F35("Foe", 35, "F35"),
    F40("Foe", 40, "F40"),
    F50("Foe", 50, "F50"),
    F70("Foe", 70, "F70"),

    Q2("Quest", 2, "Q2"),
    Q3("Quest", 3, "Q3"),
    Q4("Quest", 4, "Q4"),
    Q5("Quest", 5, "Q5"),

    PLAGUE("Event", "PLAGUE"),
    QUEENS_FAVOR("Event", "QUEEN'S FAVOR"),
    PROSPERITY("Event", "PROSPERITY");

    private final String type;
    private final int value;
    private String name;

    CardType(String type, int value, String name) {
        this.type = type;
        this.value = value;
        this.name = name;
    }

    CardType(String category, String name) {
        this.type = category;
        this.value = 0;
        this.name = name;
    }

    public String getName() { return name; }

    public int getValue() {
        return value;
    }

    public boolean isWeapon() {
        return type.equals("Weapon");
    }

    public boolean isFoe() {
        return type.equals("Foe");
    }

    public boolean isQuest() {
        return type.equals("Quest");
    }

    public boolean isEvent() {
        return type.equals("Event");
    }
}


