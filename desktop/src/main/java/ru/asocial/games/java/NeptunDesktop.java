package ru.asocial.games.java;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import ru.asocial.games.core.Neptun;

public class NeptunDesktop {
	public static void main (String[] args) {
		if (args.length == 0) {
			args = new String[]{"map300"};
		}
		Neptun.setLaunchArgs(args);

		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//config.vSyncEnabled = true;
		new LwjglApplication(new Neptun(), config);
	}
}
