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
import com.badlogic.gdx.utils.Align;
import ru.asocial.games.core.behaviours.EnemyBehavior;
import ru.asocial.games.core.behaviours.MovingBehavior;
import ru.asocial.games.core.behaviours.PlayerBehavior;
import ru.asocial.games.core.behaviours.WalkingBehaviour;
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

    private EntityFactory entityFactory;

    private final MapGenerator mapGenerator = new MapGenerator();
    private long levelSeed;

    private final FrameProfiler frameProfiler = new FrameProfiler();
    private boolean metricsEnabled;
    private Label metricsLabel;

    private boolean levelRestartScheduled;

    public GameScreen(IGame game) {
        super(game, 600, 1000);
    }

    public void restart(boolean nextLevel) {
        clear();
        setup(nextLevel);
    }

    public void clear() {
        levelRestartScheduled = false;
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
        metricsLabel = null;
        mapLoaded = false;
    }

    private void createMapFromDungeonFile(boolean nextLevel) {
        if (nextLevel || levelSeed == 0) {
            levelSeed = System.nanoTime();
        }

        Preferences prefs = Gdx.app.getPreferences("neptun");
        if (prefs.getBoolean("map300") || Neptun.hasLaunchArg("map300")) {
            mapGenerator.setFixedDungeonFile("dungeons/300.txt", 300);
        } else if (prefs.getBoolean("map150") || Neptun.hasLaunchArg("map150")) {
            mapGenerator.setFixedDungeonFile("dungeons/150.txt", 150);
        } else if (prefs.getBoolean("map40") || Neptun.hasLaunchArg("map40")) {
            mapGenerator.setFixedDungeonFile("dungeons/40.txt", 40);
        }
        map = mapGenerator.generateMap(nextLevel, levelSeed, getResourcesManager().getSkin(), new MapGenerator.EventHandler() {
            @Override
            public void exitPlaced(int x, int y) {
                exitCoors.setText("exit " + x + ":" + y);
            }

            @Override
            public void playerPlaced(int x, int y) {
                playerCoors.setText("player " + x + ":" + y + " seed " + levelSeed);
            }
        });
        levelSeed = mapGenerator.getCurrentSeed();
        Gdx.app.log("GameScreen", "level seed: " + levelSeed);
    }

    private void createMapFromTmx() {
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load("map/neptun.tmx");
    }

    public void setup(boolean nextLevel) {
        hud = new Stage();
        hud.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
        boolean map300 = prefs.getBoolean("map300") || Neptun.hasLaunchArg("map300");
        boolean map150 = prefs.getBoolean("map150") || Neptun.hasLaunchArg("map150");
        boolean map40 = prefs.getBoolean("map40") || Neptun.hasLaunchArg("map40");
        metricsEnabled = prefs.getBoolean("metrics");
        if (metricsEnabled) {
            metricsLabel = new Label("metrics...", getResourcesManager().getSkin());
            metricsLabel.setAlignment(Align.topLeft);
            layoutMetricsLabel();
            hud.addActor(metricsLabel);
            metricsLabel.toFront();
            Gdx.app.log("GameScreen", "metrics overlay enabled");
        }

        int matrixSize = map300 ? 350 : map150 ? 200 : map40 ? 90 : 500;
        entityMatrix = new EntityMatrix(matrixSize, matrixSize, getResourcesManager(), prefs.getBoolean("debug"));
        entityFactory = new EntityFactory(getResourcesManager(), layers, getStage());

        MovingBehavior.setObjectMatrix(entityMatrix);
        MovingBehavior.TileLayerChangedListener tileLayerChangedListener = this::invalidateMapCache;
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
                    RestartEvent restartEvent = (RestartEvent) event;
                    scheduleLevelRestart(restartEvent.isNextLvl(), restartEvent.getRestartDelay());
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

                                if ("player".equals(e1.getProperty(PropertyKeys.TYPE, String.class))) {
                                    if (e1.getPropertyOrDefault("is_dead", Boolean.class, false)
                                            || e1.getPropertyOrDefault("is_capturing", Boolean.class, false)) {
                                        continue;
                                    }
                                    EntityMatrixUtils.freeObject(entityMatrix, e1);
                                    playExplosionDeath(e1, explosive);
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
                        invalidateMapCache();
                    }
                }
                else if (event instanceof DestroyEntityEvent) {
                    DestroyEntityEvent destroyEvent = (DestroyEntityEvent) event;
                    Entity entity = (Entity) event.getTarget();
                    if ("player".equals(entity.getProperty(PropertyKeys.TYPE, String.class))) {
                        if (entity.getPropertyOrDefault("is_capturing", Boolean.class, false)) {
                            return false;
                        }
                        if (destroyEvent.isSquized()) {
                            playSquishDeath(entity, destroyEvent.getRelatedEntity());
                            return false;
                        }
                        float delay = entity.getPropertyOrDefault("is_dead", Boolean.class, false) ? 0.2f : 1f;
                        scheduleLevelRestart(false, delay);
                    }
                }
                else if (event instanceof PlayerKilledEvent) {
                    PlayerKilledEvent playerKilledEvent = (PlayerKilledEvent) event;
                    Entity player = playerKilledEvent.getVictim();
                    if (player.getPropertyOrDefault("is_dead", Boolean.class, false)
                            || player.getPropertyOrDefault("is_capturing", Boolean.class, false)) {
                        return false;
                    }
                    playGolemCaptureDeath(player, playerKilledEvent.getKiller());
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

        frameProfiler.setMapSize(wallsLayer.getWidth(), wallsLayer.getHeight());
        frameProfiler.setMatrixSize(matrixSize, matrixSize);

        mapLoaded = true;

    }

    private void playGolemCaptureDeath(Entity player, Entity golem) {
        player.putProperty("is_capturing", true);
        stopEntityMotion(player);
        player.removeBehaviours(PlayerBehavior.class);
        player.clearActions();

        float cellW = player.getWidth();
        float cellH = player.getHeight();

        if (golem != null) {
            stopEntityMotion(golem);
            golem.removeBehaviours(WalkingBehaviour.class);
            golem.removeBehaviours(EnemyBehavior.class);
            golem.clearActions();

            snapCapturePair(player, golem, cellW, cellH);

            EntityOrientation towardPlayer = orientationToward(golem.getX(), golem.getY(), player.getX(), player.getY());
            golem.putProperty(PropertyKeys.ORIENTATION, towardPlayer.name());
            golem.putProperty(PropertyKeys.IS_ANIMATION_RUNNING, false);
            golem.putProperty(PropertyKeys.ANIMATION_STATE_TIME, 0f);

            golem.addAction(Actions.sequence(
                    Actions.scaleTo(1.08f, 1.08f, 0.1f),
                    Actions.scaleTo(1f, 1f, 0.55f)
            ));
        }

        player.putProperty(PropertyKeys.IS_ANIMATION_RUNNING, false);
        player.putProperty(PropertyKeys.ANIMATION_STATE_TIME, 0f);

        float shakeX = 0f;
        float shakeY = 0f;
        if (golem != null) {
            float dx = golem.getX() - player.getX();
            float dy = golem.getY() - player.getY();
            if (Math.abs(dx) >= Math.abs(dy)) {
                shakeX = Math.signum(dx) * 6f;
            } else {
                shakeY = Math.signum(dy) * 6f;
            }
        } else {
            shakeX = 6f;
        }

        Action shake = Actions.repeat(4, Actions.sequence(
                Actions.moveBy(shakeX, shakeY, 0.05f),
                Actions.moveBy(-shakeX, -shakeY, 0.05f)
        ));

        player.addAction(Actions.sequence(
                shake,
                Actions.rotateBy(90, 0.3f),
                Actions.run(() -> finishPlayerDeath(player, false, 0.2f))
        ));
    }

    private void playExplosionDeath(Entity player, Entity epicenter) {
        player.putProperty("is_capturing", true);
        stopEntityMotion(player);
        player.removeBehaviours(PlayerBehavior.class);
        player.clearActions();
        player.getColor().a = 1f;
        player.setScale(1f, 1f);
        player.setRotation(0f);
        Entity flash = entityFactory.newExplosionAt(player.getX(), player.getY(), 0.55f);
        getStage().addActor(flash);
        player.toFront();

        float dx = player.getX() - epicenter.getX();
        float dy = player.getY() - epicenter.getY();
        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len < 1f) {
            dx = 0f;
            dy = 1f;
            len = 1f;
        }
        float knock = 56f;
        float kx = dx / len * knock;
        float ky = dy / len * knock;

        player.addAction(Actions.sequence(
                Actions.scaleTo(1.25f, 1.25f, 0.12f),
                Actions.parallel(
                        Actions.moveBy(kx, ky, 0.4f),
                        Actions.sequence(
                                Actions.delay(0.08f),
                                Actions.fadeOut(0.65f)
                        ),
                        Actions.rotateBy(180, 0.45f),
                        Actions.sequence(
                                Actions.delay(0.25f),
                                Actions.scaleTo(0.5f, 0.5f, 0.35f)
                        )
                ),
                Actions.run(() -> finishPlayerDeath(player, false, 0.35f))
        ));
    }

    private void playSquishDeath(Entity player, Entity stone) {
        player.putProperty("is_capturing", true);
        stopEntityMotion(player);
        player.removeBehaviours(PlayerBehavior.class);
        player.clearActions();
        player.getColor().a = 1f;
        player.setRotation(0f);
        player.setScale(1f, 1f);
        player.putProperty(PropertyKeys.IS_ANIMATION_RUNNING, false);

        float cellW = player.getWidth();
        float cellH = player.getHeight();
        if (stone != null) {
            player.setPosition(stone.getX(), stone.getY() - cellH);
            player.toBack();
        }

        player.addAction(Actions.sequence(
                Actions.parallel(
                        Actions.scaleTo(1.4f, 0.18f, 0.14f),
                        Actions.moveBy(0f, -6f, 0.14f)
                ),
                Actions.delay(0.5f),
                Actions.run(() -> finishPlayerDeath(player, false, 0.3f))
        ));
    }

    private void finishPlayerDeath(Entity player, boolean nextLevel, float restartDelay) {
        player.putProperty("is_dead", true);
        player.removeProperty("is_capturing");
        scheduleLevelRestart(nextLevel, restartDelay);
    }

    private void scheduleLevelRestart(boolean nextLevel, float delay) {
        if (levelRestartScheduled) {
            return;
        }
        levelRestartScheduled = true;
        final boolean next = nextLevel;
        getStage().addAction(Actions.sequence(
                Actions.delay(delay),
                Actions.run(() -> {
                    if (!levelRestartScheduled) {
                        return;
                    }
                    levelRestartScheduled = false;
                    GameScreen.this.restart(next);
                })
        ));
    }

    private void stopEntityMotion(Entity entity) {
        entity.putProperty(PropertyKeys.IS_MOVING, false);
        entity.putProperty(PropertyKeys.IS_ANIMATION_RUNNING, false);
        entity.putProperty(PropertyKeys.MOVE_PROGRESS, 0f);
        entity.removeProperty(PropertyKeys.MOVING_TO);
        entity.removeProperty(PropertyKeys.MOVE_FROM);
    }

    /**
     * Aligns sprites on adjacent cell centers. Needed because grid logic can place the player
     * one cell ahead while the move tween is still in progress.
     */
    private void snapCapturePair(Entity player, Entity golem, float cellW, float cellH) {
        int golemCx = Math.round(golem.getX() / cellW);
        int golemCy = Math.round(golem.getY() / cellH);
        int playerCx = Math.round(player.getX() / cellW);
        int playerCy = Math.round(player.getY() / cellH);

        int dx = Integer.compare(playerCx, golemCx);
        int dy = Integer.compare(playerCy, golemCy);
        if (dx != 0 && dy != 0) {
            if (Math.abs(playerCx - golemCx) >= Math.abs(playerCy - golemCy)) {
                dy = 0;
            } else {
                dx = 0;
            }
        }
        if (dx == 0 && dy == 0) {
            dx = 1;
        }

        golem.setPosition(golemCx * cellW, golemCy * cellH);
        player.setPosition((golemCx + dx) * cellW, (golemCy + dy) * cellH);
    }

    private EntityOrientation orientationToward(float fromX, float fromY, float toX, float toY) {
        float dx = toX - fromX;
        float dy = toY - fromY;
        if (Math.abs(dx) >= Math.abs(dy)) {
            return EntityOrientation.fromMoveDirection(dx >= 0 ? EntityMove.RIGHT : EntityMove.LEFT);
        }
        return EntityOrientation.fromMoveDirection(dy >= 0 ? EntityMove.UP : EntityMove.DOWN);
    }

    private void invalidateMapCache() {
        renderer.invalidateCache();
        frameProfiler.recordCacheInvalidation();
    }

    private void layoutMetricsLabel() {
        if (metricsLabel == null) {
            return;
        }
        float hudHeight = hud.getViewport().getWorldHeight();
        metricsLabel.setPosition(10, hudHeight - 10);
    }

    public void render(float delta) {
        if (!mapLoaded) {
            setup(false);
        } else {
            Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            frameProfiler.beginAct();
            getStage().act();
            frameProfiler.endAct();

            getStage().getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            renderer.setView((OrthographicCamera) getStage().getCamera());

            frameProfiler.beginMapRender();
            renderer.render();
            frameProfiler.endMapRender();

            frameProfiler.beginStageDraw();
            DrawCuller.begin((OrthographicCamera) getStage().getCamera());
            getStage().draw();
            frameProfiler.endStageDraw();

            hud.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

            frameProfiler.beginHud();
            hud.act();
            if (metricsEnabled && metricsLabel != null) {
                layoutMetricsLabel();
                metricsLabel.setText(frameProfiler.formatOverlay());
                metricsLabel.toFront();
            }
            hud.draw();
            frameProfiler.endHud();

            frameProfiler.endFrame(delta, getStage().getRoot().getChildren().size,
                    DrawCuller.getVisibleCount(), DrawCuller.getCulledCount());

        }

    }
}
