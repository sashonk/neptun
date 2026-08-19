package ru.asocial.games.core.events;

import ru.asocial.games.core.Entity;

public class PlayerKilledEvent extends EntityEvent {

    private final Entity victim;
    private final Entity killer;

    public PlayerKilledEvent(Entity victim, Entity killer) {
        this.victim = victim;
        this.killer = killer;
    }

    public Entity getVictim() {
        return victim;
    }

    public Entity getKiller() {
        return killer;
    }
}
