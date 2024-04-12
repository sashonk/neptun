package ru.asocial.games.core.events;

import ru.asocial.games.core.Entity;

public class PlayerKilledEvent extends EntityEvent {

    private Entity victim;
    public PlayerKilledEvent(Entity victim) {
        this.victim = victim;
    }

    public Entity getVictim() {
        return victim;
    }
}
