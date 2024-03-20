package ru.asocial.games.neptun;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

public class DoorCutter {


    public static void main(String[] argc) throws Exception {
        System.out.println("begin");
        File srcDir = new File("D:\\work\\neptun-temp\\doors\\in");
        File destDir = new File("D:\\work\\neptun-temp\\doors\\out");
        destDir.mkdirs();

        for (File imgFile : srcDir.listFiles()) {
            File dir = new File(destDir, imgFile.getName().replace(".png", ""));
            destDir.mkdirs();

            BufferedImage image = ImageIO.read(imgFile);
            int tileWidth = 64, tileHeight = 64;
            int tileSetWidth = 12 * tileWidth;
            int tileSetHeight = 8 * tileHeight;
            int tileSubSetWidth = 3 * tileWidth;
            int tileSubSetHeight = 4 * tileHeight;
            int d = 0;
            for (int j = 0; j < tileSetWidth; j+=tileWidth) {
                File destSubDir = new File(dir,  Integer.toString(d++));
                destSubDir.mkdirs();
                int k = 0;
                for (int i = 0; i < tileSubSetHeight; i+=tileHeight) {
                    processImage(image, j , i, tileWidth, tileHeight, "door_"+ k++, destSubDir);
                }
            }

            for (int j = 0; j < tileSetWidth; j+=tileWidth) {
                File destSubDir = new File(dir,  Integer.toString(d++));
                destSubDir.mkdirs();
                int k = 0;
                for (int i = tileSetHeight / 2; i < tileSetHeight; i+=tileHeight) {
                    processImage(image, j , i, tileWidth, tileHeight, "door_"+ k++, destSubDir);
                }
            }
        }

        System.out.println("end");
    }

    private static void processImage(BufferedImage srcImage, int x, int y, int w, int h, String name, File destSubDir) throws Exception{
        BufferedImage sub = srcImage.getSubimage(x, y, w, h);
        ImageIO.write(sub, "png", new File(destSubDir, name + ".png"));
        System.out.println((x + w) + " " + (y + h));
    }
}
