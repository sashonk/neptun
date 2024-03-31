package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import ru.asocial.games.core.Entity;

public class LimitedLifeTimeBehavior implements Behaviour {

    private static final String PROP_TIME_LEFT = "time_left";
    private float timeLimit;


    public LimitedLifeTimeBehavior(float timeLimit) {
        this.timeLimit = timeLimit;
    }

    @Override
    public void act(Entity entity, float delta) {
        Float timeLeft = entity.getPropertyOrDefault(PROP_TIME_LEFT, Float.class, timeLimit);
        timeLeft -= delta;

        if (timeLeft <= 0) {
            entity.addAction(Actions.removeActor());
        }

        entity.putProperty(PROP_TIME_LEFT, timeLeft);
    }
}
