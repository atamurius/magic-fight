package game.warrior;

import game.*;
import game.Character;

public class Attack implements Action {

    private final int damage;

    public Attack(int damage) {
        this.damage = damage;
    }

    public int getDamageFor(game.Character opponent) {
        int damage = this.damage - opponent.getArmor();
        return damage < 1 ? 1 : damage;
    }

    @Override
    public void perform(Character actor, Character opponent) {
        opponent.hit(getDamageFor(opponent));
    }

    public int getDamage() {
        return damage;
    }
}
