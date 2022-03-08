/*
 * Copyright (c) 2017 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.imageio.rococoa;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import org.rococoa.cocoa.appkit.NSImage;
import org.rococoa.cocoa.foundation.NSData;

import vavi.imageio.WrappedImageInputStream;


/**
 * RococoaImageReader.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 Nov 16, 2017 umjammer initial version <br>
 */
public class RococoaImageReader extends ImageReader {

    /** */
    public RococoaImageReader(ImageReaderSpi originatingProvider) {
        super(originatingProvider);
    }

    /** @see ImageReader */
    public int getNumImages(boolean allowSearch) throws IIOException {
        return 1;
    }

    /** */
    private void checkIndex(int imageIndex) {
        if (imageIndex != 0) {
            throw new IndexOutOfBoundsException("bad index");
        } else {
            return;
        }
    }

    /** @see ImageReader */
    public int getWidth(int imageIndex) throws IIOException {
        checkIndex(imageIndex);
        return getWidth(1);
    }

    /** @see ImageReader */
    public int getHeight(int imageIndex) throws IIOException {
        checkIndex(imageIndex);
        return getHeight(2);
    }

    /** @see ImageReader */
    public BufferedImage read(int imageIndex, ImageReadParam param)
        throws IIOException {

        com.sun.jna.NativeLibrary.addSearchPath("rococoa", System.getProperty("java.library.path"));

        ImageInputStream stream = ImageInputStream.class.cast(input);

        try {

            Path file = Files.createTempFile("vavix.imageio.rococoa", ".heic");
            // TODO StandardOpenOption.DELETE_ON_CLOSE
            Files.copy(new WrappedImageInputStream(stream), file, StandardCopyOption.REPLACE_EXISTING);

            // stream not found で null が返る...orz
            NSImage nsImage = NSImage.imageWithContentsOfFile(file.toString());
            if (nsImage == null) {
//System.err.print(file.getPath());
                throw new FileNotFoundException("problem in reading temporary file: " + file);
            }
            NSData data = nsImage.TIFFRepresentation();
            ByteArrayInputStream bais = new ByteArrayInputStream(data.getBytes());

            return ImageIO.read(bais);

        } catch (IOException e) {
            throw new IIOException(e.getMessage(), e);
        }
    }

    /** @see ImageReader */
    public IIOMetadata getStreamMetadata() throws IIOException {
        return null;
    }

    /** @see ImageReader */
    public IIOMetadata getImageMetadata(int imageIndex) throws IIOException {
        checkIndex(imageIndex);
        return null;
    }

    /** */
    public Iterator<ImageTypeSpecifier> getImageTypes(int imageIndex) throws IIOException {
        checkIndex(imageIndex);
        ImageTypeSpecifier specifier = null;
        List<ImageTypeSpecifier> l = new ArrayList<>();
        l.add(specifier);
        return l.iterator();
    }
}

/* */
