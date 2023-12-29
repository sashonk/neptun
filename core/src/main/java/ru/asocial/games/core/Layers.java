package ru.asocial.games.core;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;

public class Layers {
    private final TiledMapTileLayer wallLayer;
    private final MapLayer objectLayer;
    private final TiledMapTileLayer dirtLayer;

    public Layers(TiledMapTileLayer wallLayer, MapLayer objectLayer, TiledMapTileLayer dirtLayer) {
        this.wallLayer = wallLayer;
        this.objectLayer = objectLayer;
        this.dirtLayer = dirtLayer;
    }

    public TiledMapTileLayer getWallLayer() {
        return wallLayer;
    }

    public MapLayer getObjectLayer() {
        return objectLayer;
    }

    public TiledMapTileLayer getDirtLayer() {
        return dirtLayer;
    }
}
