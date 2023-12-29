package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.math.Vector2;
import ru.asocial.games.core.Entity;
import ru.asocial.games.core.Layers;
import ru.asocial.games.core.PropertyKeys;

public class RollingBehavior  extends MovingBehavior{
    public RollingBehavior(Layers layers) {
        super(layers);
    }

    @Override
    protected Vector2 findNextMove(Entity entity) {
        boolean isRolling = entity.getPropertyOrDefault(PropertyKeys.IS_ROLLING, Boolean.class, false);
        if (isRolling) {
            String rollDir = entity.getPropertyOrDefault(PropertyKeys.ROLLING_DIRECTION, String.class, "right");
            Vector2 move = new Vector2("left".equals(rollDir) ? -1 : 1, 0);
            int cx = (int) (entity.getX() / entity.getWidth() + move.x);
            int cy = (int) (entity.getY() / entity.getHeight() + move.y);
            if (isCellFree(cx, cy)) {
                entity.putProperty(PropertyKeys.IS_FALLING, false);
                return move;
            }
            entity.putProperty(PropertyKeys.IS_ROLLING, false);
        }

        Vector2 down = new Vector2(0, -1);
        int cx = (int) (entity.getX() / entity.getWidth() + down.x);
        int cy = (int) (entity.getY() / entity.getHeight() + down.y);
        Entity e = getObjectAtCell(cx, cy);
        if (e != null && e.getPropertyOrDefault(PropertyKeys.CAN_ROLL, Boolean.class, false)) {
            Vector2 right = new Vector2(1, 0);
            int rx = (int) (entity.getX() / entity.getWidth() + right.x);
            int ry = (int) (entity.getY() / entity.getHeight() + right.y);
            if (isCellFree(rx, ry)) {
                Vector2 rightDown = new Vector2(1, -1);
                int rdx = (int) (entity.getX() / entity.getWidth() + rightDown.x);
                int rdy = (int) (entity.getY() / entity.getHeight() + rightDown.y);
                if (isCellFree(rdx, rdy)) {
                    entity.putProperty(PropertyKeys.ROLLING_DIRECTION, "right");
                    entity.putProperty(PropertyKeys.IS_ROLLING, true);
                    return right;
                }
            }

            Vector2 left = new Vector2(-1, 0);
            int lx = (int) (entity.getX() / entity.getWidth() + left.x);
            int ly = (int) (entity.getY() / entity.getHeight() + left.y);
            if (isCellFree(lx, ly)) {
                Vector2 leftDown = new Vector2(-1, -1);
                int ldx = (int) (entity.getX() / entity.getWidth() + leftDown.x);
                int ldy = (int) (entity.getY() / entity.getHeight() + leftDown.y);
                if (isCellFree(ldx, ldy)) {
                    entity.putProperty(PropertyKeys.ROLLING_DIRECTION, "left");
                    entity.putProperty(PropertyKeys.IS_ROLLING, true);
                    return left;
                }
            }
        }

        return null;
    }
}
