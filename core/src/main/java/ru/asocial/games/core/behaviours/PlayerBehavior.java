package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import ru.asocial.games.core.*;
import ru.asocial.games.core.events.RestartEvent;

public class PlayerBehavior extends MovingBehavior{

    public PlayerBehavior(Layers layers) {
        super(layers);
    }

    @Override
    protected Vector2 findNextMove(Entity entity) {
        Vector2 move = null;
        Preferences keyboardKeys = Gdx.app.getPreferences("keyboard");
        int keyUp = keyboardKeys.getInteger("up");
        int keyDown = keyboardKeys.getInteger("down");
        int keyLeft = keyboardKeys.getInteger("left");
        int keyRight = keyboardKeys.getInteger("right");
        int keyAction = keyboardKeys.getInteger("action");
        boolean isActing = Gdx.input.isKeyPressed(keyAction);
        if (Gdx.input.isKeyPressed(keyUp)) {
            move = new Vector2(0, 1);
        }
        else if (Gdx.input.isKeyPressed(keyDown)) {
            move = new Vector2(0, -1);
        }
        else if (Gdx.input.isKeyPressed(keyRight)) {
            move = new Vector2(1, 0);
        }
        else if (Gdx.input.isKeyPressed(keyLeft)) {
            move = new Vector2(-1, 0);
        }
        if (move != null) {
            EntityMove direction = EntityMove.fromVector2(move);
            EntityOrientation orientation = EntityOrientation.fromMoveDirection(direction);
            entity.putProperty(PropertyKeys.ORIENTATION, orientation.name());

            int cx = (int) (entity.getX() / entity.getWidth() + move.x);
            int cy = (int) (entity.getY() / entity.getHeight() + move.y);

            if (isCellFree(cx, cy) && !isActing) {
                return move.cpy();
            }

            if (isDirtAtCell(cx, cy)) {
                removeDirtAtCell(cx, cy);
                if (!isActing) {
                    return move.cpy();
                }
            }

            Entity e = getObjectAtCell(cx, cy);
            if (e != null && !isActing) {
                if ("exit".equals(e.getProperty(PropertyKeys.TYPE, String.class))) {
                    e.getStage().getRoot().fire(new RestartEvent(entity, true));
                }

                if (move.y == 0 && e.getPropertyOrDefault(PropertyKeys.CAN_ROLL, Boolean.class, false) && !e.getPropertyOrDefault(PropertyKeys.IS_ROLLING, Boolean.class, false)) {
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
        }
        entity.putProperty("delay", 0.1f);
        return null;
    }
}
