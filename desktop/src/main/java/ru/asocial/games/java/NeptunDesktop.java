package ru.asocial.games.java;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import ru.asocial.games.core.Neptun;

import java.util.Arrays;
import java.util.List;

public class NeptunDesktop {
	public static void main (String[] args) {


		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//config.vSyncEnabled = true;
		new LwjglApplication(new Neptun(), config);


		Preferences keyboard = Gdx.app.getPreferences("keyboard");
		keyboard.putInteger("up", Input.Keys.UP);
		keyboard.putInteger("left", Input.Keys.LEFT);
		keyboard.putInteger("down", Input.Keys.DOWN);
		keyboard.putInteger("right", Input.Keys.RIGHT);
		keyboard.putInteger("action", Input.Keys.SPACE);

		Preferences neptun = Gdx.app.getPreferences("neptun");
		//neptun.putBoolean("replay", true);
		for (String arg : args) {
			if ("replay".equals(arg)) {
				neptun.putBoolean("replay", true);
			}
			if ("debug".equals(arg)) {
				neptun.putBoolean("debug", true);
			}
		}

	}
}
