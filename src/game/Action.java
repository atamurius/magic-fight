package game;

public interface Action {
    void perform(Character actor, Character opponent);

    Action IDLE = (actor, opponent) -> { };
}
