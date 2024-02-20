/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.apng;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import javax.imageio.metadata.IIOMetadata;

import vavi.awt.image.AnimationRenderer;


/**
 * ApngRenderer.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-11-18 nsano initial version <br>
 */
public class ApngRenderer implements AnimationRenderer {

    @Override
    public BufferedImage addFrame(BufferedImage image, IIOMetadata imageMetaData) {
        return null;
    }

    @Override
    public int getDelayTime(IIOMetadata imageMetaData) {
        return 0;
    }

    @Override
    public int getDelayTime(int index) {
        return 0;
    }

    @Override
    public Rectangle getBounds(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public BufferedImage get(int index) {
        return null;
    }

    @Override
    public Iterator<BufferedImage> iterator() {
        return null;
    }
}
