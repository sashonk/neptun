package ru.asocial.games.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;

import java.util.HashMap;
import java.util.Map;

public class Neptun extends Game implements IGame{

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

	public Neptun(Graphics.DisplayMode displayMode) {
		this.displayMode = displayMode;
	}

	public Neptun() {

	}

	@Override
	public void create() {
		Gdx.graphics.setWindowedMode(600, 1000);

		setScreen(new SplashScreen(this));

		Preferences keyboard = Gdx.app.getPreferences("keyboard");
		keyboard.putInteger("up", Input.Keys.W);
		keyboard.putInteger("left", Input.Keys.A);
		keyboard.putInteger("down", Input.Keys.S);
		keyboard.putInteger("right", Input.Keys.D);
		keyboard.putInteger("action", Input.Keys.SPACE);

		Preferences neptun = Gdx.app.getPreferences("neptun");
		//neptun.putBoolean("replay", true);

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
