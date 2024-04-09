package ru.asocial.games.core.events;

import com.badlogic.gdx.scenes.scene2d.Event;
import ru.asocial.games.core.Entity;

public class DestroyEntityEvent extends Event {

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
