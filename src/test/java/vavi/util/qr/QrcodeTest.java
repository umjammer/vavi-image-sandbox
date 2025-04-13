/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.qr;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


/**
 * QrcodeTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2018/02/25 umjammer initial version <br>
 */
public class QrcodeTest {

    @Test
    @Disabled
    public void test() {
        fail("Not yet implemented");
    }

    //----

    /** java Qrcode string */
    public static void main(String[] args) throws Exception {
        Qrcode qr = new Qrcode();
        qr.setErrorCorrectionLevel(Qrcode.ERROR_CORRECTION_LEVEL_M);
        qr.setEncoding(Qrcode.ENCODING_BYTE);
System.err.println(args[0]);
        boolean[][] matrix = qr.toQrcode(args[0].getBytes("Windows-31J"));

        for (int i = 0; i < matrix.length; i++) {
            for (boolean[] booleans : matrix) {
                if (booleans[i]) {
                    System.out.print("##");
                } else {
                    System.out.print("  ");
                }
            }
            System.out.print("\n");
        }
    }
}
