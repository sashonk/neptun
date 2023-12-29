package ru.asocial.games.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;

import java.util.HashMap;
import java.util.Map;

public class MarsRemake extends Game implements IGame{

	private Map<String, BaseScreen> screens = new HashMap<>();

	private Graphics.DisplayMode displayMode;

	private ResourcesManager resourcesManager;

	@Override
	public ResourcesManager getResourcesManager() {
		return resourcesManager;
	}

	@Override
	public void setResourceManager(ResourcesManager resourcesManager) {
		this.resourcesManager = resourcesManager;
	}

	public MarsRemake(Graphics.DisplayMode displayMode) {
		this.displayMode = displayMode;
	}

	public MarsRemake() {

	}

	@Override
	public void create() {
		Gdx.graphics.setWindowedMode(1200, 900);

		//Gdx.graphics.setWindowedMode(1000, 1000);

		setScreen(new SplashScreen(this));

	}

	@Override
	public void dispose() {
		if (resourcesManager != null) {
			resourcesManager.dispose();
		}

		for (Screen screen : screens.values()) {
			screen.dispose();
		}

		Gdx.app.exit();
	}

	@Override
	public void onLoad() {
		Screen exaustedScreen = super.getScreen();
		exaustedScreen.dispose();
		setScreen(new GameScreen(this));
	}

}
