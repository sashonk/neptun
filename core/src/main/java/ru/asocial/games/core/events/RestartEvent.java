package ru.asocial.games.core.events;

import com.badlogic.gdx.scenes.scene2d.Event;
import ru.asocial.games.core.Entity;

public class RestartEvent extends Event {

    private final Entity player;
    private final boolean nextLvl;
    private final float restartDelay;

    public RestartEvent(Entity player, boolean nextLevel) {
        this(player, nextLevel, nextLevel ? 1f : 1f);
    }

    public RestartEvent(Entity player, boolean nextLevel, float restartDelay) {
        this.player = player;
        this.nextLvl = nextLevel;
        this.restartDelay = restartDelay;
    }

    public Entity getPlayer() {
        return player;
    }

    public boolean isNextLvl() {
        return nextLvl;
    }

    public float getRestartDelay() {
        return restartDelay;
    }
}
