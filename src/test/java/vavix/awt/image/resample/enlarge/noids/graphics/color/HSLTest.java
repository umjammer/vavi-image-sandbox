/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.resample.enlarge.noids.graphics.color;

import org.junit.jupiter.api.Test;
import vavix.awt.image.resample.enlarge.noids.util.UtString;


/**
 * HSLTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-08-31 nsano initial version <br>
 */
public class HSLTest {

    @Test
    void test1() {
        main(null);
    }

    public static void main(String[] args) {
        int[] colors = UtColor.getDefaultColorTable();
        for (int c : colors) {
            double[] hsl = HSL.toHsl(c, null);
            System.out.print("0x" + UtString.toHex(c) + " : ");
            System.out.print("  " + UtColor.toString(hsl));
            int rgb = HSL.toRgb(hsl[0], hsl[1], hsl[2]);
            System.out.println("    RGB : " + UtString.toHexString_(rgb));
        }
    }
}
