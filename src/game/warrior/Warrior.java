package game.warrior;

import game.*;

import java.util.Collections;
import java.util.List;

public class Warrior extends game.Character {

    private final List<Action> actions;

    public Warrior(int health, int damage, int armor) {
        super(health, armor);
        this.actions = Collections.<Action>singletonList(new Attack(damage));
    }

    @Override
    public List<Action> getPossibleActions() {
        return actions;
    }
}
