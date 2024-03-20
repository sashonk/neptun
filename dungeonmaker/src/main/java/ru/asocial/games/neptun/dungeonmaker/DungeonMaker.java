package ru.asocial.games.neptun.dungeonmaker;

public class DungeonMaker {

    public native void generateDungeon(String designFileName, String outFileName);

    public static void main(String[] argc) {
        System.loadLibrary("dmaker");

        DungeonMaker maker = new DungeonMaker();
        maker.generateDungeon("C:\\Users\\user\\Downloads\\dmaker\\dungeonmaker2_0WinExe\\design", "D:\\work\\dungeon.txt");
    }
}
