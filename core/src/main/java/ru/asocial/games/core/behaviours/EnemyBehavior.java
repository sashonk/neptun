package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.math.Vector2;
import ru.asocial.games.core.Entity;
import ru.asocial.games.core.EntityMatrix;
import ru.asocial.games.core.PropertyKeys;
import ru.asocial.games.core.events.RestartEvent;

import java.util.Arrays;

public class EnemyBehavior implements Behaviour {

    private static EntityMatrix matrix;

    public static void setMatrix(EntityMatrix m) {
        matrix = m;
    }

    @Override
    public void act(Entity entity, float delta) {
        checkPlayerOneSquareOff(entity);
    }

    private void checkPlayerOneSquareOff(final Entity entity) {

        Vector2[] vv = new Vector2[]{
                new Vector2(1, 0),
                new Vector2(0, 1),
                new Vector2(-1, 0),
                new Vector2(0, -1)
        };
        Arrays.stream(vv).forEach(v -> {
            Entity e = getObjectAtCell(entity, v);
            if (e != null && "deathspirit".equals(e.getProperty(PropertyKeys.TYPE, String.class))) {
                e.getStage().getRoot().fire(new RestartEvent(e, false));
            }
        });
    }

    private Entity getObjectAtCell(Entity entity, Vector2 dir) {
        return matrix.get((int) entity.getX() / (int) entity.getWidth() + (int) dir.x,(int) entity.getY() / (int) entity.getHeight() + (int) dir.y);
    }
}
