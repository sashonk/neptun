package ru.asocial.games.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import ru.asocial.games.core.behaviours.EnemyBehavior;
import ru.asocial.games.core.behaviours.MovingBehavior;
import ru.asocial.games.core.behaviours.PlayerBehavior;
import ru.asocial.games.core.dungeon.MapGenerator;
import ru.asocial.games.core.events.*;

import java.util.Iterator;

public class GameScreen extends BaseScreen {

    private boolean mapLoaded;

    private OrthoCachedTiledMapRenderer renderer;

    private TiledMap map;

    private Stage hud;
    private Label playerCoors;
    private Label exitCoors;
    private EntityPanel entityPanel;

    private EntityMatrix entityMatrix;

    private long lastSeed;

    private IMessageService messagingService;

    public GameScreen(IGame game) {
        super(game, 600, 1000);

        this.messagingService = game.getMessagingService();
    }

    public void restart(boolean nextLevel) {
        clear();
        setup(nextLevel);
    }

    public void clear() {
        if (entityMatrix != null) {
            entityMatrix.freeAll();
        }
        getStage().clear();
        getStage().getCamera().position.set(0, 0, 0);
        if (map != null) {
            map.dispose();
        }
        renderer = null;

        hud.clear();
        hud.dispose();
        hud = null;
        entityPanel = null;
        playerCoors = null;
        exitCoors = null;
        mapLoaded = false;
    }

    private void createMapFromDungeonFile(boolean next) {
        MapGenerator mapGenerator = new MapGenerator();
        map = mapGenerator.generateMap(next, getResourcesManager().getSkin(), new MapGenerator.EventHandler() {
            @Override
            public void exitPlaced(int x, int y) {
                exitCoors.setText("exit " + x + ":" + y);
            }

            @Override
            public void playerPlaced(int x, int y) {
                playerCoors.setText("player " + x + ":" + y);
            }
        });
    }

    private void createMapFromTmx() {
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load("map/neptun.tmx");
    }

    public void setup(boolean nextLevel) {
        hud = new Stage();
        entityPanel = new EntityPanel(getResourcesManager().getSkin());
        entityPanel.setPosition(300, 300);
        hud.addActor(entityPanel);
        playerCoors = new Label("n/a", getResourcesManager().getSkin());
        exitCoors = new Label("n/a", getResourcesManager().getSkin());
        exitCoors.setPosition(10, 10);
        playerCoors.setPosition(10, 50);
        hud.addActor(playerCoors);
        hud.addActor(exitCoors);

        WASDPlayerController wasdController = new WASDPlayerController();
        HUDPlayerController hudController = new HUDPlayerController(getResourcesManager().getSkin(), hud.getWidth(), hud.getHeight());
        hudController.setPosition(hud.getWidth() / 2, 200);
        hud.addActor(hudController);
        hud.addActor(wasdController);
        hud.addListener(new InputListener(){
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                if (y > hud.getHeight() / 2) {
                    Entity player = getStage().getRoot().findActor("deathspirit");
                    if (player != null) {
                        getStage().getCamera().position.set(player.getX(), player.getY(), 1);
                    }
                }
                return false;
            }
        });

        createMapFromDungeonFile(nextLevel);

        //MapLayer objectLayer = map.getLayers().get("walls");
        TiledMapTileLayer wallsLayer = (TiledMapTileLayer) map.getLayers().get("walls");
        TiledMapTileLayer dirtLayer = (TiledMapTileLayer) map.getLayers().get("dirt");

        Layers layers = new Layers(wallsLayer, wallsLayer, dirtLayer);

        renderer = new OrthoCachedTiledMapRenderer(map, 1f);

        Iterator<MapObject> objectIterator = wallsLayer.getObjects().iterator();

        Preferences prefs = Gdx.app.getPreferences("neptun");
        entityMatrix = new EntityMatrix(500, 500, getResourcesManager(), prefs.getBoolean("debug"));
        EntityFactory entityFactory = new EntityFactory(getResourcesManager(), layers, getStage(), messagingService);

        MovingBehavior.setObjectMatrix(entityMatrix);
        MovingBehavior.TileLayerChangedListener tileLayerChangedListener = () -> renderer.invalidateCache();
        MovingBehavior.setTileLayerChangedListener(tileLayerChangedListener);

        EnemyBehavior.setMatrix(entityMatrix);

        while (objectIterator.hasNext()) {
            MapObject object = objectIterator.next();
            Entity entity = entityFactory.create(object);
            entityMatrix.take((int) entity.getX() / (int) entity.getWidth(),(int) entity.getY() / (int) entity.getHeight(), entity);
            if (object.getProperties().get(PropertyKeys.ATTACH_CONTROLLER, false, Boolean.class)) {
                PlayerBehavior behavior = new PlayerBehavior(layers);
                behavior.setController(new CombinedPlayerController(hudController, wasdController));
                FileHandle movesFile = Gdx.files.absolute("D:\\work\\moves.txt");
                behavior.setMoveCallback(move -> movesFile.writeString(move.toString() + "\r\n", true));
                entity.addBehaviour(behavior);
            }
            if ("player".equals(entity.getProperty(PropertyKeys.TYPE, String.class))) {
                entity.putProperty("controller", hudController);
            }

            getStage().addActor(entity);
        }


