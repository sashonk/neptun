package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.Vector2;
import ru.asocial.games.core.*;
import ru.asocial.games.core.events.PlaceBombEvent;
import ru.asocial.games.core.events.RestartEvent;

public class PlayerBehavior extends MovingBehavior{

    private IPlayerController controller;

    public PlayerBehavior(Layers layers) {
        super(layers);
    }

    public void setController(IPlayerController controller) {
        this.controller = controller;
    }

    protected Vector2 doFindNextMove() {
        Vector2 move = null;
        if (controller != null) {
            if (controller.isUpPressed()) {
                move = new Vector2(0, 1);
            }
            else if (controller.isDownPressed()) {
                move = new Vector2(0, -1);
            }
            else if (controller.isRightPressed()) {
                move = new Vector2(1, 0);
            }
            else if (controller.isLeftPressed()) {
                move = new Vector2(-1, 0);
            }
        }

        return move;
    }

    @Override
    public void act(Entity entity, float delta) {
        super.act(entity, delta);

        if (controller != null && controller.isBombPressed()) {
            Float bombRegenTime =  entity.getPropertyOrDefault("bomb_regen_time", Float.class, 0f);
            if (bombRegenTime <= 0) {
                entity.putProperty("bomb_regen_time", 3f);
                String orientation = entity.getProperty(PropertyKeys.ORIENTATION, String.class);
                entity.fire(new PlaceBombEvent());
            }
        }

        Float bombRegenTime = entity.getPropertyOrDefault("bomb_regen_time", Float.class, 0f);
        if (bombRegenTime > 0) {
            entity.putProperty("bomb_regen_time", bombRegenTime - delta);
        }
    }

    @Override
    protected Vector2 findNextMove(Entity entity) {
        if (entity.getPropertyOrDefault("is_dead", Boolean.class, false)) {
            return null;
        }
        Vector2 move = doFindNextMove();
        if (move != null) {
            Preferences keyboardKeys = Gdx.app.getPreferences("keyboard");
            int keyAction = keyboardKeys.getInteger("action");
            boolean isActing = Gdx.input.isKeyPressed(keyAction);
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
        return null;
    }
}
