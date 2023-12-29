package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import ru.asocial.games.core.Entity;
import ru.asocial.games.core.Layers;

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
        }

        return null;
    }
}
