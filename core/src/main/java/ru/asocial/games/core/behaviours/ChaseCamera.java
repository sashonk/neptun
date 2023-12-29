package ru.asocial.games.core.behaviours;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import ru.asocial.games.core.Entity;

public class ChaseCamera implements Behaviour{

    private final Camera camera;

    public ChaseCamera(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void act(Entity entity, float delta) {
        Vector3 camPos = camera.position;
        float x = entity.getX() - camPos.x;
        float y = entity.getY() - camPos.y;

        float a = new Vector2(x, y).len2() * 0.3f;
        float ax = (float) (a * Math.cos(Math.atan2(y, x)));
        float ay = (float) (a * Math.sin(Math.atan2(y, x)));

        float vx = ax * delta;
        float vy = ay * delta;

        float dx = vx * delta;
        float dy = vy * delta;

        if (Math.abs(dx) > 0.003 || Math.abs(dy) > 0.003) {
            camera.translate(dx, dy, 0);
        }
    }
}
