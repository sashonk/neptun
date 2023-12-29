package ru.asocial.games.mars;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;

public class CatFluffyCutter {


    public static void main(String[] argc) throws Exception {
        System.out.println("begin");
        File srcDir = new File("D:\\work\\mars-temp\\MythologicalAnimals\\12x8");
        File destDir = new File("D:\\work\\mars-temp\\MythologicalAnimals\\12x8_proc");
        destDir.mkdirs();
        for (File imgFile : srcDir.listFiles()) {
            File dir = new File(destDir, imgFile.getName().replace(".png", ""));
            destDir.mkdirs();

            BufferedImage image = ImageIO.read(imgFile);
            int tileWidth = 48, tileHeight = 48;
            int tileSetWidth = 12 * tileWidth;
            int tileSetHeight = 8 * tileHeight;
            int tileSubSetWidth = 3 * tileWidth;
            int tileSubSetHeight = 4 * tileHeight;
            int d = 0;
            for (int j = 0; j < tileSetHeight; j+=tileSubSetHeight) {
                for (int i = 0; i < tileSetWidth; i+=tileSubSetWidth) {
                    File destSubDir = new File(dir,  Integer.toString(d++));
                    destSubDir.mkdirs();
                    LinkedList<String> positions = new LinkedList<>(Arrays.asList("front","left", "right", "back")) ;
                    for (int y = 0; y < tileSubSetHeight; y+=tileHeight) {
                        String pos = positions.poll();
                        int k = 0;
                        for (int x = 0; x < tileSubSetWidth; x+=tileWidth) {
                            processImage(image, x + i, y + j, tileWidth, tileHeight, pos + "_" + k++, destSubDir);
                        }
                    }
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
