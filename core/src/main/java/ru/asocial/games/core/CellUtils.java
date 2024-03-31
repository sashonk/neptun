package ru.asocial.games.core;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.GridPoint2;

public class CellUtils {

    public static boolean isDirtAtCell(TiledMapTileLayer dirtLayer, int cellX, int cellY) {
        return dirtLayer.getCell(cellX, cellY) != null;
    }

    public static boolean isDirtAtCell(TiledMapTileLayer dirtLayer, Entity center, int offsetX, int offsetY) {
        return dirtLayer.getCell((int)center.getX() / (int)center.getWidth() + offsetX, (int)center.getY() / (int)center.getHeight() + offsetY) != null;
    }

    public static GridPoint2 getCellCoors(Entity e, int offsetX, int offsetY) {
        return new GridPoint2((int)e.getX() / (int) e.getWidth() + offsetX, (int) e.getY() / (int)e.getHeight() + offsetY);
    }
}
