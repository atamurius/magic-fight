package game.listeners;

import game.Action;
import game.Turn;

public interface GameActionListener {

    void onBeforeTurn(Turn turn);

    void onActionPerformed(Turn turn, Action action);

    void onAfterTurn(Turn turn);

    GameActionListener NOTHING = new GameActionListener() {
        @Override
        public void onBeforeTurn(Turn turn) { }

        @Override
        public void onActionPerformed(Turn turn, Action action) { }

        @Override
        public void onAfterTurn(Turn turn) { }
    };
}
