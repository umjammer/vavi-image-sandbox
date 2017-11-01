
package jp.noids.image.scaling;

import java.awt.RenderingHints;


/** k */
public interface Constants extends DirectionConstants {

    public static final Object hints = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

    public static final Class_a[] table_a = {
        new Class_a(1, 0, 28, true, false, false),
        new Class_a(1, 0, 28, false, true, false),
        new Class_a(1, 0, 28, false, false, true),
        new Class_a(1, -1, 25, true, false, false),
        new Class_a(1, -1, 23, false, false, true),
        new Class_a(1, 1, 25, true, false, false),
        new Class_a(1, 1, 23, false, true, false),
        new Class_a(1, -2, 14, true, false, false),
        new Class_a(1, -2, 16, false, false, true),
        new Class_a(1, 2, 14, true, false, false),
        new Class_a(1, 2, 10, false, true, false),
        new Class_a(0, -1, 14, true, true, true),
        new Class_a(0, -1, 28, false, false, true),
        new Class_a(0, 1, 14, true, true, true),
        new Class_a(0, 1, 28, false, true, false),
        new Class_a(2, 0, 20, true, false, false),
        new Class_a(2, 0, 8, false, true, false),
        new Class_a(2, 0, 8, false, false, true),
        new Class_a(2, -1, 18, true, false, false),
        new Class_a(2, -1, 10, false, false, true),
        new Class_a(2, 1, 18, true, false, false),
        new Class_a(2, 1, 16, false, true, false),
        new Class_a(0, -2, 6, true, true, true),
        new Class_a(0, -2, 8, false, false, true),
        new Class_a(0, 2, 6, true, true, true),
        new Class_a(0, 2, 8, false, true, false),
        new Class_a(-1, -1, 4, true, true, true),
        new Class_a(-1, 1, 4, true, true, true)
    };

    public static final int[] table_i = {
        5, 3, 1, -1, -2, -3, -4, -5, -6, -7, -8
    };
}
