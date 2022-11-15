/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.resample.enlarge.noids.graphics.geom;

import java.awt.Point;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;


/**
 * UtVectorTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-08-31 nsano initial version <br>
 */
public class UtVectorTest {

    @Test
    void test1() {
        Point.Double p1 = new Point.Double(10d, 10d);
        Point.Double p2 = new Point.Double(20d, 20d);
        Vector2d v1 = new Vector2d(10f, 10f);
        Vector2d v2 = new Vector2d(0.0f, 10f);
        double[] r = UtVector.method1(p1, p2, v1, v2);
System.out.println("r[0] : " + r[0]);
System.out.println("r[1] : " + r[1]);
        assertArrayEquals(new double[] {-1.0, -0.0}, r);
    }
}
