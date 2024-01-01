package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import ru.asocial.games.core.Entity;
import ru.asocial.games.core.Layers;
import ru.asocial.games.core.PropertyKeys;

public class FallingBehavior extends MovingBehavior{
    public FallingBehavior(Layers layers) {
        super(layers);
    }

    @Override
    protected Vector2 findNextMove(Entity entity) {
        Vector2 move = new Vector2(0, -1);
        int cx = (int) (entity.getX() / entity.getWidth() + move.x);
        int cy = (int) (entity.getY() / entity.getHeight() + move.y);
        if (isCellFree(cx, cy)) {
            entity.putProperty(PropertyKeys.IS_ROLLING, false);
            entity.putProperty(PropertyKeys.IS_FALLING, true);
            return move;
        }

        Entity e = getObjectAtCell(entity, move);
        if (e != null && entity.getPropertyOrDefault(PropertyKeys.IS_FALLING, Boolean.class, false) &&
                e.getPropertyOrDefault(PropertyKeys.IS_SQUIZABLE, Boolean.class, false)) {
            e.addAction(Actions.removeActor());
            freeObjectAtCell(entity, move);
        }

        entity.putProperty(PropertyKeys.IS_FALLING, false);
        return null;
    }
}
