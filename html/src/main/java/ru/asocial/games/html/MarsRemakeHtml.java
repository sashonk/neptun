package ru.asocial.games.html;

import ru.asocial.games.core.Neptun;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;

public class NeptunHtml extends GwtApplication {
	@Override
	public ApplicationListener getApplicationListener () {
		return new Neptun();
	}
	
	@Override
	public GwtApplicationConfiguration getConfig () {
		return new GwtApplicationConfiguration(480, 320);
	}
}
