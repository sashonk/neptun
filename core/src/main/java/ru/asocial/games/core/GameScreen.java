package ru.asocial.games.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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


    public GameScreen(IGame game) {
        super(game, 1000, 500);
    }

    public void render(float delta) {
        if (!mapLoaded) {
            TmxMapLoader loader = new TmxMapLoader();
            map = loader.load("map/neptun.tmx");

            MapLayer objectLayer = map.getLayers().get("main");
            TiledMapTileLayer wallsLayer = (TiledMapTileLayer) map.getLayers().get("walls");
            TiledMapTileLayer dirtLayer = (TiledMapTileLayer) map.getLayers().get("dirt");

            Layers layers = new Layers(wallsLayer, objectLayer, dirtLayer);

            renderer = new OrthogonalTiledMapRenderer(map, 1f);


            Iterator<MapObject> objectIterator = objectLayer.getObjects().iterator();

            EntityMatrix entityMatrix = new EntityMatrix(50, 50, getResourcesManager());
            EntityFactory entityFactory = new EntityFactory(getResourcesManager(), layers, getStage(), entityMatrix);

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


            hud = new Stage();
            hud.addActor(entityPanel);

            mapLoaded = true;
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

            //ui.act();
            //ui.draw();
        }

    }
}
