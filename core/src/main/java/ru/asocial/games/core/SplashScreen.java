package ru.asocial.games.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ScalingViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SplashScreen implements Screen {

	private ResourcesManager _manager;

	private Stage stage;

	private IGame game;
	
	public SplashScreen(IGame game) {
		this.game = game;
		Viewport viewport = new ScalingViewport(Scaling.stretch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.stage = new Stage(viewport);

/*		Texture splash = new Texture(Gdx.files.internal("logo.png"));
		TextureRegion tr = new TextureRegion(splash, 0, 0, 1280, 720);
		Image im = new Image(tr);
		im.setFillParent(true);
		stage.addActor(im);*/

		 _manager = new ResourcesManager();

		 _manager.loadResources();
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {
		if(!_manager.update()){
			stage.act();
			stage.draw();
		}
		else{
			_manager.finishLoading();
			
			_manager.init();

			 game.setResourceManager(_manager);

			game.onLoad();
		}

	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height);
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
