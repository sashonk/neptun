package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ru.asocial.games.core.Config;
import ru.asocial.games.core.Entity;
import ru.asocial.games.core.Layers;
import ru.asocial.games.core.PropertyKeys;

public class RollingStoneBehavior extends MovingBehavior{

    private final RollingBehavior rollingBehavior;
    private final FallingBehavior fallingBehavior;

    public RollingStoneBehavior(Layers layers) {
        super(layers);

        rollingBehavior = new RollingBehavior(layers);
        fallingBehavior = new FallingBehavior(layers);
    }

    @Override
    public void act(Entity entity, float delta) {
        boolean isRolling = entity.getPropertyOrDefault(PropertyKeys.IS_ROLLING, Boolean.class, false);
        if (isRolling) {
            String rollDir = entity.getPropertyOrDefault(PropertyKeys.ROLLING_DIRECTION, String.class, "right");
            float stateTime = entity.getPropertyOrDefault(PropertyKeys.ANIMATION_STATE_TIME, Float.class, 0f);
            stateTime += Gdx.graphics.getDeltaTime();
            entity.putProperty(PropertyKeys.ANIMATION_STATE_TIME, stateTime);
            boolean clockwise = "right".equals(rollDir);
            float o = 2 * MathUtils.radiansToDegrees / Config.SINGLE_MOVE_DURATION * (clockwise ? -1 : 1);
            float angle = o * stateTime;
            entity.setRotation(angle);
        }
        super.act(entity, delta);
    }

    @Override
    protected Vector2 findNextMove(Entity entity) {
        Vector2 move = fallingBehavior.findNextMove(entity);
        if (move != null) {
            return move;
        }
        move = rollingBehavior.findNextMove(entity);
        if (move != null) {
            entity.removeProperty(PropertyKeys.DELAY);
        }
        return move;
    }
}
