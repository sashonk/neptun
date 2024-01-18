package ru.asocial.games.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.loaders.resolvers.AbsoluteFileHandleResolver;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import ru.asocial.games.core.behaviours.MovingBehavior;

import java.util.Iterator;

public class GameScreen extends BaseScreen {

    private boolean mapLoaded;

    private TiledMapRenderer renderer;

    private TiledMap map;

    private Stage hud;
    private EntityPanel entityPanel;

    private EntityMatrix entityMatrix;


    public GameScreen(IGame game) {
        super(game, 1000, 600);
    }

    public void restart() {
        clear();
        setup();
    }

    public void clear() {
        if (entityMatrix != null) {
            entityMatrix.freeAll();
        }
        getStage().clear();
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

    public void setup() {
        TmxMapLoader loader = new TmxMapLoader(new AbsoluteFileHandleResolver());
        map = loader.load("D:\\work\\tiled\\neptun\\neptun.tmx");

        MapLayer objectLayer = map.getLayers().get("main");
        TiledMapTileLayer wallsLayer = (TiledMapTileLayer) map.getLayers().get("walls");
        TiledMapTileLayer dirtLayer = (TiledMapTileLayer) map.getLayers().get("dirt");

        Layers layers = new Layers(wallsLayer, objectLayer, dirtLayer);

        renderer = new OrthogonalTiledMapRenderer(map, 1f);


        Iterator<MapObject> objectIterator = objectLayer.getObjects().iterator();

        entityMatrix = new EntityMatrix(50, 50, getResourcesManager(), false);
        EntityFactory entityFactory = new EntityFactory(getResourcesManager(), layers, getStage());

        MovingBehavior.setObjectMatrix(entityMatrix);

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
                    restart();
                    return true;
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
            setup();
        } else {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            getStage().getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            renderer.setView((OrthographicCamera) getStage().getCamera());
            renderer.render();

            getStage().act();
            getStage().draw();

            hud.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            hud.act();
            hud.draw();

        }

    }
}
