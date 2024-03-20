package ru.asocial.games.core.dungeon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.objects.TiledMapTileMapObject;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import ru.asocial.games.core.PropertyKeys;
import ru.asocial.games.core.astar.AStar;
import ru.asocial.games.core.astar.Node;
import ru.asocial.games.neptun.dungeonmaker.DungeonMaker;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MapGenerator {

    public MapGenerator(boolean generateRandomMaps) {
        this.generateRandomMaps = generateRandomMaps;
    }

    private static long lastSeed = 1;

    private boolean deathSpiritPlaced;

    private boolean exitPlaced;

    private int x;

    private boolean generateRandomMaps;

    public TiledMap generateMap(boolean useSameDungeonFile, Skin skin) {
        int count = 5;
        while (count > 0) {
            DungeonMaker dmaker = new DungeonMaker();
            TiledMap tiledMap = new TiledMap();
/*            MapLayer mainLayer = new MapGroupLayer();
            mainLayer.setName("main");*/
            TiledMapTileLayer dirtLayer = new TiledMapTileLayer(200, 200, 48, 48);
            dirtLayer.setName("dirt");
            TiledMapTileLayer wallLayer = new TiledMapTileLayer(200, 200, 48, 48);
            wallLayer.setName("walls");
            wallLayer.setVisible(true);
            tiledMap.getLayers().add(wallLayer);
            //tiledMap.getLayers().add(mainLayer);
            tiledMap.getLayers().add(dirtLayer);


            if (!useSameDungeonFile) {
                lastSeed++;
            }
            Random rnd = new Random(lastSeed);

            BufferedReader br = null;
            try {
                if (generateRandomMaps) {
                    String designFileName = "C:\\Users\\user\\Downloads\\dmaker\\dungeonmaker2_0WinExe\\design";
                    String dungeonFileName = "D:\\work\\dungeon.txt";
                    File dungeonFile = new File(dungeonFileName);
                    if (!useSameDungeonFile) {
                        dungeonFile.delete();
                        try {
                            if (!dungeonFile.createNewFile()) {
                                throw new GdxRuntimeException("can't create file: " + dungeonFile.getPath());
                            }
                        }
                        catch (Exception exc) {
                            throw new GdxRuntimeException("can't create file: " + dungeonFile.getPath(), exc);
                        }
                        dmaker.generateDungeon(designFileName, dungeonFileName);
                        br = Files.newBufferedReader(dungeonFile.toPath());
                    }
                }
                else {
                    FileHandle dungFile = Gdx.files.internal("dungeons/1.txt");
                    br = dungFile.reader(1024);
                }


                int width = 48, height = 48;
                List<String> lines = br.lines().collect(Collectors.toList());
                int mapHeight = lines.size();
                int mapWidth = lines.get(0).split(",").length;

                Node initialNode = new Node(0, 50);
                Node finalNode = new Node(149, 50);

                //AStar aStar = new AStar(mapHeight, mapWidth, initialNode, finalNode);

                lines.forEach(line -> processLine(line, width, height, mapWidth, mapHeight, wallLayer, dirtLayer, skin, rnd));

/*
                if (aStar.findPath().isEmpty()) {
                    count--;
                    continue;
                }
*/

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

        throw new GdxRuntimeException("failed to create playable map");
    };

    void processLine(String line, int width, int height, int mapWidth, int mapHeight, TiledMapTileLayer wallLayer, TiledMapTileLayer dirtLayer, Skin skin, Random rnd) {
        String[] types = line.split(",");
        for (int y = 0; y < types.length; y++ ) {
            int type = Integer.valueOf(types[y]);
            if (type == SquareData.CLOSED.ordinal() || type == SquareData.G_CLOSED.ordinal()) {
                TiledMapTile tile = new StaticTiledMapTile(skin.getRegion("walls/128"));
                int cx = y;
                int cy = mapHeight - x - 1;
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(tile);
                wallLayer.setCell(cx, cy, cell);
                //aStar.setBlock(x, y);
            }
            else if (type == SquareData.IR_OPEN.ordinal()  && y > 5 && y < mapWidth - 6) {
                if (rnd.nextFloat() < 0.9f) {
                    if (rnd.nextFloat() < 0.8f) {
                        TiledMapTile tile = new StaticTiledMapTile(skin.getRegion("walls/126"));
                        int cx = y;
                        int cy = mapHeight - x - 1;
                        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                        cell.setTile(tile);
                        dirtLayer.setCell(cx, cy, cell);
                    }
                    else {
                        TiledMapTile tile = new StaticTiledMapTile(skin.getRegion("walls/stone"));
                        TiledMapTileMapObject o = new TiledMapTileMapObject(tile, false, false);
                        fillBaseProperties(o, x, y, width, height, mapHeight);
                        o.getProperties().put("can_roll", true);
                        o.getProperties().put("can_fall", true);
                        o.getProperties().put("type", "stone");
                        wallLayer.getObjects().add(o);
                    }
                }
                else {
                    if (rnd.nextBoolean()) {
                        Array<TextureRegion> regionArray = skin.getRegions("golem/front");
                        TiledMapTile tile = new StaticTiledMapTile(regionArray.get(0));
                        TiledMapTileMapObject o = new TiledMapTileMapObject(tile, false, false);
                        fillBaseProperties(o, x, y, width, height, mapHeight);
                        o.getProperties().put("type", "golem");
                        o.getProperties().put("has_animations",  true);
                        o.getProperties().put("is_squizable",  true);
                        o.getProperties().put("is_walking", true);
                        o.getProperties().put("is_enemy", true);
                        wallLayer.getObjects().add(o);
                    }
                }
            }
            else if ((type == SquareData.IT_OPEN.ordinal() || type == SquareData.IA_OPEN.ordinal() )  && y > 5 && y < mapWidth - 6) {
                if (rnd.nextFloat() < 0.95f) {
                    if (rnd.nextFloat() < 0.7f) {
                        TiledMapTile tile = new StaticTiledMapTile(skin.getRegion("walls/126"));
                        int cx = y;
                        int cy = mapHeight - x - 1;
                        TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                        cell.setTile(tile);
                        dirtLayer.setCell(cx, cy, cell);
                    }
                    else {
                        TiledMapTile tile = new StaticTiledMapTile(skin.getRegion("walls/stone"));
                        TiledMapTileMapObject o = new TiledMapTileMapObject(tile, false, false);
                        fillBaseProperties(o, x, y, width, height, mapHeight);
                        o.getProperties().put("can_roll", true);
                        o.getProperties().put("can_fall", true);
                        o.getProperties().put("type", "stone");
                        wallLayer.getObjects().add(o);
                    }
                }
            }
            else if (type == SquareData.G_OPEN.ordinal()) {
                {
                    TiledMapTile tile = new StaticTiledMapTile(skin.getRegion("walls/128"));
                    int cx = y;
                    int cy = mapHeight - x - 1;
                    TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                    cell.setTile(tile);
                    wallLayer.setCell(cx, cy, cell);
                }

                if (!deathSpiritPlaced && y < 10 && x > 10) {
                    Array<TextureRegion> regionArray = skin.getRegions("deathspirit/front");
                    TiledMapTile tile = new StaticTiledMapTile(regionArray.get(0));
                    TiledMapTileMapObject o = new TiledMapTileMapObject(tile, false, false);
                    fillBaseProperties(o, x, y + 3, width, height, mapHeight);
                    fillDeathSpiritProps(o);
                    deathSpiritPlaced = true;
                    wallLayer.getObjects().add(o);
                }

                if (!exitPlaced && mapWidth - y < 10 && x > 10) {
                    Array<TextureRegion> regionArray = skin.getRegions("doors/19/door");
                    Array<StaticTiledMapTile> tileArray = new Array<>();
                    regionArray.forEach(region -> tileArray.add(new StaticTiledMapTile(region)));
                    TiledMapTile animatedTile = new AnimatedTiledMapTile(1f, tileArray);
                    TiledMapTileMapObject o = new TiledMapTileMapObject(animatedTile, false, false);
                    fillBaseProperties(o, x, y - 3, width, height, mapHeight);
                    o.getProperties().put("type", "exit");
                    o.getProperties().put(PropertyKeys.ANIMATIONS, regionArray);
                    wallLayer.getObjects().add(o);
                    exitPlaced = true;
                }
            }
        }
        x++;
    }

    void fillBaseProperties(TiledMapTileMapObject o, int x, int y, int width, int height, int mapHeight) {
        o.setX(y * width);
        o.setY((mapHeight - x - 1) * height);
        o.getProperties().put("width", (float) width);
        o.getProperties().put("height", (float) height);
    }

    void fillDeathSpiritProps(TiledMapTileMapObject o) {
        o.getProperties().put("type", "deathspirit");
        o.getProperties().put("name", "deathspirit");
        o.getProperties().put("attach_controller",  true);
        o.getProperties().put("chase_camera",  true);
        o.getProperties().put("has_animations",  true);
        o.getProperties().put("is_squizable",  true);
    }

}
