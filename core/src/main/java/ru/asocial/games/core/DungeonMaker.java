package ru.asocial.games.core;

public class DungeonMaker {

    static {
        System.loadLibrary("dmaker.dll");
    }

    public native void generateMap(byte[][] output);
}
