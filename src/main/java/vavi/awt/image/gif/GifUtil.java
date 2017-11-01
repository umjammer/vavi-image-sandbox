/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.gif;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;


/**
 * GifUtil.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/18 nsano initial version <br>
 */
public class GifUtil {

    private GifUtil() {
    }

    /** @cache */
    private static Map<File, Boolean> isAnimationGIFCache = new HashMap<>();

    private static Map<File, Boolean> isGIFCache = new HashMap<>();

    /** @cache */
    public static boolean isGIF(File sourceFile) throws IOException {
        if (isGIFCache.containsKey(sourceFile)) {
            return isGIFCache.get(sourceFile);
        } else {
//	         int n = nAnimationGIF(sourceFile);
            boolean  isGIF = sourceFile.getAbsolutePath().endsWith("gif");

            isGIFCache.put(sourceFile, isGIF);
            return isGIF;
        }
    }

    public static boolean isAnimationGIF(File sourceFile) throws IOException {
        if (isAnimationGIFCache.containsKey(sourceFile)) {
            return isAnimationGIFCache.get(sourceFile);
        } else {
            if (!isGIF(sourceFile))
                return false;
            boolean _isAnimationGIF = nAnimationGIF(sourceFile) > 1;
            isAnimationGIFCache.put(sourceFile, _isAnimationGIF);
            return _isAnimationGIF;
        }
    }

    /** */
    private static int nAnimationGIF(File sourceFile) throws IOException {
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
//logger.debug("ANIGIF: reader: " + reader);
        ImageInputStream iis = new FileImageInputStream(sourceFile);
        reader.setInput(iis, true);
        int numImages = 0;
        int i = -1;
        for (i = 0;; i++) {
            try {
                reader.read(i);

            } catch (IndexOutOfBoundsException e) {
                numImages = i;
                break;
            } catch (IOException e ){
		numImages = 0;
		break;
	    }
        }
        iis.close();
//logger.debug("anigif: numImages: " + i);
        return numImages;
    }

    /** @cache */
    public static void clearCache() {
        isAnimationGIFCache.clear();
    }
}

/* */
