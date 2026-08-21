package ru.asocial.games.core.events;

import com.badlogic.gdx.scenes.scene2d.Event;
import ru.asocial.games.core.Entity;

public class LevelCompleteEvent extends Event {

    private final Entity player;
    private final int levelNumber;

    public LevelCompleteEvent(Entity player, int levelNumber) {
        this.player = player;
        this.levelNumber = levelNumber;
    }

    public Entity getPlayer() {
        return player;
    }

    public int getLevelNumber() {
        return levelNumber;
    }
}
