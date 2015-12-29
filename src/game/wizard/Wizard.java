package game.wizard;

import game.*;
import game.Character;

import java.util.*;
import java.util.stream.Collectors;

public class Wizard extends game.Character {

    private int mana;

    private final List<Spell> spells = new ArrayList<Spell>();

    private static final List<Action> IDLE = Collections.singletonList(Action.IDLE);

    private final Map<Effect,Integer> effects = new HashMap<>();

    public Wizard(int health, int mana) {
        super(health, 0);
        this.mana = mana;
    }

    public void learn(Spell spell) {
        spells.add(spell);
    }

    public void start(Effect effect) {
        effects.put(effect, -1);
    }

    @Override
    public List<Action> getPossibleActions() {
        List<Action> spells = this.spells.stream()
                .filter(spell -> spell.getCost() <= mana)
                .filter(spell -> ! hasActiveEffect(spell))
                .collect(Collectors.toList());
        return spells.isEmpty() ? IDLE : spells;
    }

    private boolean hasActiveEffect(Spell effect) {
        return this.effects.getOrDefault(effect,0) > 1;
    }

    @Override
    public void beforeTurn(Character opponent) {
        effects.forEach((effect, timeLeft) -> {
            effect.turn(this, opponent);
            effects.replace(effect, timeLeft - 1);
        });
    }

    @Override
    public void afterTurn(Character opponent) {
        effects.keySet().removeIf(effect -> {
            if (effects.get(effect) == 0) {
                effect.stop(this, opponent);
                return true;
            }
            return false;
        });
        effects.forEach((effect, timeLeft) -> {
            if (timeLeft < 0) {
                effect.start(this, opponent);
                effects.replace(effect, effect.getTurns());
            }
        });
    }

    public void setArmor(int armor) {
        this.armor = armor;
    }

    public int getMana() {
        return mana;
    }

    public void setMana(int mana) {
        this.mana = mana;
    }

    public Map<Effect, Integer> getEffects() {
        return effects;
    }
}
