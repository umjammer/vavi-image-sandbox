/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.resample.enlarge;


/**
 * float-vector-operations  plus scalar product
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/13 nsano initial version <br>
 */
interface Primitive<T extends Primitive<T>> {

    T operatorEqual(T p);

    T operatorPlusEqual(T p);

    T operatorMinusEqual(T p);

    T operatorMultiplyEqual(float a);

    T operatorPlus(T p);

    T operatorMinus(T p);

    T operatorMinus();

    float operatorMultiply(T p);

    T operatorMultiply(float a);

    boolean isZero();

    /** t / Norm(t) */
    T normalized();

    /** t / Norm(t) */
    void normalize();

    float norm();

    /** simple norm */
    float norm1();

    /** clamping */
    void clip();

    /** t += a * p */
    void addMul(float a, T p);

    void setZero();

    T operatorMultiply(float a, T p);
}

/* */
