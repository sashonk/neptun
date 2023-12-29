package ru.asocial.games.html;

import ru.asocial.games.core.MarsRemake;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class MarsRemakeHtml extends GwtApplication {
	@Override
	public ApplicationListener getApplicationListener () {
		return new MarsRemake();
	}
	
	@Override
	public GwtApplicationConfiguration getConfig () {
		return new GwtApplicationConfiguration(480, 320);
	}
}
