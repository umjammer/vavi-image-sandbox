/*
 * Copyright (c) 2009 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.jpeg;

import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;


/**
 * LibJpeg.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/07/03 nsano initial version <br>
 */
public interface JpegLibrary extends Library {

    /** */
    JpegLibrary INSTANCE = null;

    /** */
    class jpeg_compress_struct extends Structure {

        static final String JCS_RGB = null;

        public int image_width;
        public int image_height;
        public int input_components;

        public Object in_color_space;

        public Object err;

        public int next_scanline;
    }

    /** */
    class jpeg_error_mgr extends Structure {

    }

    /**
     * @param pointer
     */
    void jpeg_set_defaults(Pointer pointer);

    /**
     * @param pointer
     */
    void jpeg_create_compress(Pointer pointer);

    /**
     * @param pointer
     * @return
     */
    Object jpeg_std_error(Pointer pointer);

    /**
     * @param pointer
     * @param quality
     * @param b
     */
    void jpeg_set_quality(Pointer pointer, int quality, int b);

    /**
     * @param pointer
     */
    void jpeg_simple_progression(Pointer pointer);

    /**
     * @param pointer
     * @param b
     */
    void jpeg_start_compress(Pointer pointer, int b);

    /**
     * @param pointer
     */
    void jpeg_finish_compress(Pointer pointer);

    /**
     * @param pointer
     */
    void jpeg_destroy_compress(Pointer pointer);

    /**
     * @param cinfo
     * @param fp
     */
    void jpeg_stdio_dest(Pointer cinfo, Pointer fp);

    /**
     * @param pointer
     * @param row_pointer
     * @param i
     */
    void jpeg_write_scanlines(Pointer pointer, Pointer row_pointer, int i);
}
