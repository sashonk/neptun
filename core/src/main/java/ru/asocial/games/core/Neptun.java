package ru.asocial.games.core;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;

public class Neptun extends Game implements IGame{

	private Map<String, BaseScreen> screens = new HashMap<>();

	private Graphics.DisplayMode displayMode;

	private ResourcesManager resourcesManager;


	private IMessagingService messagingService;

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
		Gdx.graphics.setWindowedMode(1200, 900);

		//Gdx.graphics.setWindowedMode(1000, 1000);

		setScreen(new SplashScreen(this));

		ServiceLoader<IMessagingService> serviceLoader = ServiceLoader.load(IMessagingService.class);
		Iterator<IMessagingService> messagingServiceIterator = serviceLoader.iterator();
		messagingService = messagingServiceIterator.hasNext() ? messagingServiceIterator.next() : new IMessagingService() {
			@Override
			public void writeMessage(String tag, String message) {
				System.out.println(tag + ":" + message);
			}

			@Override
			public void close() {
				//NOOP
			}
		};
	}

	@Override
	public void dispose() {
		if (resourcesManager != null) {
			resourcesManager.dispose();
		}

		for (Screen screen : screens.values()) {
			screen.dispose();
		}

		messagingService.close();

		Gdx.app.exit();
	}

	@Override
	public void onLoad() {
		Screen exaustedScreen = super.getScreen();
		exaustedScreen.dispose();
		setScreen(new GameScreen(this));
	}

	@Override
	public IMessagingService getMessagingService() {
		return messagingService;
	}

}
