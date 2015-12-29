package game;

import game.listeners.ConsoleGame;
import game.listeners.GameActionListener;
import game.warrior.Warrior;
import game.wizard.effects.Recharge;
import game.wizard.spells.Drain;
import game.wizard.spells.MagicMissile;
import game.wizard.effects.Poison;
import game.wizard.effects.Shield;
import game.wizard.Wizard;

public class Game {

    public static void main(String[] args) {
        Wizard wizard = new Wizard(20, 5);
        wizard.learn(new Recharge(1, 3, 3));
        wizard.learn(new Poison(1, 3, 3));
        wizard.learn(new Shield(1, 3, 3));
        wizard.learn(new Drain(3, 5));
        wizard.learn(new MagicMissile(3, 5));

        play(new Turn(wizard, new Warrior(40, 10, 3)), new ConsoleGame());
    }

    public static void play(Turn fight, GameActionListener listener) {
        while (! fight.isEnded()) {
            listener.onBeforeTurn(fight);
            Action action = fight.getPossibleActions().get(0);
            listener.onActionPerformed(fight, action);
            fight.perform(action);
            listener.onAfterTurn(fight);
            fight.next();
        }
        listener.onBeforeTurn(fight);
        System.out.printf("%s wins in %d turns%n", fight.getWinner(), fight.getTurn() - 1);
    }
}
