
package vavix.awt.image.resample.enlarge.noids.graphics;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.util.Arrays;


public abstract class UtBufferedImage {

    public static void fill(BufferedImage image, int color) {
        DataBuffer buffer = image.getRaster().getDataBuffer();
        if (buffer instanceof DataBufferInt) {
            int[] data = ((DataBufferInt) buffer).getData();
            Arrays.fill(data, color);
        } else {
            byte[] data = ((DataBufferByte) buffer).getData();
            Arrays.fill(data, (byte) color);
        }
    }

    public static void copy(BufferedImage src, BufferedImage dst) {
        DataBuffer buffer = src.getRaster().getDataBuffer();
        if (buffer instanceof DataBufferInt) {
            int[] s = ((DataBufferInt) src.getRaster().getDataBuffer()).getData();
            int[] d = ((DataBufferInt) dst.getRaster().getDataBuffer()).getData();
            System.arraycopy(s, 0, d, 0, s.length);
        } else if (buffer instanceof DataBufferByte) {
            byte[] s = ((DataBufferByte) src.getRaster().getDataBuffer()).getData();
            byte[] d = ((DataBufferByte) dst.getRaster().getDataBuffer()).getData();
            System.arraycopy(s, 0, d, 0, s.length);
        } else if (buffer instanceof DataBufferShort) {
            short[] s = ((DataBufferShort) src.getRaster().getDataBuffer()).getData();
            short[] d = ((DataBufferShort) dst.getRaster().getDataBuffer()).getData();
            System.arraycopy(s, 0, d, 0, s.length);
        } else if (buffer instanceof DataBufferUShort) {
            short[] s = ((DataBufferUShort) src.getRaster().getDataBuffer()).getData();
            short[] d = ((DataBufferUShort) dst.getRaster().getDataBuffer()).getData();
            System.arraycopy(s, 0, d, 0, s.length);
        } else {
            throw new RuntimeException("未実装 _" + buffer.getClass());
        }
    }

    public static int[] getIntData(BufferedImage image) {
        return ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
    }

    public static byte[] getByteData(BufferedImage image) {
        return ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
    }
}
