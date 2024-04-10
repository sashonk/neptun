package ru.asocial.games.core.events;

import ru.asocial.games.core.Entity;

public class DestroyEntityEvent extends EntityEvent {

    private Entity relatedEntity;
    private boolean squized;

    public void setRelatedEntity(Entity relatedEntity) {
        this.relatedEntity = relatedEntity;
    }

    public Entity getRelatedEntity() {
        return relatedEntity;
    }

    public void setSquized(boolean squized) {
        this.squized = squized;
    }

    public boolean isSquized() {
        return squized;
    }
}
