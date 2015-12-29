package game.wizard.spells;

import game.wizard.Spell;
import game.wizard.Wizard;

public class Drain extends Spell {

    private final int amount;

    public Drain(int cost, int amount) {
        super(cost);
        this.amount = amount;
    }

    @Override
    protected void performOnSelf(Wizard wizard) {
        wizard.heal(amount);
    }

    @Override
    protected void performOnOpponent(game.Character opponent) {
        opponent.hit(amount);
    }

    public int getAmount() {
        return amount;
    }
}
