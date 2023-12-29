package ru.asocial.games.java;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import ru.asocial.games.core.MarsRemake;

public class MarsRemakeDesktop {
	public static void main (String[] args) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//config.vSyncEnabled = true;
		new LwjglApplication(new MarsRemake(), config);


	}
}
