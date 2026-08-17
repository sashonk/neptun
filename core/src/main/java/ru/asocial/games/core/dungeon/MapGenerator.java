package ru.asocial.games.core.dungeon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import ru.asocial.games.core.PropertyKeys;
import ru.asocial.games.neptun.dungeonmaker.DungeonMaker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MapGenerator {

    private static long level = 1;

    private GridPoint2 playerXY;

    private boolean generateRandomMaps = true;

    private final List<GridPoint2> exitPoints = new LinkedList<>();

    private int x;

    public interface EventHandler {
        void exitPlaced(int x, int y);

        void playerPlaced(int x, int y);
    }

    public TiledMap generateMap(boolean next, Skin skin, EventHandler handler) {
        TiledMap tiledMap = new TiledMap();
/*            MapLayer mainLayer = new MapGroupLayer();
            mainLayer.setName("main");*/
        int layerWidth = 500, layerHeight = 500;
        int tileWidth = 48, tileHeight = 48;
        TiledMapTileLayer imageLayer = new TiledMapTileLayer(layerWidth, layerHeight, tileWidth, tileHeight);
        imageLayer.setName("image");
        TiledMapTileLayer dirtLayer = new TiledMapTileLayer(layerWidth, layerHeight, tileWidth, tileHeight);
        dirtLayer.setName("dirt");
        TiledMapTileLayer wallLayer = new TiledMapTileLayer(layerWidth, layerHeight, tileWidth, tileHeight);
        wallLayer.setName("walls");
        wallLayer.setVisible(true);
        tiledMap.getLayers().add(imageLayer);
        tiledMap.getLayers().add(wallLayer);
        tiledMap.getLayers().add(dirtLayer);
        if (next) {
            level++;
        }
        Random rnd = new Random(System.currentTimeMillis());

        BufferedReader br = null;
        try {
            if (generateRandomMaps) {
                int digit = rnd.nextInt(1, 4);
                String letter = rnd.nextBoolean() ? "a" : "b";
                FileHandle designFile = Gdx.files.internal("designs/design3a");
                FileHandle tmp = FileHandle.tempFile("design");
                designFile.copyTo(tmp);
                //String designFileName = "C:\\Users\\user\\Downloads\\dmaker\\dungeonmaker2_0WinExe\\design";
                String designFileName = tmp.path();
                String dungeonFileName = FileHandle.tempFile("dungeon").path();
                File dungeonFile = new File(dungeonFileName);
/*                try {
                    if (!dungeonFile.createNewFile()) {
                        throw new GdxRuntimeException("can't create file: " + dungeonFile.getPath());
                    }
                }
                catch (Exception exc) {
                    throw new GdxRuntimeException("can't create file: " + dungeonFile.getPath(), exc);
                }*/
                DungeonMaker dungeonMaker = new DungeonMaker();
                dungeonMaker.generateDungeon(designFileName, dungeonFileName);
                br = Files.newBufferedReader(dungeonFile.toPath());
            }
            else {
                FileHandle dungFile = Gdx.files.internal(String.format("dungeons/%d.txt", level));
                br = dungFile.reader(1024);
            }

            int width = 48, height = 48;
            List<String> lines = br.lines().collect(Collectors.toList());
            int mapHeight = lines.size();
            int mapWidth = lines.get(0).split(",").length;

            lines.forEach(line -> processLine(line, width, height, mapWidth, mapHeight, wallLayer, dirtLayer, imageLayer, skin, rnd, handler));

            {
                // Player
                playerXY = exitPoints.remove(rnd.nextInt(exitPoints.size()));
                Array<TextureRegion> regionArray = skin.getRegions("player/front");
                TiledMapTile tile = new StaticTiledMapTile(regionArray.get(0));
                TiledMapTileMapObject o = new TiledMapTileMapObject(tile, false, false);
                fillBaseProperties(o, playerXY.x, playerXY.y, width, height);
                fillDeathSpiritProps(o);
                wallLayer.getObjects().add(o);
                handler.playerPlaced(playerXY.x, playerXY.y);
            }

            {
                //Exit
                GridPoint2 exitPoint = exitPoints.remove(rnd.nextInt(exitPoints.size()));
                Array<TextureRegion> regionArray = skin.getRegions("doors/19/door");
                Array<StaticTiledMapTile> tileArray = new Array<>();
                regionArray.forEach(region -> tileArray.add(new StaticTiledMapTile(region)));
                TiledMapTile animatedTile = new AnimatedTiledMapTile(1f, tileArray);
                TiledMapTileMapObject o = new TiledMapTileMapObject(animatedTile, false, false);
                fillBaseProperties(o, exitPoint.x, exitPoint.y , width, height);
                o.getProperties().put("type", "exit");
                o.getProperties().put(PropertyKeys.ANIMATION, regionArray);
                wallLayer.getObjects().add(o);
                handler.exitPlaced(exitPoint.x, exitPoint.y);
            }

            return tiledMap;
        }
        catch (Exception ex) {
            throw new GdxRuntimeException(ex);
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    void processLine(String line, int width, int height, int mapWidth, int mapHeight, TiledMapTileLayer wallLayer, TiledMapTileLayer dirtLayer,
                     TiledMapTileLayer imageLayer, Skin skin, Random rnd, EventHandler eventHandler) {
        String[] types = line.split(",");
        for (int y = 0; y < types.length; y++ ) {
            int type = Integer.valueOf(types[y]);
            int cx = y;
            int cy = mapHeight - x - 1;
            if (type == SquareData.CLOSED.ordinal() || type == SquareData.G_CLOSED.ordinal()) {
                TiledMapTile tile = new StaticTiledMapTile(skin.getRegion("walls/128"));
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(tile);
                wallLayer.setCell(cx, cy, cell);
                //aStar.setBlock(x, y);
            }
            else if ( type == SquareData.IR_OPEN.ordinal()  && y > 5 && y < mapWidth - 6) {
                if (rnd.nextFloat() < 0.9f) {
                    if (rnd.nextFloat() < 0.8f) {
                        TiledMapTile tile = new StaticTiledMapTile(skin.getRegion("walls/126"));
                        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                        cell.setTile(tile);
                        dirtLayer.setCell(cx, cy, cell);
                    }
                    else {
                        MapObject o = createStone(rnd.nextBoolean(),cx, cy, width, height, skin);
                        wallLayer.getObjects().add(o);
                    }
                }
                else {
                    if (rnd.nextBoolean()) {
                        MapObject o = createMob(cx, cy, width, height, skin);
                        wallLayer.getObjects().add(o);
                    }
                }
            }
            else if (type == SquareData.OPEN.ordinal()) {
                if (rnd.nextFloat() < 0.98f) {
                    float nextFloat = rnd.nextFloat();
                    if (nextFloat < 0.8f) {
                        TiledMapTile tile = new StaticTiledMapTile(skin.getRegion("walls/126"));
                        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                        cell.setTile(tile);
                        dirtLayer.setCell(cx, cy, cell);
                    }
                    else if (nextFloat < 0.9){

                    }
                    else {
                        MapObject o = createStone(rnd.nextBoolean(),cx, cy, width, height, skin);
                        wallLayer.getObjects().add(o);
                    }
                }
                else {
                    if (rnd.nextBoolean()) {
                        MapObject o = createMob(cx, cy, width, height, skin);
                        wallLayer.getObjects().add(o);
                    }
                }
            }
            else if ((type == SquareData.IT_OPEN.ordinal() || type == SquareData.IA_OPEN.ordinal() )  && y > 5 && y < mapWidth - 6) {
                if (rnd.nextFloat() < 0.95f) {
                    if (rnd.nextFloat() < 0.7f) {
                        TiledMapTile tile = new StaticTiledMapTile(skin.getRegion("walls/126"));
                        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                        cell.setTile(tile);
                        dirtLayer.setCell(cx, cy, cell);
                    }
                    else {
                        MapObject o = createStone(rnd.nextBoolean(),cx, cy, width, height, skin);

                        wallLayer.getObjects().add(o);
                    }
                }
                else {
                    if (rnd.nextBoolean()) {
                        wallLayer.getObjects().add(createMob(cx, cy, width, height, skin));
                    }
                }
            }
            else if (type == SquareData.G_OPEN.ordinal()) {
                if (y == 0 || y == mapWidth - 1 || x == 0 || x == mapHeight - 1) {
                    TiledMapTile tile = new StaticTiledMapTile(skin.getRegion("walls/128"));

                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(tile);
                    wallLayer.setCell(cx, cy, cell);
                    //imageLayer.setCell(cx, cy);
                }
                else {
                    boolean alreadyExists = false;
                    for (GridPoint2 exitPoint : exitPoints) {
                        if (Math.abs(cx - exitPoint.x) < 30 && Math.abs(cy - exitPoint.y) < 30) {
                            alreadyExists = true;
                            break;
                        }
                    }
                    if (!alreadyExists) {
                        exitPoints.add(new GridPoint2(cx, cy));
                    }
                }

                {
                    TiledMapTile tile = new StaticTiledMapTile(skin.getRegion("general/physical_hazard"));
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(tile);
                    imageLayer.setCell(cx, cy, cell);
                }
            }
        }
        x++;
    }

    MapObject createMob(int cx, int cy, int width, int height, Skin skin) {
        Array<TextureRegion> regionArray = skin.getRegions("golem/front");
        TiledMapTile tile = new StaticTiledMapTile(regionArray.get(0));
        TiledMapTileMapObject o = new TiledMapTileMapObject(tile, false, false);
        fillBaseProperties(o, cx, cy, width, height);
        o.getProperties().put("type", "golem");
        o.getProperties().put("has_animations",  true);
        o.getProperties().put("is_squizable",  true);
        o.getProperties().put("is_walking", true);
        o.getProperties().put("is_enemy", true);
        //o.getProperties().put("is_explosive", true);
        return o;
    }

    MapObject createStone(boolean isExplosive, int cx, int cy, int width, int height, Skin skin) {
        TiledMapTile tile = new StaticTiledMapTile(skin.getRegion(isExplosive ? "walls/stone_hot" : "walls/stone"));
        TiledMapTileMapObject o = new TiledMapTileMapObject(tile, false, false);
        fillBaseProperties(o, cx, cy, width, height);
        o.getProperties().put("can_roll", true);
        o.getProperties().put("can_fall", true);
        o.getProperties().put("type", "stone");
        if (isExplosive) {
            o.getProperties().put("is_explosive", true);
        }
        return o;
    }

    void fillBaseProperties(TiledMapTileMapObject o, int cx, int cy, int width, int height) {
        o.setX(cx * width);
        o.setY(cy * height);
        o.getProperties().put("width", (float) width);
        o.getProperties().put("height", (float) height);
    }

    void fillDeathSpiritProps(TiledMapTileMapObject o) {
        o.setName("deathspirit");
        o.getProperties().put("type", "player");
        o.getProperties().put("name", "deathspirit");
        o.getProperties().put("attach_controller",  true);
        o.getProperties().put("chase_camera",  true);
        o.getProperties().put("has_animations",  true);
        o.getProperties().put("is_squizable",  true);
    }

}
