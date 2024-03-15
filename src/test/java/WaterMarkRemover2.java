/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;

import org.junit.jupiter.api.condition.EnabledIf;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * WaterMarkRemover2
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/04/08 umjammer initial version <br>
 */
@EnabledIf("localPropertiesExists")
@PropsEntity(url = "file:local.properties")
public class WaterMarkRemover2 {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "watermark2.file1")
    String file1;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        WaterMarkRemover2 app = new WaterMarkRemover2();
        PropsEntity.Util.bind(app);
        args = new String[] {app.file1 };
        t1(args);
    }

    /** single */
    public static void t2(String[] args) throws Exception {
        WaterMarkRemover2 app = new WaterMarkRemover2();
        Path in = Paths.get(args[0], "v01_014.jpg");
        Path out = Paths.get("tmp", "out.jpg");

        BufferedImage result = app.func1(in);
        if (result != null) {
            ImageIO.write(result, "JPG", Files.newOutputStream(out));
System.err.printf("%s%n", out);
        }
    }

    /** multiple */
    public static void t1(String[] args) throws Exception {
        WaterMarkRemover2 app = new WaterMarkRemover2();
        Path inDir = Paths.get(args[0]);
        Path outDir = inDir.getParent().resolve("out");
        Files.createDirectories(outDir);
        Files.list(inDir).filter(p -> p.getFileName().toString().endsWith(".jpg")).forEach(p -> {
                try {
                    BufferedImage result = app.func1(p);
                    if (result != null) {
                        Path out = outDir.resolve(p.getFileName());
                        ImageIO.write(result, "JPG", Files.newOutputStream(out));
System.err.printf("%s%n", out);
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
        });
    }

    /** subtract */
    BufferedImage func1(Path path) throws IOException {
        BufferedImage image = ImageIO.read(Files.newInputStream(path));
        int width = image.getWidth();
        int height = image.getHeight();

        int[] rgbs = new int[width * height];
        image.getRGB(0, 0, width, height, rgbs, 0, width);

        BufferedImage result = new BufferedImage(width, height, image.getType());

        int y = 0;
        for (int x = 0; x < width; x++) {
            int p = x + y * width;
            int a = (rgbs[p] & 0xff000000) >>> 24;
            int r = (rgbs[p] & 0x00ff0000) >> 16;
            int g = (rgbs[p] & 0x0000ff00) >> 8;
            int b =  rgbs[p] & 0x000000ff;

            int v = ave(r, g, b);

            result.setRGB(x, y, 220 > v ? toInt(a, 255, 255, 255) : rgbs[p]);
        }

        y = 1;
        for (int x = 0; x < width; x++) {
            int p = x + y * width;
            int a = (rgbs[p] & 0xff000000) >>> 24;
            int r = (rgbs[p] & 0x00ff0000) >> 16;
            int g = (rgbs[p] & 0x0000ff00) >> 8;
            int b =  rgbs[p] & 0x000000ff;

            int v = ave(r, g, b);

            result.setRGB(x, y, 240 > v ? toInt(a, 255, 255, 255) : rgbs[p]);
        }

        for (y = 2; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = x + y * width;
                result.setRGB(x, y, rgbs[p]);
            }
        }

        return result;
    }

    int ave(int r, int g, int b) {
        return (int) ((r + g + b) / 3f);
    }

    /** argb to color */
    int toInt(int a, int r, int g, int b) {
        int v = ave(r, g, b);
        return ((a & 0xff) << 24) |
               ((v & 0xff) << 16) |
               ((v & 0xff) <<  8) |
                (v & 0xff);
    }
}
