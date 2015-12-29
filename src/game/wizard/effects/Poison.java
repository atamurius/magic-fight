package game.wizard.effects;

import game.Character;
import game.wizard.Effect;
import game.wizard.Wizard;

public class Poison extends Effect {

    private final int damage;

    public Poison(int cost, int turns, int damage) {
        super(cost, turns);
        this.damage = damage;
    }

    @Override
    public void turn(Wizard actor, Character opponent) {
        opponent.hit(damage);
    }

    public int getDamage() {
        return damage;
    }
}
