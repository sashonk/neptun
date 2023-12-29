package ru.asocial.games.mars;

import com.badlogic.gdx.tools.texturepacker.TexturePacker;

/**
 * Hello world!
 *
 */
public class Packer
{
    public static void main( String[] args )
    {
        TexturePacker.process("D:\\WORK\\mars-pack", "D:\\WORK\\mars-pack\\packed", "images");
        //TexturePacker.process("D:\\WORK\\mars-images\\floor", "D:\\WORK\\mars-images\\floor\\packed", "images");

    }
}
