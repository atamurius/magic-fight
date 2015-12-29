package game.listeners;

import game.*;
import game.Character;
import game.warrior.Attack;
import game.wizard.Effect;
import game.wizard.Wizard;
import game.wizard.effects.Poison;
import game.wizard.effects.Recharge;
import game.wizard.effects.Shield;
import game.wizard.spells.Drain;
import game.wizard.spells.MagicMissile;

import static game.utils.TypeSwitch.*;

import static java.lang.System.out;

@SuppressWarnings("unused")
public class ConsoleGame implements GameActionListener {

    @Override
    public void onBeforeTurn(Turn turn) {
        out.printf("%nTurn %d:%n", turn.getTurn());
        describe(turn.getActor());
        describe(turn.getOpponent());
        describeEffects(turn.getActor(), turn.getOpponent());
        describeEffects(turn.getOpponent(), turn.getActor());
    }

    private void describeEffects(Character actor, Character opponent) {
        match(actor,
            of (Wizard.class, self -> {
                self.getEffects().forEach((effect, timeLeft) ->
                        describe(effect, self, opponent, timeLeft));
                self.getEffects().keySet().forEach(e -> describeEffectAction(self, opponent, e));
            })
        );
    }

    private void describeEffectAction(Character actor, Character opponent, Effect effect) {
        match(effect,
            of (Poison.class, self ->
                out.printf("> %s is poisoned by %d points%n",
                        opponent, self.getDamage())
            ),
            of (Recharge.class, self ->
                out.printf("> %s recharges %d mana%n",
                        actor, self.getAmount())
            ),
            other (obj -> {
                throw new IllegalArgumentException("Unknown effect " + effect);
            })
        );
    }

    private void describe(Character actor) {
        match(actor,
            of (Wizard.class, self ->
                out.printf("%s has %d hp, %d mana and %d armor%n",
                        actor,
                        self.getHealth(),
                        self.getMana(),
                        self.getArmor())
            ),
            other (obj ->
                out.printf("%s has %d hp and %d armor%n",
                        actor,
                        actor.getHealth(),
                        actor.getArmor())
            )
        );
    }

    private void describe(Effect effect, Wizard actor, Character opponent, int timeLeft) {
        match(effect,
            of (Poison.class, self ->
                out.printf("~Poison with %d damage is active on %s",
                        self.getDamage(),
                        opponent)
            ),
            of (Recharge.class, self ->
                out.printf("~Recharge +%d mana is active on %s",
                        self.getAmount(),
                        actor)
            ),
            of (Shield.class, self ->
                out.printf("~Shield +%d is active on %s",
                        self.getArmor(),
                        actor)
            ),
            other (obj -> {
                throw new IllegalArgumentException("Unknown effect " + effect);
            })
        );
        out.printf(" for %d turn%s%n", timeLeft, timeLeft > 1 ? "s" : "");
    }

    @Override
    public void onActionPerformed(Turn turn, Action action) {
        Character actor = turn.getActor();
        Character opponent = turn.getOpponent();
        match(action,
            of (Attack.class, self ->
                out.printf("> %s hits %s with %d points and causes %d damage%n",
                        actor,
                        opponent,
                        self.getDamage(),
                        self.getDamageFor(opponent))
            ),
            of (MagicMissile.class, self ->
                out.printf("> %s casts Magic Missile on %s and causes %d damage%n",
                        actor,
                        opponent,
                        self.getDamage())
            ),
            of (Drain.class, self ->
                out.printf("> %s casts Drain and takes %d hp from %s%n",
                        actor,
                        self.getAmount(),
                        opponent)
            ),
            of (Effect.class, act ->
                out.printf("> %s casts effect %s%n",
                        actor,
                        action)
            ),
            other (obj -> {
                if (action == Action.IDLE) {
                    out.printf("> %s does nothing%n", actor);
                } else {
                    throw new IllegalArgumentException("Unknown action " + action);
                }
            })
        );
    }

    @Override
    public void onAfterTurn(Turn turn) {

    }
}
