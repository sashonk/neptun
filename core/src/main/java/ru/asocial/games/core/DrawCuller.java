package ru.asocial.games.core;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Viewport bounds for draw-time culling. Updated once per frame before stage draw.
 * Does not affect {@link Actor#act(float)} — off-screen entities keep simulating.
 */
public final class DrawCuller {

    private static final float DEFAULT_MARGIN = 96f;

    private static float minX;
    private static float minY;
    private static float maxX;
    private static float maxY;

    private static int visibleCount;
    private static int culledCount;

    private DrawCuller() {
    }

    public static void begin(OrthographicCamera camera) {
        begin(camera, DEFAULT_MARGIN);
    }

    public static void begin(OrthographicCamera camera, float margin) {
        float halfWidth = camera.viewportWidth * camera.zoom * 0.5f;
        float halfHeight = camera.viewportHeight * camera.zoom * 0.5f;
        minX = camera.position.x - halfWidth - margin;
        maxX = camera.position.x + halfWidth + margin;
        minY = camera.position.y - halfHeight - margin;
        maxY = camera.position.y + halfHeight + margin;
        visibleCount = 0;
        culledCount = 0;
    }

    public static boolean isVisible(Actor actor) {
        boolean visible = isRectVisible(
                actor.getX(),
                actor.getY(),
                actor.getWidth() * Math.abs(actor.getScaleX()),
                actor.getHeight() * Math.abs(actor.getScaleY())
        );
        if (visible) {
            visibleCount++;
        } else {
            culledCount++;
        }
        return visible;
    }

    public static boolean isRectVisible(float x, float y, float width, float height) {
        return x + width >= minX && x <= maxX && y + height >= minY && y <= maxY;
    }

    public static int getVisibleCount() {
        return visibleCount;
    }

    public static int getCulledCount() {
        return culledCount;
    }
}
