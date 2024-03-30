package ru.asocial.games.java;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import ru.asocial.games.core.Neptun;

public class NeptunDesktop {
	public static void main (String[] args) {


		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//config.vSyncEnabled = true;
		new LwjglApplication(new Neptun(), config);


		Preferences keyboard = Gdx.app.getPreferences("keyboard");
		keyboard.putInteger("up", Input.Keys.W);
		keyboard.putInteger("left", Input.Keys.A);
		keyboard.putInteger("down", Input.Keys.S);
		keyboard.putInteger("right", Input.Keys.D);
		keyboard.putInteger("action", Input.Keys.SPACE);

		Preferences neptun = Gdx.app.getPreferences("neptun");
		//neptun.putBoolean("replay", true);
		if (args.length > 0) {
			if ("replay".equals(args[0])) {
				neptun.putBoolean("replay", true);
			}
		}

	}
}
