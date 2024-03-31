package ru.asocial.games.neptun;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class ExplosionCutter {

    static File destDir = new File("D:\\WORK\\neptun-temp\\explosion\\out");

    static int i = 0;

    public static void main(String[] argc) throws Exception {
        System.out.println("begin");
        File srcImg = new File("D:\\work\\neptun-temp\\explosion\\explosion.png");
        destDir.mkdirs();
        BufferedImage image = ImageIO.read(srcImg);

        int size = 184;

        for (int j = 0; j < 5; j ++) {
            processImage(image, size * j, 0, size);
        }

        for (int j = 0; j < 2; j ++) {
            processImage(image, size * j, size, size);
        }

        System.out.println("end");
    }

    private static void processImage(BufferedImage srcImage, int x, int y, int size) throws Exception{
        BufferedImage sub = srcImage.getSubimage(x, y, size, size);
        Image img = sub.getScaledInstance(size, size, Image.SCALE_DEFAULT);
        BufferedImage outputImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        outputImage.getGraphics().drawImage(img, 0, 0, null);
        ImageIO.write(outputImage, "png", new File(destDir, "explosion_"+i+++".png"));
    }
}
