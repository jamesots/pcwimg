package com.jamesots.pcwimg;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar pcwimg.jar <src.png> <dest.pig>");
        }

        try {
            final BufferedImage image = ImageIO.read(new File(args[0]));

            final int width = image.getWidth();
            final int height = image.getHeight();

            if (width != 360 || height != 256) {
                System.out.println("Image height should be 360x256 (not " + width + "x" + height +")");
                return;
            }

            final int[] rgbs = image.getRGB(0, 0, 360, 256, null, 0, 360);

            final byte[] pig = new byte[32*90*8];
            for (int y = 0; y < 256; y++) {
                for (int x = 0; x < 360; x++) {
                    int rgb = rgbs[y * 360 + x];

                    int row = 720 * (y / 8) + y % 8;
                    int col = ((x * 2) / 8) * 8;
                    int colbit = (x * 2) % 8;

                    byte colour = 0;
                    if ((rgb & 0xFF) < 64) {
                        colour = 0;
                    } else if ((rgb & 0xFF) > 240) {
                        colour = 3;
                    } else {
                        colour = 1;
                    }

                    pig[row + col] = (byte) (pig[row + col] | (colour << (6 - colbit)));
                }
            }
            final FileOutputStream pigfile = new FileOutputStream(new File(args[1]));
            pigfile.write(pig);
            pigfile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
