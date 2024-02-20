/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.CountDownLatch;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JFrame;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import vavi.awt.image.AnimationRenderer;
import vavi.awt.image.gif.GifRenderer;


/**
 * JAnimationFrameTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-11-18 nsano initial version <br>
 */
public class JAnimationFrameTest {

    @Test
    @EnabledIfSystemProperty(named = "vavi.test", matches = "ide")
    void test1() throws Exception {
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream iis = ImageIO.createImageInputStream(JAnimationFrame.class.getResourceAsStream("/ani.gif"));
        reader.setInput(iis, true);
        AnimationRenderer renderer = new GifRenderer();
        for (int i = 0;; i++) {
            BufferedImage image;
            try {
                image = reader.read(i);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
            IIOMetadata imageMetaData = reader.getImageMetadata(i);
            renderer.addFrame(image, imageMetaData);
        }
        iis.close();

        // using cdl cause junit stops awt thread suddenly
        CountDownLatch cdl = new CountDownLatch(1);
        JFrame frame = new JAnimationFrame(renderer);
        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { cdl.countDown(); }
        });
        frame.setLocation(100, 100);
        frame.pack();
        frame.setVisible(true);
        cdl.await();
    }
}
