package game.wizard.effects;

import game.wizard.Effect;
import game.wizard.Wizard;

public class Shield extends Effect {

    private final int armor;

    public Shield(int cost, int turns, int armor) {
        super(cost, turns);
        this.armor = armor;
    }

    @Override
    public void start(Wizard actor, game.Character opponent) {
        actor.setArmor(actor.getArmor() + armor);
    }

    @Override
    public void stop(Wizard actor, game.Character opponent) {
        actor.setArmor(actor.getArmor() - armor);
    }

    public int getArmor() {
        return armor;
    }
}
