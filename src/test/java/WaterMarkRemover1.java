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
 * WaterMarkRemover1.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/01/29 umjammer initial version <br>
 */
@EnabledIf("localPropertiesExists")
@PropsEntity(url = "file:local.properties")
public class WaterMarkRemover1 {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "watermark1.file1")
    String file1;

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        WaterMarkRemover1 app = new WaterMarkRemover1();
        PropsEntity.Util.bind(app);
        args = new String[] {app.file1 };
        t1(args);
    }

    /** single */
    public static void t2(String[] args) throws Exception {
        WaterMarkRemover1 app = new WaterMarkRemover1();
        Path in = Paths.get("tmp", "v13_049.jpg");
        Path out = Paths.get("tmp", "out.jpg");

        BufferedImage result = app.func1(in);
        if (result != null) {
            ImageIO.write(result, "JPG", Files.newOutputStream(out));
System.err.printf("%s%n", out);
        }
    }

    /** multiple */
    public static void t1(String[] args) throws Exception {
        WaterMarkRemover1 app = new WaterMarkRemover1();
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

    /** removes watermark */
    BufferedImage func1(Path path) throws IOException {
        BufferedImage image = ImageIO.read(Files.newInputStream(path));
        int width = image.getWidth();
        int height = image.getHeight();

        int[] rgbs = new int[width * height];
        image.getRGB(0, 0, width, height, rgbs, 0, width);

        BufferedImage result = new BufferedImage(width, height, image.getType());

        int modified = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = x + y * width;
                int a = (rgbs[p] & 0xff000000) >>> 24;
                int r = (rgbs[p] & 0x00ff0000) >> 16;
                int g = (rgbs[p] & 0x0000ff00) >> 8;
                int b =  rgbs[p] & 0x000000ff;
                if (!(nearlyEquals(r, g) && nearlyEquals(r, b))) {
                    // color
//System.err.printf("%03d, %03d: %02x %02x%02x%02x%n", x, y, a, r, g, b);
                    result.setRGB(x, y, getColor(x, y, rgbs, a, r, g, b, width, height));
                    modified++;
                } else {
                    // W&B
                    result.setRGB(x, y, rgbs[p]);
                }
            }
        }

        if (1000 < modified && modified < 9000) { // magic: how many pixels modified
System.err.printf("%s: %d%n", path, modified);
            return result;
        } else {
            return null;
        }
    }

    /** argb to color */
    int toInt(int a, int r, int g, int b) {
        return ((a & 0xff) << 24) |
               ((r & 0xff) << 16) |
               ((g & 0xff) <<  8) |
                (b & 0xff);
    }

    static final double threshold = .025;

    /**
     * @see // #threshold
     */
    boolean nearlyEquals(int a, int b) {
//System.err.printf("%02x, %02x, %.2f%n", a, b, Math.abs(a - b) / 255d);
        return (Math.abs(a - b) / 255d) <= threshold;
    }

    static final int X = 2;

    /**
     * @see // #X
     */
    int getColor(int x, int y, int[] rgbs, int a, int r, int g, int b, int width, int height) {
        int c = 0xffffffff;
        switch (X) {
        default:
            // erase by white
            break;
        case 1:
            // gray by brightest element of rgb
            int e = 255 - Math.max(Math.max(r - g, 0), Math.max(r - b, 0));
            c = (int) (Long.parseLong(String.format("ff%1$02x%1$02x%1$02x", e), 16) & 0xffffffffL);
            break;
        case 2:
            // for a-zmanga pink (241, 179, 182)
            int mr = 236; // magic: target color r
            int mg = 157; // magic: target color g
            int mb = 162; // magic: target color b
            int r2 = 255 - mr;
            int g2 = 255 - mg;
            int b2 = 255 - mb;
            int r3 = (r + r2) & 0xff;
            int g3 = (g + g2) & 0xff;
            int b3 = (b + b2) & 0xff;
            if (!(nearlyEquals(r3, g3) && nearlyEquals(r3, b3))) {
                e = Math.max(g3, b3);
                if (e > 80) { // magic: exclude darker pixels
                    c = toInt(a, e, e, e);
                }
            }
//System.err.printf("%08x%n", c);
            break;
        }

        return c;
    }
}

/* */
