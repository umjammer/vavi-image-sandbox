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
 * CroppedImageFixer.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2022/01/28 umjammer initial version <br>
 */
@EnabledIf("localPropertiesExists")
@PropsEntity(url = "file:local.properties")
public class CroppedImageFixer {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "cropped.dir")
    String dir;

    /**
     * <pre>
     *  $ mogrify -gravity center -background white -extent 1222x1800 -path out *.jpg
     * </pre>
     * @param args 0: dir
     */
    public static void main(String[] args) throws IOException {
//        String dir = args[0];
        CroppedImageFixer app = new CroppedImageFixer();
        PropsEntity.Util.bind(app);
        Path path = Paths.get(app.dir);
        Files.list(path).filter(p -> p.getFileName().toString().endsWith(".jpg")).sorted().forEach(p -> {
            try {
                int W = 1184;
                int H = 1750;
                BufferedImage image = ImageIO.read(Files.newInputStream(p));
                int w = image.getWidth();
                int h = image.getHeight();
                double s1 = (double) W / w;
                int w2 = (int) (w * s1);
                int h2 = (int) (H * s1);
                double s2 = 1;
                if (h2 > H) {
                    s2 = (double) H / h2;
                } 
                int w3 = (int) (w2 * s2);
                int h3 = (int) (h2 * s2);
                System.out.println("mogrify -resize " + w3 + "x" + h3 + " -path out " + p.getFileName());
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }
}
