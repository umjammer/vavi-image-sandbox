
package vavix.awt.image.resample.enlarge.noids.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Arrays;


/** A */
public abstract class UtString {

    public static String replace(String s, char from, String to) {
        return replace(s, "" + from, to);
    }

    public static String replace(String s, String from, String to) {
        StringBuffer sb = new StringBuffer(s);
        int l1 = from.length();
        int l2 = to.length();
        int n = 0;
        int t = 0;
        int d = l2 - l1;
        while (true) {
            int p = s.indexOf(from, n);
            if (p >= 0) {
                sb.replace(p + t, p + t + l1, to);
                n = p + l1;
                t += d;
            } else {
                return sb.toString();
            }
        }
    }

    public static String toHex(int v) {
        char[] cs = new char[8];
        int p = 0;
        for (int i = 28; i >= 0; i -= 4) {
            int n = v >>> i & 0xf;
            cs[p++] = n >= 10 ? (char) (65 + (n - 10)) : (char) (48 + n);
        }

        return new String(cs);
    }

    public static String fillSpaces(double v, int n) {
        if (Double.isNaN(v))
            return "NaN";
        if (v == Double.MAX_VALUE) // 1.7976931348623157E+308D
            return "MAX_VALUE";
        if (v == Double.MIN_VALUE) // 4.9406564584124654E-324D
            return "MIN_VALUE";
        if (v == (1.0d / 0.0d))
            return "POSITIVE_INFINITY";
        if (v == (-1.0d / 0.0d))
            return "NEGATIVE_INFINITY";
        String s = fillZeros(v, n - 3);
        if (v < 0.0d)
            return fillChars(s, ' ', n);
        else
            return fillChars(" " + s, ' ', n);
    }

    public static String fillZeros(double v, int n) {
        try {
            if (n <= 0)
                return "" + (int) v;
        } catch (RuntimeException e) {
            System.err.println("" + v);
            e.printStackTrace();
            return "" + v;
        }
        DecimalFormat format = new DecimalFormat("#.###############");
        String s1 = format.format(v);
        int p = s1.lastIndexOf(".");
        String ret;
        if (p < 0) {
            String s2 = s1 + ".";
            ret = fillChars(s2, '0', s2.length() + n);
        } else if (p + n + 1 < s1.length())
            ret = s1.substring(0, p + n + 1);
        else
            ret = fillChars(s1, '0', p + n + 1);
        return ret;
    }

    public static String fillSpaces(String s, int n) {
        return fillChars(s, ' ', n);
    }

    public static String fillChars(String s, char c1, int n) {
        if (s.length() >= n)
            return s;
        try {
            char[] cs1 = s.toCharArray();
            int j = 0;
            for (int k = 0; k < cs1.length; k++)
                if (cs1[k] < '\177')
                    j++;
                else
                    j += 2;

            if (j < n) {
                char[] cs2 = new char[cs1.length + (n - j)];
                System.arraycopy(cs1, 0, cs2, 0, cs1.length);
                Arrays.fill(cs2, cs1.length, cs2.length, c1);
                cs1 = cs2;
            }
            return new String(cs1);
        } catch (RuntimeException e) {
            e.printStackTrace(System.err);
        }
        return s;
    }

    public static String read(URL url) throws IOException {
        return read(url.openStream(), (String) null);
    }

    public static String read(File file) throws IOException {
        return read(new FileInputStream(file), (String) null);
    }

    public static String read(File file, String s) throws IOException {
        return read(new FileInputStream(file), s);
    }

    public static String read(InputStream is, String s) throws IOException {
        return read(s != null ? (Reader) new InputStreamReader(is, s) : (Reader) new InputStreamReader(is));
    }

    public static String read(Reader reader) throws IOException {
        BufferedReader reader_ = null;
        try {
            reader_ = (reader instanceof BufferedReader) ? (BufferedReader) reader : new BufferedReader(reader);
            StringBuffer sb = new StringBuffer();
            boolean flag = true;
            String line;
            while ((line = reader_.readLine()) != null) {
                if (!flag)
                    sb.append('\n');
                else
                    flag = false;
                sb.append(line);
            }
            return sb.toString();
        } finally {
            try {
                if (reader_ != null)
                    reader_.close();
            } catch (Throwable t) {
                t.printStackTrace(System.err);
            }
        }
    }

    public static String toHex2(int v) {
        char[] cs = new char[8];
        for (int i = 0; i < 8; i++) {
            int n = v >>> i * 4 & 0xf;
            cs[7 - i] = (char) (n >= 10 ? (65 + n) - 10 : 48 + n);
        }

        return new String(cs);
    }

    public static String toHexString_(int i) {
        return toHex2(i);
    }

    public static int parseHexInt(String s) {
        long l = Long.parseLong(s, 16);
        return (int) (-1L & l);
    }
}
