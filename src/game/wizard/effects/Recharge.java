package game.wizard.effects;

import game.wizard.Effect;
import game.wizard.Wizard;

public class Recharge extends Effect {

    private final int amount;

    public Recharge(int cost, int turns, int amount) {
        super(cost, turns);
        this.amount = amount;
    }

    @Override
    public void turn(Wizard actor, game.Character opponent) {
        actor.setMana(actor.getMana() + amount);
    }

    public int getAmount() {
        return amount;
    }
}
