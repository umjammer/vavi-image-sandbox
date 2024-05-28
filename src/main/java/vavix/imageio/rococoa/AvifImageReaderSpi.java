/*
 * Copyright (c) 2017 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.imageio.rococoa;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;

import vavi.util.Debug;


/**
 * AvifImageReaderSpi.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 Nov 16, 2017 umjammer initial version <br>
 */
public class AvifImageReaderSpi extends ImageReaderSpi {

    static {
        try {
            try (InputStream is = AvifImageReaderSpi.class.getResourceAsStream("/META-INF/maven/vavi/vavi-image-sandbox/pom.properties")) {
                if (is != null) {
                    Properties props = new Properties();
                    props.load(is);
                    Version = props.getProperty("version", "undefined in pom.properties");
                } else {
                    Version = System.getProperty("vavi.test.version", "undefined");
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static final String VendorName = "https://github.com/umjammer/vavi-image-sandbox";
    private static final String Version;
    private static final String ReaderClassName =
        "vavix.imageio.rocococa.RococoaImageReader";
    private static final String[] Names = {
        "avif", "AVIF"
    };
    private static final String[] Suffixes = {
        "avif"
    };
    private static final String[] mimeTypes = {
        "image/avif"
    };
    static final String[] WriterSpiNames = {};
    private static final boolean SupportsStandardStreamMetadataFormat = false;
    private static final String NativeStreamMetadataFormatName = null;
    private static final String NativeStreamMetadataFormatClassName = null;
    private static final String[] ExtraStreamMetadataFormatNames = null;
    private static final String[] ExtraStreamMetadataFormatClassNames = null;
    private static final boolean SupportsStandardImageMetadataFormat = false;
    private static final String NativeImageMetadataFormatName = "avif";
    private static final String NativeImageMetadataFormatClassName = null;
    private static final String[] ExtraImageMetadataFormatNames = null;
    private static final String[] ExtraImageMetadataFormatClassNames = null;

    /** */
    public AvifImageReaderSpi() {
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
        return "AVIF Image";
    }

    @Override
    public boolean canDecodeInput(Object obj) throws IOException {
Debug.println(Level.FINER, "input: " + obj);
        if (obj instanceof ImageInputStream fiis) {
            fiis.mark();
            // we currently accept avif only
            byte[] buf = new byte[8];
            fiis.skipBytes(4);
            fiis.readFully(buf);
            fiis.reset();
            // "ftyp" "avif"
            final byte[] magic = { 0x66, 0x74, 0x79, 0x70, 0x61, 0x76, 0x69, 0x66 };
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
