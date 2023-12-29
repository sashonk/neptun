package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.math.Vector2;
import ru.asocial.games.core.Entity;
import ru.asocial.games.core.Layers;

public class WalkingBehaviour extends MovingBehavior {

    public static final String PropertyKey_handle = "walking_handle";
    public static final String PropertyKey_direction = "walking_direction";

    public WalkingBehaviour(Layers layers) {
        super(layers);
    }

    @Override
    protected Vector2 findNextMove(Entity entity) {
        int c = 0;
        while (true) {
            Vector2 handle = entity.getProperty(PropertyKey_handle, Vector2.class);
            Vector2 direction = entity.getProperty(PropertyKey_direction, Vector2.class);
            if (handle == null) {
                handle = new Vector2(0, 1);
                direction = new Vector2(1 , 0);
                entity.putProperty(PropertyKey_handle, handle);
                entity.putProperty(PropertyKey_direction, direction);
            }
            int cx = (int) (entity.getX() / entity.getWidth() + handle.x);
            int cy = (int) (entity.getY() / entity.getHeight() + handle.y);
            if (!isCellFree(cx, cy)) {
                if (c++ > 5) {
                    break;
                }
                int dx = (int) (entity.getX() / entity.getWidth() + direction.x);
                int dy = (int) (entity.getY() / entity.getHeight() + direction.y);
                if (isCellFree(dx, dy)) {
                    return direction.cpy();
                }
                handle.rotate90(-1);
                direction.rotate90(-1);
            }
            else {
                handle.rotate90(1);
                direction.rotate90(1);
                return direction.cpy();
            }
        }
        return null;
    }
}
