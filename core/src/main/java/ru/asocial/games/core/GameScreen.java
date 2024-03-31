package ru.asocial.games.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import ru.asocial.games.core.behaviours.EnemyBehavior;
import ru.asocial.games.core.behaviours.MovingBehavior;
import ru.asocial.games.core.dungeon.MapGenerator;
import ru.asocial.games.core.events.RemoveEntityEvent;
import ru.asocial.games.core.events.ExplodeEntityEvent;
import ru.asocial.games.core.events.RestartEvent;

import java.util.Iterator;

public class GameScreen extends BaseScreen {

    private boolean mapLoaded;

    private OrthoCachedTiledMapRenderer renderer;

    private TiledMap map;

    private Stage hud;
    private EntityPanel entityPanel;

    private EntityMatrix entityMatrix;

    private long lastSeed;

    private IMessageService messagingService;

    public GameScreen(IGame game) {
        super(game, 1000, 600);

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
        mapLoaded = false;
    }

    private void createMapFromDungeonFile(boolean next) {
        MapGenerator mapGenerator = new MapGenerator();
        map = mapGenerator.generateMap(next, getResourcesManager().getSkin());
    }

    private void createMapFromTmx() {
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load("map/neptun.tmx");
    }

    public void setup(boolean nextLevel) {

        createMapFromDungeonFile(nextLevel);

        //MapLayer objectLayer = map.getLayers().get("walls");
        TiledMapTileLayer wallsLayer = (TiledMapTileLayer) map.getLayers().get("walls");
        TiledMapTileLayer dirtLayer = (TiledMapTileLayer) map.getLayers().get("dirt");

        Layers layers = new Layers(wallsLayer, wallsLayer, dirtLayer);

        renderer = new OrthoCachedTiledMapRenderer(map, 1f);

        Iterator<MapObject> objectIterator = wallsLayer.getObjects().iterator();

        Preferences prefs = Gdx.app.getPreferences("neptun");
        entityMatrix = new EntityMatrix(200, 200, getResourcesManager(), prefs.getBoolean("debug"));
        EntityFactory entityFactory = new EntityFactory(getResourcesManager(), layers, getStage(), messagingService);

        MovingBehavior.setObjectMatrix(entityMatrix);
        MovingBehavior.TileLayerChangedListener tileLayerChangedListener = () -> renderer.invalidateCache();
        MovingBehavior.setTileLayerChangedListener(tileLayerChangedListener);

        EnemyBehavior.setMatrix(entityMatrix);

        while (objectIterator.hasNext()) {
            MapObject object = objectIterator.next();
            Entity entity = entityFactory.create(object);
            entityMatrix.take((int) entity.getX() / (int) entity.getWidth(),(int) entity.getY() / (int) entity.getHeight(), entity);

            getStage().addActor(entity);
        }

        getStage().addActor(entityMatrix);

        getStage().getRoot().setTouchable(Touchable.childrenOnly);

        entityPanel = new EntityPanel(getResourcesManager().getSkin());
        entityPanel.setPosition(300, 300);

        Actor player = getStage().getRoot().findActor("deathspirit");
        if (player != null) {
            getStage().getCamera().position.set(player.getX(), player.getY(), 1);
        }

        getStage().getRoot().addListener(new InputListener() {

            public boolean scrolled (InputEvent event, float x, float y, float amountX, float amountY) {
                if (amountY > 0) {
                    Vector3 pos = getStage().getCamera().position;
                    getStage().getCamera().translate(pos.x, pos.y, pos.z + 0.1f);
                    return true;
                }
                else if (amountY < 0){
                    Vector3 pos = getStage().getCamera().position;
                    getStage().getCamera().translate(pos.x, pos.y, pos.z - 0.1f);
                    return false;
                }
                return false;
            }
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {

                if (button == Input.Buttons.LEFT) {
                    if (event.getTarget() instanceof Entity) {

                        Entity entity = (Entity) event.getTarget();
                        entity.putProperty("test", true);
                        return true;
                    }
                    return false;
                }
                else if (button == Input.Buttons.RIGHT) {
                    if (event.getTarget() instanceof Entity) {
                        Entity entity = (Entity) event.getTarget();
                        entityPanel.clearActions();
                        entityPanel.addAction(Actions.forever(Actions.sequence(new Action() {
                            @Override
                            public boolean act(float delta) {
                                entityPanel.setVisible(true);
                                entityPanel.init(entity);
                                return true;
                            }
                        }, Actions.delay(0.2f))));

                    }
                    return true;
                }

                return false;
            }
        });

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
                                e1.fire(new RemoveEntityEvent());
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
                    explosive.fire(new RemoveEntityEvent());
                    EntityMatrixUtils.freeObject(entityMatrix, explosive);

                    if (needInvalidateCache) {
                        renderer.invalidateCache();
                    }
                }
                else if (event instanceof RemoveEntityEvent) {
                    Entity entity = (Entity) event.getTarget();
                    if ("deathspirit".equals(entity.getProperty(PropertyKeys.TYPE, String.class)) ) {
                        getStage().getRoot().fire(new RestartEvent(entity, false));
                    }
                }
                return false;
            }
        });

        hud = new Stage();
        hud.addActor(entityPanel);
        mapLoaded = true;
    }

    public void render(float delta) {
        if (!mapLoaded) {
            setup(false);
        } else {
            Gdx.gl.glClearColor(0, 0, 0, 1);
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
