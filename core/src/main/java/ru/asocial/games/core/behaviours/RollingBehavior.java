package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ru.asocial.games.core.Config;
import ru.asocial.games.core.Entity;
import ru.asocial.games.core.Layers;
import ru.asocial.games.core.PropertyKeys;

import java.util.HashMap;
import java.util.Map;

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
            Map<String, Integer> sides = new HashMap<>();
            sides.put("left", -1);
            sides.put("right", 1);
            for (Map.Entry<String, Integer> sideEntry : sides.entrySet()) {
                Vector2 right = new Vector2(sideEntry.getValue(), 0);
                int rx = (int) (entity.getX() / entity.getWidth() + right.x);
                int ry = (int) (entity.getY() / entity.getHeight() + right.y);
                if (isCellFree(rx, ry)) {
                    Vector2 rightDown = new Vector2(sideEntry.getValue(), -1);
                    int rdx = (int) (entity.getX() / entity.getWidth() + rightDown.x);
                    int rdy = (int) (entity.getY() / entity.getHeight() + rightDown.y);
                    if (isCellFree(rdx, rdy)) {
                        entity.putProperty(PropertyKeys.ROLLING_DIRECTION, sideEntry.getKey());
                        entity.putProperty(PropertyKeys.IS_ROLLING, true);
                        return right;
                    }
                }
            }

        }

        return null;
    }
}
