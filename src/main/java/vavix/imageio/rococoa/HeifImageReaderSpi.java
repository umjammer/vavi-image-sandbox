/*
 * Copyright (c) 2017 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.imageio.rococoa;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import vavi.util.Debug;


/**
 * HeifImageReaderSpi.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 Nov 16, 2017 umjammer initial version <br>
 */
public class HeifImageReaderSpi extends ImageReaderSpi {

    private static final String VendorName = "https://github.com/umjammer/vavi-image-sandbox";
    private static final String Version = "1.0.5";
    private static final String ReaderClassName =
        "vavix.imageio.rocococa.RococoaImageReader";
    private static final String[] Names = {
        "heif", "heic", "HEIF", "HEIC"
    };
    private static final String[] Suffixes = {
        "heif", "heic"
    };
    private static final String[] mimeTypes = {
        "image/heif", "image/heic"
    };
    static final String[] WriterSpiNames = {};
    private static final boolean SupportsStandardStreamMetadataFormat = false;
    private static final String NativeStreamMetadataFormatName = null;
    private static final String NativeStreamMetadataFormatClassName = null;
    private static final String[] ExtraStreamMetadataFormatNames = null;
    private static final String[] ExtraStreamMetadataFormatClassNames = null;
    private static final boolean SupportsStandardImageMetadataFormat = false;
    private static final String NativeImageMetadataFormatName = "heic";
    private static final String NativeImageMetadataFormatClassName = null;
    private static final String[] ExtraImageMetadataFormatNames = null;
    private static final String[] ExtraImageMetadataFormatClassNames = null;

    /** */
    public HeifImageReaderSpi() {
        super(VendorName,
              Version,
              Names,
              Suffixes,
              mimeTypes,
              ReaderClassName,
              new Class[] { ImageInputStream.class },
              WriterSpiNames,
              SupportsStandardStreamMetadataFormat,
              NativeStreamMetadataFormatName,
              NativeStreamMetadataFormatClassName,
              ExtraStreamMetadataFormatNames,
              ExtraStreamMetadataFormatClassNames,
              SupportsStandardImageMetadataFormat,
              NativeImageMetadataFormatName,
              NativeImageMetadataFormatClassName,
              ExtraImageMetadataFormatNames,
              ExtraImageMetadataFormatClassNames);
    }

    @Override
    public String getDescription(Locale locale) {
        return "HEIF Image";
    }

    @Override
    public boolean canDecodeInput(Object obj) throws IOException {
Debug.println(Level.FINER, "input: " + obj);
        if (obj instanceof ImageInputStream fiis) {
            fiis.mark();
            // we currently accept heif only
            byte[] buf = new byte[8];
            fiis.skipBytes(4);
            fiis.readFully(buf);
            fiis.reset();
            // "ftyp" "mif1"
            final byte[] magic = { 0x66, 0x74, 0x79, 0x70, 0x6d, 0x69, 0x66, 0x31 };
Debug.println(Level.FINER, "magic: " + Arrays.equals(buf, magic) + "\n" + vavi.util.StringUtil.getDump(buf));
            return Arrays.equals(buf, magic);
        } else {
            return false;
        }
    }

    @Override
    public ImageReader createReaderInstance(Object obj) {
        return new RococoaImageReader(this);
    }
}
