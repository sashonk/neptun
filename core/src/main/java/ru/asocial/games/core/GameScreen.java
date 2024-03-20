package ru.asocial.games.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import ru.asocial.games.core.behaviours.EnemyBehavior;
import ru.asocial.games.core.behaviours.MovingBehavior;
import ru.asocial.games.core.dungeon.MapGenerator;
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

    public void restart(boolean useSameDungFile) {
        clear();
        setup(useSameDungFile);
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

    private void createMapFromDungeonFile(boolean useSameDungeonFile) {
        MapGenerator mapGenerator = new MapGenerator(false);
        map = mapGenerator.generateMap(useSameDungeonFile, getResourcesManager().getSkin());
    }

    private void createMapFromTmx() {
        TmxMapLoader loader = new TmxMapLoader();
        map = loader.load("map/neptun.tmx");
    }

    public void setup(boolean useSameDungeonFile) {

        createMapFromDungeonFile(useSameDungeonFile);

        //MapLayer objectLayer = map.getLayers().get("walls");
        TiledMapTileLayer wallsLayer = (TiledMapTileLayer) map.getLayers().get("walls");
        TiledMapTileLayer dirtLayer = (TiledMapTileLayer) map.getLayers().get("dirt");

        Layers layers = new Layers(wallsLayer, wallsLayer, dirtLayer);

        renderer = new OrthoCachedTiledMapRenderer(map, 1f);

        Iterator<MapObject> objectIterator = wallsLayer.getObjects().iterator();

        entityMatrix = new EntityMatrix(200, 200, getResourcesManager(), false);
        EntityFactory entityFactory = new EntityFactory(getResourcesManager(), layers, getStage(), messagingService);

        MovingBehavior.setObjectMatrix(entityMatrix);
        MovingBehavior.setTileLayerChangedListener(new MovingBehavior.TileLayerChangedListener() {
            @Override
            public void onTileLayerChanged() {
                renderer.invalidateCache();
            }
        });

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

        getStage().getRoot().addListener(new InputListener(){

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
                        String onTouch = entity.getProperty(PropertyKeys.ON_TOUCH, String.class);
                        if ("roll".equals(onTouch)) {
                            entity.putProperty(PropertyKeys.IS_ROLLING, true);
                        }
                    }
                    System.out.println(event.getTarget());
                    return true;
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

        getStage().addListener(new InputListener(){
            public boolean keyDown (InputEvent event, int keycode) {
                if (keycode == Input.Keys.F2) {
                    restart(true);
                    return true;
                }
                return false;
            }
        });
        getStage().addListener(new EventListener() {
            @Override
            public boolean handle(Event event) {
                if (event instanceof RestartEvent) {
                    RestartEvent restartEvent = (RestartEvent) event;
                    Entity player = restartEvent.getPlayer();
                    player.addAction(Actions.removeActor());
                    int px = (int) player.getX() / (int) player.getWidth();
                    int py = (int) player.getY() / (int) player.getHeight();
                    entityMatrix.free(px, py);
                    Action delay = Actions.delay(1, new Action() {
                        @Override
                        public boolean act(float delta) {
                            GameScreen.this.restart(!((RestartEvent) event).isNextLvl());
                            return true;
                        }
                    });
                    getStage().addAction(delay);
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
