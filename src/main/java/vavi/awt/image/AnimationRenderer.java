/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import javax.imageio.metadata.IIOMetadata;


/**
 * AnimationRenderer.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-11-18 nsano initial version <br>
 */
public interface AnimationRenderer extends Iterable<BufferedImage> {

    /**
     * Adds a frame image with it's image meta data.
     * @return rendered frame image
     */
    BufferedImage addFrame(BufferedImage image, IIOMetadata imageMetaData);

    /** Gets delay time of frame image in [milli-sec]. */
    int getDelayTime(IIOMetadata imageMetaData);

    /** Gets delay time of frame image in [milli-sec]. */
    int getDelayTime(int index);

    /** Gets bounds of frame image. */
    Rectangle getBounds(int index);

    /** Gets a number of frame images */
    int size();

    /** Gets a rendered frame image specified by the index number. */
    BufferedImage get(int index);

    /** Gets a rendered frame image iterator. */
    @Override
    Iterator<BufferedImage> iterator();
}
