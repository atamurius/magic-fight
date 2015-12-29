package game.wizard.spells;

import game.wizard.Spell;

public class MagicMissile extends Spell {

    private final int damage;

    public MagicMissile(int cost, int damage) {
        super(cost);
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }

    @Override
    protected void performOnOpponent(game.Character opponent) {
        opponent.hit(damage);
    }
}
