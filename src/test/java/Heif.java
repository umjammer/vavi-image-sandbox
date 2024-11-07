/*
 * Copyright (c) 2017 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.rococoa.cocoa.appkit.NSImage;
import org.rococoa.cocoa.foundation.NSData;
import vavi.swing.JImageComponent;
import vavi.util.Debug;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * Heif prototype. (heif) using jna, rococoa
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2017/11/16 umjammer initial version <br>
 */
@EnabledIf("localPropertiesExists")
@PropsEntity(url = "file:local.properties")
public class Heif {

    static boolean localPropertiesExists() {
        return Files.exists(Paths.get("local.properties"));
    }

    @Property(name = "avif")
    String avif = "src/test/resources/kimono.avif";

    @BeforeEach
    void setup() throws Exception {
        if (localPropertiesExists()) {
            PropsEntity.Util.bind(this);
        }
    }

    @Test
    @EnabledIfSystemProperty(named = "vavi.test", matches = "ide")
    void testAvif() throws Exception {
        main(new String[] {avif});
    }

    static void show(BufferedImage image) throws Exception {
        CountDownLatch cdl = new CountDownLatch(1);
        JFrame frame = new JFrame();
        frame.setTitle("HEIF/AVIF");
        frame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) {
                cdl.countDown();
            }
        });
        JImageComponent component = new JImageComponent();
        component.setImage(image);
        int w = image.getWidth(component);
        int h = image.getHeight(component);
        component.setPreferredSize(new Dimension(w, h));
        frame.getContentPane().add(component);
        frame.pack();
        frame.setVisible(true);
        cdl.await();
    }

    /**
     * @param args 0: int
     */
    public static void main(String[] args) throws Exception {
        Random random = new Random(System.currentTimeMillis());
        String filename = args[random.nextInt(args.length)];

        // returns null when file not found ...orz
        NSImage nsImage = NSImage.imageWithContentsOfFile(filename);
        if (nsImage == null) {
            throw new FileNotFoundException(filename);
        }
Debug.println(nsImage);
        //com.sun.jna.Pointer imageRep = image.TIFFRepresentation();
        NSData data = nsImage.TIFFRepresentation();
Debug.println(data.length());
        ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());
        BufferedImage image = ImageIO.read(bais);

        show(image);
    }
}
