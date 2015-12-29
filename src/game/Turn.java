package game;

import java.util.List;

public class Turn {

    private int turn;
    private Character actor;
    private Character opponent;

    public Turn(Character actor, Character opponent) {
        this.actor = actor;
        this.opponent = opponent;
        this.turn = 1;
    }

    public List<Action> getPossibleActions() {
        return actor.getPossibleActions();
    }

    public void perform(Action action) {
        actor.beforeTurn(opponent);
        opponent.beforeTurn(actor);
        action.perform(actor, opponent);
    }

    public void next() {
        actor.afterTurn(opponent);
        opponent.afterTurn(actor);

        Character actor = opponent;
        this.opponent = this.actor;
        this.actor = actor;
        this.turn++;
    }

    public int getTurn() {
        return turn;
    }

    public boolean isEnded() {
        return ! (actor.isAlive() && opponent.isAlive());
    }

    public Character getWinner() {
        if (! isEnded())
            return null;
        else if (actor.isAlive())
            return actor;
        else
            return opponent;
    }

    public Character getActor() {
        return actor;
    }

    public Character getOpponent() {
        return opponent;
    }
}
