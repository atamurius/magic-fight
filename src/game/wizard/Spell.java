package game.wizard;

import game.*;
import game.Character;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class Spell implements Action {

    private final int cost;

    public Spell(int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }

    @Override
    public final void perform(Character actor, Character opponent) {
        Wizard self = (Wizard) actor;
        self.setMana(self.getMana() - cost);
        performOnSelf(self);
        performOnOpponent(opponent);
    }

    protected void performOnSelf(Wizard wizard) { }
    
    protected void performOnOpponent(Character opponent) { }
}