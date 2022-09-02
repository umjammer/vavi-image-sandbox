
package vavix.awt.image.resample.enlarge.noids.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static vavix.awt.image.resample.enlarge.noids.util.UtString.fillZeros;


/** t */
public abstract class UtToString {

    public static String toString(Rectangle.Double rect, int n) {
        return rect != null ? "[ " + fillZeros(rect.getX(), n) + " ," + fillZeros(rect.getY(), n) + " ,"
                              + fillZeros(rect.getWidth(), n) + " ," + fillZeros(rect.getHeight(), n) + " ]"
                           : "[null rect]";
    }

    public static String toString(Rectangle.Double rect) {
        return toString(rect, 3);
    }

    public static String toString(Point.Double value) {
        return toString(value, 2);
    }

    public static String toString(Point.Double value, int n) {
        return value != null ? "[ " + fillZeros(value.getX(), n) + " ," + fillZeros(value.getY(), n) + " ]"
                            : "[null rect]";
    }

    public static String toString(Dimension dimension) {
        return dimension != null ? "[ " + dimension.width + " ," + dimension.height + " ]" : "[null dimension]";
    }

    public static String toString(double value, int n) {
        return UtString.fillSpaces(value, n + 3);
    }

    public static String toString(Object[] values, String s) {
        if (values == null)
            return "[array is null]";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0)
                sb.append(s);
            sb.append(values[i]);
        }

        return sb.toString();
    }

    public static String toString(int[] values, String s) {
        if (values == null)
            return "[array is null]";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0)
                sb.append(s);
            sb.append(values[i]);
        }

        return sb.toString();
    }

    public static String toString(double[] values, String s, int n) {
        if (values == null)
            return "[array is null]";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0)
                sb.append(s);
            sb.append(toString(values[i], n));
        }

        return sb.toString();
    }

    public static String toString(float[] values, String s, int n) {
        if (values == null)
            return "[array is null]";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0)
                sb.append(s);
            sb.append(toString(values[i], n));
        }

        return sb.toString();
    }

    public static String toString(Map<?, ?> map) {
        if (map == null)
            return "[map is null]";
        StringBuilder sb = new StringBuilder();
        Set<?> set = map.keySet();
        Object key;
        for (Object o : set) {
            key = o;
            sb.append(key).append(":").append(map.get(key)).append("\n");
        }

        return sb.toString();
    }

    public static String toString(Color color) {
        if (color == null) {
            return "[color is null]";
        } else {
            int rgb = color.getRGB();
            return toHex(rgb);
        }
    }

    public static String toHex(int i) {
        return UtString.toHex2(i);
    }

    public static String getMemoryUsageString() {
        double d = 9.9999999999999995e-07d;
        double d1 = Runtime.getRuntime().maxMemory() * d;
        double d2 = Runtime.getRuntime().totalMemory() * d;
        double d3 = Runtime.getRuntime().freeMemory() * d;
        return "using : " + fillZeros(d2 - d3, 1) + "/" + fillZeros(d2, 1) + "MB  ( max : "
               + fillZeros(d1, 1) + "MB )";
    }

    public static String getFormattedDateString() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return dateFormat.format(date);
    }

    public static String toString(List<?> list) {
        StringBuilder s = new StringBuilder();
        int c = 0;
        for (Object o : list) {
            if (c != 0)
                s.append("\n");
            s.append("[").append(c++).append("] : ").append(o);
        }
        return s.toString();
    }

    public static String toString(Object value) {
        if (value == null)
            return "null";
        if (value instanceof String)
            return (String) value;
        if (value instanceof Point)
            return toString(value);
        if (value instanceof Point.Double)
            return toString((Point.Double) value);
        if (value instanceof Rectangle)
            return toString(value);
        if (value instanceof Rectangle.Double)
            return toString((Rectangle.Double) value);
        if (value instanceof AffineTransform)
            return toString((AffineTransform) value);
        if (value instanceof Double)
            return toString((Double) value, 4);
        if (value instanceof java.util.List)
            return toString((java.util.List<?>) value);
        if (value instanceof Map)
            return toString((Map<?, ?>) value);
        if (value.getClass().isArray() && !value.getClass().getComponentType().isPrimitive())
            return toString((Object[]) value, " ,");
        if (value instanceof int[])
            return toString((int[]) value, " ,");
        if (value instanceof double[])
            return toString((double[]) value, " ,", 8);
        if (value instanceof float[])
            return toString((float[]) value, " ,", 6);
        if (value instanceof Color)
            return toString((Color) value);
        if (value instanceof Dimension)
            return toString((Dimension) value);
        else
            return value.toString();
    }

    public static String toString(AffineTransform tx) {
        double[] flatMatrix = new double[6];
        tx.getMatrix(flatMatrix);
        return "[ " + toString(flatMatrix, " ,", 2) + " ]";
    }

    public static void debug(AffineTransform tx) {
        double[] flatMatrix = new double[6];
        tx.getMatrix(flatMatrix);
        System.out.println("AffineTransform [ " + toString(flatMatrix, " ,", 3) + " ]");
    }

    public static void debug(Point.Double value) {
        System.out.println(toString(value));
    }
}
