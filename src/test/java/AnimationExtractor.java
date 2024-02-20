/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import vavi.awt.image.AnimationRenderer;
import vavi.awt.image.gif.GifRenderer;


/**
 * AnimationExtractor.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-11-18 nsano initial version <br>
 */
public class AnimationExtractor {

    @Test
    @EnabledIfSystemProperty(named = "vavi.test", matches = "ide")
    void test1() throws Exception {
        main(new String[] {"tmp/anigif.gif", "tmp/aniout", "tmp/aniout2"});
    }

    /**
     * @param args 0: image in
     */
    public static void main(String[] args) throws IOException {
        Path dir = Paths.get(args[1]);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
        Path dir2 = Paths.get(args[2]);
        if (!Files.exists(dir2)) {
            Files.createDirectories(dir2);
        }
        Path anigif = Paths.get(args[0]);

        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream iis = ImageIO.createImageInputStream(Files.newInputStream(anigif));
        reader.setInput(iis, true);
        AnimationRenderer renderer = new GifRenderer();
        for (int i = 0;; i++) {
            BufferedImage image;
            BufferedImage renderedImage;
            int delay;
            try {
                image = reader.read(i);
                IIOMetadata imageMetaData = reader.getImageMetadata(i);
                renderedImage = renderer.addFrame(image, imageMetaData);
                delay = renderer.getDelayTime(i);
            } catch (IndexOutOfBoundsException e) {
                break;
            }

            Path out = dir.resolve(String.format("%03d_%05d.png", i, delay));
            ImageIO.write(renderedImage,  "PNG", Files.newOutputStream(out));

            Path out2 = dir2.resolve(String.format("%03d.png", i, delay));
            ImageIO.write(image,  "PNG", Files.newOutputStream(out2));
        }
        iis.close();
    }
}
