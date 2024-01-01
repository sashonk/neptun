package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import ru.asocial.games.core.Entity;
import ru.asocial.games.core.Layers;
import ru.asocial.games.core.PropertyKeys;

public class WasdController extends MovingBehavior{

    public WasdController(Layers layers) {
        super(layers);
    }

    @Override
    protected Vector2 findNextMove(Entity entity) {
        Vector2 move = null;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            move = new Vector2(0, 1);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            move = new Vector2(0, -1);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            move = new Vector2(1, 0);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            move = new Vector2(-1, 0);
        }
        if (move != null) {
            int cx = (int) (entity.getX() / entity.getWidth() + move.x);
            int cy = (int) (entity.getY() / entity.getHeight() + move.y);
            if (isCellFree(cx, cy)) {
                return move.cpy();
            }

            if (isDirtAtCell(cx, cy)) {
                removeDirtAtCell(cx, cy);
                return move.cpy();
            }

            Entity e = getObjectAtCell(cx, cy);
            if (move.y == 0 && e != null && e.getProperty(PropertyKeys.CAN_ROLL, Boolean.class) && !e.getPropertyOrDefault(PropertyKeys.IS_ROLLING, Boolean.class, false)) {
                Vector2 behind = move.cpy().scl(2);
                int bx = (int) (entity.getX() / entity.getWidth() + behind.x);
                int by = (int) (entity.getY() / entity.getHeight() + behind.y);
                if (isCellFree(bx, by)) {
                    e.putProperty(PropertyKeys.IS_ROLLING, true);
                    e.putProperty(PropertyKeys.ROLLING_DIRECTION, move.x == 1 ? "right" : "left");
                    //entity.addAction(Actions.);
                    return move.cpy();
                }

            }
        }

        return null;
    }
}