        getStage().addActor(entityMatrix);

        // getStage().getRoot().setTouchable(Touchable.childrenOnly);

        Actor player = getStage().getRoot().findActor("deathspirit");
        if (player != null) {
            getStage().getCamera().position.set(player.getX(), player.getY(), 1);
        }

        getStage().addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof RestartEvent) {
                    Action delay = Actions.delay(1, new Action() {
                        @Override
                        public boolean act(float delta) {
                            GameScreen.this.restart(((RestartEvent) event).isNextLvl());
                            return true;
                        }
                    });
                    getStage().addAction(delay);
                }
                if (event instanceof ExplodeEntityEvent) {
                    Entity explosive = (Entity) event.getTarget();
                    explosive.putProperty("is_exploding", true);
                    boolean needInvalidateCache = false;
                    for (int i = -1 ; i < 2; i++) {
                        for (int j = -1; j < 2; j++) {
                            Entity e1 = EntityMatrixUtils.getWithOffset(entityMatrix, explosive, i, j);
                            if (e1 != null) {
                                if (e1.getPropertyOrDefault(PropertyKeys.IS_EXPLOSIVE, Boolean.class, false)
                                        && !e1.getPropertyOrDefault("is_exploding", Boolean.class, false)) {
                                    e1.fire(new ExplodeEntityEvent());
                                    continue;
                                }

                                EntityMatrixUtils.freeObject(entityMatrix, e1);

                                e1.addAction(Actions.removeActor());
                                e1.fire(new DestroyEntityEvent());
                                //e1.fire(new RemoveEntityEvent());
                            }
                            else {
                                if (CellUtils.isDirtAtCell(dirtLayer, explosive, i, j)) {
                                    GridPoint2 cellXY = CellUtils.getCellCoors(explosive, i, j);
                                    dirtLayer.setCell(cellXY.x, cellXY.y, null);
                                    needInvalidateCache = true;
                                }
                            }

                            Entity explosion = entityFactory.newExplosion(explosive, i, j);
                            getStage().addActor(explosion);
                        }
                    }

                    explosive.addAction(Actions.removeActor());
                    explosive.fire(new DestroyEntityEvent());
                    EntityMatrixUtils.freeObject(entityMatrix, explosive);

                    if (needInvalidateCache) {
                        renderer.invalidateCache();
                    }
                }
                else if (event instanceof DestroyEntityEvent) {
                    Entity entity = (Entity) event.getTarget();
                    if ("player".equals(entity.getProperty(PropertyKeys.TYPE, String.class)) ) {
                        //entity.setRotation();
                        getStage().getRoot().fire(new RestartEvent(entity, false));
                    }
                }
                else if (event instanceof PlayerKilledEvent) {
                    PlayerKilledEvent playerKilledEvent = (PlayerKilledEvent) event;
                    Entity e = playerKilledEvent.getVictim();
                    //e.setRotation(90);
                    e.addAction(Actions.rotateBy(90, 1));
                    e.putProperty(PropertyKeys.IS_MOVING, false);
                    e.removeBehaviours(PlayerBehavior.class);
                    e.putProperty("is_dead", true);
                    e.fire(new RestartEvent(e, false));
                }
                else if (event instanceof PlaceBombEvent) {
                    PlaceBombEvent placeBombEvent = (PlaceBombEvent) event;
                    Entity e = (Entity) placeBombEvent.getTarget();
                    Entity bomb = entityFactory.newBomb(e);
                    getStage().addActor(bomb);
                }
                else if (event instanceof MoveEvent) {
                    MoveEvent moveEvent = (MoveEvent) event;
                    Entity entity = (Entity) moveEvent.getTarget();
                    if ("player".equals(entity.getProperty(PropertyKeys.TYPE, String.class)) ) {
                        //entity.setRotation();
                        playerCoors.setText("player " + moveEvent.getX() + ":" + moveEvent.getY());

                    }
                }
                return false;
            }
        });

        InputMultiplexer p = new InputMultiplexer();
        p.addProcessor(hud);
        p.addProcessor(getStage());
        Gdx.input.setInputProcessor(p);

        mapLoaded = true;

    }

    public void render(float delta) {
        if (!mapLoaded) {
            setup(false);
        } else {
            Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            getStage().act();
            getStage().getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            renderer.setView((OrthographicCamera) getStage().getCamera());

            renderer.render();

            //getStage().setC
            getStage().draw();

            hud.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            hud.act();
            hud.draw();

        }

    }
}
