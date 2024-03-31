package ru.asocial.games.core;

import com.badlogic.gdx.math.Vector2;

public class EntityMatrixUtils {

    public static void freeObject(EntityMatrix matrix, Entity entity, Vector2 offset) {
        matrix.free((int) entity.getX() / (int) entity.getWidth() + (int) offset.x,(int) entity.getY() / (int) entity.getHeight() + (int) offset.y);
    }

    public static void freeObject(EntityMatrix matrix, Entity entity) {
        matrix.free((int) entity.getX() / (int) entity.getWidth(),(int) entity.getY() / (int) entity.getHeight());

    }

    public static Entity getWithOffset(EntityMatrix matrix, Entity center, int offsetX, int offsetY) {
        return matrix.get((int) center.getX() / (int) center.getWidth() + offsetX, (int) center.getY() / (int) center.getHeight() + offsetY);
    }
}
