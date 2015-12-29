package game;

import java.util.List;

public abstract class Character {
    protected int armor;
    protected int health;

    public Character(int health, int armor) {
        this.health = health;
        this.armor = armor;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void hit(int damage) {
        this.health -= damage;
    }

    public int getArmor() {
        return armor;
    }

    public abstract List<Action> getPossibleActions();

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    public void heal(int amount) {
        health += amount;
    }

    public void beforeTurn(Character opponent) { }

    public void afterTurn(Character opponent) { }

    public int getHealth() {
        return health;
    }
}
