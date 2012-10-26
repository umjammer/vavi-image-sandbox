/*
 * Copyright (c) 2009 by KLab Inc., All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.gif;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.junit.Test;



/**
 * GifRendererTest. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/20 nsano initial version <br>
 */
public class GifRendererTest {

    @Test
    public void test01() throws Exception {

        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream iis = new FileImageInputStream(new File("src/test/resources/vavi/awt/image/color/sendMail.gif"));
        reader.setInput(iis, true);

        GifRenderer renderer = new GifRenderer();

        for (int i = 0;; i++) {
            BufferedImage image;
            try {
                image = reader.read(i);
            } catch (IndexOutOfBoundsException e) {
                break;
            }

            IIOMetadata imageMetaData = reader.getImageMetadata(i);

            BufferedImage renderedImage = renderer.render(image, imageMetaData);
JOptionPane.showMessageDialog(null, new ImageIcon(image), "gif renderer", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(renderedImage));
        }

        iis.close();

        renderer.dispose();
    }
}

/* */
