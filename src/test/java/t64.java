/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import org.w3c.dom.NodeList;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.lang.Rational;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.GpsDirectory;

import vavix.util.grep.FileDigger;
import vavix.util.grep.RegexFileDigger;


/**
 * t64. EXIF Decoder
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/11/02 nsano initial version <br>
 */
public class t64 {

    public static void main(String[] args) throws Exception {
        FileDigger fileDigger = new RegexFileDigger(new FileDigger.FileDredger() {
            public void dredge(File file) throws IOException {
                try {
                    double[] gps = getGpsInfo1(file);
                    if (gps != null) {
                        System.out.println(file);
                        System.out.printf("latitude=%.2f longitude=%.2f\n", gps[0], gps[1]);
                    } else {
                        System.out.println(file);
                        System.out.println("null");
                    }
                } catch (Exception e) {
                    System.err.println(file);
                    e.printStackTrace(System.err);
                }
            }
        }, Pattern.compile(".+\\.jpg"));
        fileDigger.dig(new File(args[0]));
    }

    static ImageReader ir;
    static {
        Iterator<?> it = ImageIO.getImageReadersByFormatName("jpeg");
        ir = (ImageReader) it.next();
    }

    static double[] getGpsInfo1(File file) throws Exception {
        Metadata metadata = JpegMetadataReader.readMetadata(file);
        return getGpsInfo3(metadata);
    }

    static double[] getGpsInfo2(File file) throws Exception {
        InputStream is = new FileInputStream(file);

        ir.setInput(ImageIO.createImageInputStream(is), true, false);
        IIOMetadata meta = ir.getImageMetadata(0);

        return getGpsInfo2_2(meta);
    }

    static double[] getGpsInfo2_2(IIOMetadata meta) throws MetadataException {

        IIOMetadataNode root = (IIOMetadataNode) meta.getAsTree(meta.getNativeMetadataFormatName());

        NodeList nl = root.getElementsByTagName("unknown");
        int nodes = nl.getLength();
        for (int i = 0; i < nodes; i++) {
            IIOMetadataNode node = (IIOMetadataNode) nl.item(i);
            String val = node.getAttribute("MarkerTag");
            if (val != null && val.equals("225")) {
                return getGpsInfo2_3((byte[]) node.getUserObject());
            }
        }

        return null;
    }

    static double[] getGpsInfo2_3(byte[] bytes) throws MetadataException {
        Metadata meta = new Metadata();
        new ExifReader(bytes).extract(meta);

        return getGpsInfo3(meta);
    }

    static double[] getGpsInfo3(Metadata meta) throws MetadataException {

System.err.println("---- dirs ----");
Iterator<?> i = meta.getDirectoryIterator();
while (i.hasNext()) {
 System.err.println(i.next());
}
        GpsDirectory dir = (GpsDirectory) meta.getDirectory(GpsDirectory.class);
        if (dir == null) {
System.out.println("meta exists");
            return null;
        }
System.err.println("---- tags ----: " + dir.getClass().getName());
i = dir.getTagIterator();
while (i.hasNext()) {
 System.err.println(i.next());
}

        double lat = 0;
        double lon = 0;
        Rational[] rs;
        String s;

        if (dir.containsTag(GpsDirectory.TAG_GPS_LATITUDE)) {
            rs = dir.getRationalArray(GpsDirectory.TAG_GPS_LATITUDE);
            lat = rs[0].doubleValue() + (rs[1].doubleValue() / 60) + (rs[2].doubleValue() / 3600);
        }
        if (dir.containsTag(GpsDirectory.TAG_GPS_LONGITUDE)) {
            rs = dir.getRationalArray(GpsDirectory.TAG_GPS_LONGITUDE);
            lon = rs[0].doubleValue() + (rs[1].doubleValue() / 60) + (rs[2].doubleValue() / 3600);
        }
        if (dir.containsTag(GpsDirectory.TAG_GPS_LATITUDE_REF)) {
            s = dir.getString(GpsDirectory.TAG_GPS_LATITUDE_REF);
            if (s.equals("S")) {
                lat *= -1;
            }
        }
        if (dir.containsTag(GpsDirectory.TAG_GPS_LONGITUDE_REF)) {
            s = dir.getString(GpsDirectory.TAG_GPS_LONGITUDE_REF);
            if (s.equals("W")) {
                lon *= -1;
            }
        }
        return new double[] {
            lat, lon
        };
    }
}



/* */
