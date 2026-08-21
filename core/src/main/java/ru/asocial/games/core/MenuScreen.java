package ru.asocial.games.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MenuScreen implements Screen {

    private final Neptun game;
    private final Stage stage;

    public MenuScreen(Neptun game) {
        this.game = game;
        this.stage = new Stage(new ScreenViewport());

        Table table = new Table();
        table.setFillParent(true);

        Label title = new Label("Neptun", game.getResourcesManager().getSkin());
        title.setFontScale(2f);

        Label hint = new Label("D-pad / WASD — move, bomb — right", game.getResourcesManager().getSkin());
        hint.setWrap(true);

        TextButton playButton = new TextButton("Play", game.getResourcesManager().getSkin());
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new GameScreen(game));
            }
        });

        table.add(title).padBottom(24).row();
        table.add(hint).width(400).padBottom(32).row();
        table.add(playButton).width(200).height(56);

        stage.addActor(table);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
