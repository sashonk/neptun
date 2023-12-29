package ru.asocial.games.mars;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class FloorCutter {

    static File destDir = new File("D:\\WORK\\mars-temp\\floor");

    static int i = 0;

    public static void main(String[] argc) throws Exception {
        System.out.println("begin");
        File srcImg = new File("D:\\WORK\\mars-temp\\5e5c7177e81329cc32c35b4d977196c6.jpg");
        destDir.mkdirs();
        BufferedImage image = ImageIO.read(srcImg);

        int y = 0;
        processImage(image, 0, y, 120, 120);
        for (int j = 0; j < 9; j ++) {
            processImage(image, 122 + 120 * j, y, 118, 120);
        }

        y+=2;
        for (int k = 0; k < 17; k++) {
            //System.out.println(k);
            y+= 120;
            processImage(image, 0, y, 120, 118);
            for (int j = 0; j < 9; j ++) {
                processImage(image, 122 + 120 * j, y, 118, 118);
            }
        }

        System.out.println("end");
    }

    private static void processImage(BufferedImage srcImage, int x, int y, int w, int h) throws Exception{
        int tileWidth = 48, tileHeight = 48;
        BufferedImage sub = srcImage.getSubimage(x, y, w, h);
        Image img = sub.getScaledInstance(tileWidth, tileHeight, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(tileWidth, tileHeight, BufferedImage.TYPE_INT_RGB);
        outputImage.getGraphics().drawImage(img, 0, 0, null);
        ImageIO.write(outputImage, "jpg", new File(destDir, i+++".jpg"));
        System.out.println((x + w) + " " + (y + h));
    }
}
