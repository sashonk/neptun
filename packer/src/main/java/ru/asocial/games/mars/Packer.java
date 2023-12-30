package ru.asocial.games.neptun;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * Hello world!
 *
 */
public class Packer
{
    public static void main( String[] args )
    {
        TexturePacker.process("D:\\WORK\\neptun-pack", "D:\\WORK\\neptun-pack\\packed", "images");
        //TexturePacker.process("D:\\WORK\\neptun-images\\floor", "D:\\WORK\\neptun-images\\floor\\packed", "images");

    }
}
