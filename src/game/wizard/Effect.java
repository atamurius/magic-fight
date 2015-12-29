package game.wizard;

import game.Character;

public abstract class Effect extends Spell {

    private final int turns;

    public Effect(int cost, int turns) {
        super(cost);
        this.turns = turns;
    }

    @Override
    protected void performOnSelf(Wizard wizard) {
        wizard.start(this);
    }

    public void start(Wizard actor, Character opponent) { }

    public void stop(Wizard actor, Character opponent) { }

    public void turn(Wizard actor, Character opponent) { }

    public int getTurns() {
        return turns;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
