package ru.asocial.games.neptun;

public class DungeonMaker {

    static {
        System.loadLibrary("dmaker.dll");
    }

    public native void generateMap(byte[][] output, int width, int height);
}
