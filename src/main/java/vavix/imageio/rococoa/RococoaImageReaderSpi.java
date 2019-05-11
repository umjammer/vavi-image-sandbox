/*
 * Copyright (c) 2017 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.imageio.rococoa;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;


public class RococoaImageReaderSpi extends ImageReaderSpi {

    private static final String VendorName = "http://www.vavisoft.com";
    private static final String Version = "0.00";
    private static final String ReaderClassName =
        "vavix.imageio.rocococa.RococoaImageReader";
    private static final String[] Names = {
        "heif"
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
    public RococoaImageReaderSpi() {
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

    /* */
    public String getDescription(Locale locale) {
        return "HEIF Image";
    }

    /* */
    public boolean canDecodeInput(Object obj)
        throws IOException {
        if (obj instanceof FileImageInputStream) {
            FileImageInputStream fiis = FileImageInputStream.class.cast(obj);
            fiis.mark();
            // we currently accept heif only
            byte[] buf = new byte[12];
            fiis.read(buf, 0, 12);
            fiis.reset();
            final byte[] magic = { 00, 00, 00, 0x1c, 0x66, 0x74, 0x79, 0x70, 0x6d, 0x69, 0x66, 0x31 };
//System.err.println("HERE; " + Arrays.equals(buf, magic) + "\n" + vavi.util.StringUtil.getDump(buf));
            return Arrays.equals(buf, magic);
        } else {
            return false;
        }
    }

    /* */
    public ImageReader createReaderInstance(Object obj) {
        return new RococoaImageReader(this);
    }
}
