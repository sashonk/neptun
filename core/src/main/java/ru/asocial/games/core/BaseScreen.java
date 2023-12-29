package ru.asocial.games.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import ru.asocial.games.core.IGame;
import ru.asocial.games.core.ResourcesManager;

public class BaseScreen implements Screen {

    private final Stage stage;
    private final ResourcesManager resourcesManager;

    protected static final String LOG_TAG = "Screen";

    protected Stage getStage() {
        return stage;
    }

    protected ResourcesManager getResourcesManager() {
        return resourcesManager;
    }

    public BaseScreen(IGame game, float worldWidth, float worldHeight) {
        Viewport viewport = new ScalingViewport(Scaling.fit, worldWidth, worldHeight);
        this.stage = new Stage(viewport);
        this.resourcesManager = game.getResourcesManager();
    }

    public BaseScreen(IGame game, float worldWidth, float worldHeight, Batch batch) {
        Viewport viewport = new ScalingViewport(Scaling.fit, worldWidth, worldHeight);
        this.stage = new Stage(viewport, batch);
        this.resourcesManager = game.getResourcesManager();
    }

    @Override
    public void show() {
        Gdx.app.debug(LOG_TAG, toString() + " " + "show");

        Gdx.input.setInputProcessor(getStage());
    }

    @Override
    public void render(float delta) {
        Gdx.app.debug(LOG_TAG, toString() + "render");
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        stage.act();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.debug(LOG_TAG, toString() + " " + "resize " + width + "x" + height);
        stage.getViewport().update(width, height, true);

    }

    @Override
    public void pause() {
        Gdx.app.debug(LOG_TAG, toString()+ "pause");
    }

    @Override
    public void resume() {
        Gdx.app.debug(LOG_TAG, toString()+ "resume");
    }

    @Override
    public void hide() {
        Gdx.app.debug(LOG_TAG, "hide");
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
