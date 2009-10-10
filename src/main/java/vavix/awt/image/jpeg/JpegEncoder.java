/*
 * Copyright (c) 2009 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.jpeg;

import java.io.InputStream;

import vavix.awt.image.jpeg.JpegLibrary.jpeg_compress_struct;
import vavix.awt.image.jpeg.JpegLibrary.jpeg_error_mgr;


/**
 * JpegEncoder. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2009/07/03 nsano initial version <br>
 */
public class JpegEncoder {

    boolean jpg_save_dib_file(InputStream fp, byte[] dib, int quality, boolean progression ) {
        int   i, j;
        int   bmp_row_bytes;
        int bmp_image;
        int src, dest;
        byte[]   new_dib = null;

        JpegLibrary jpegLibrary = null;

        jpeg_compress_struct cinfo = null;
        jpeg_error_mgr jerr = null;

        byte[] image_buffer;
        int row_pointer;      /* pointer to JSAMPLE row[s] */
        int row_stride;               /* physical row width in image buffer */
        int image_height = 0;
        int image_width = 0;

        // DIB情報
//        image_width = *(long  *)(dib+4);
//        image_height = *(long  *)(dib+8) ;

        // 24 bit カラーに変換
//        new_dib = bmp_convert_dib_24color( dib ) ;
        if( new_dib == null ) return false;
        dib = new_dib;

        /* JPEG圧縮オブジェクトを割り当てて、初期化 */
        cinfo.err = jpegLibrary.jpeg_std_error(jerr.getPointer());
        jpegLibrary.jpeg_create_compress(cinfo.getPointer());

        jpegLibrary.jpeg_stdio_dest(cinfo.getPointer(), /*fp*/null);

        /* 圧縮用のパラメータを設定 */
        cinfo.image_width  = image_width;     /* イメージのピクセル幅と高さ */
        cinfo.image_height = image_height;
        cinfo.input_components = 3;           /* ピクセル当たりの色 */
        cinfo.in_color_space = JpegLibrary.jpeg_compress_struct.JCS_RGB;       /* カラースペース */

        /* デフォルト圧縮パラメータを設定する */
        jpegLibrary.jpeg_set_defaults(cinfo.getPointer());

        /* 非デフォルトパラメータの設定 */
        jpegLibrary.jpeg_set_quality(cinfo.getPointer(), quality, -1);

        /* Progression オプションを追加  */
        if( progression )  jpegLibrary.jpeg_simple_progression (cinfo.getPointer());

        /* 圧縮開始 */
        jpegLibrary.jpeg_start_compress(cinfo.getPointer(), -1);

        row_stride = image_width * 3; /* JSAMPLEs per row in image_buffer */
        image_buffer = new byte[row_stride];
        bmp_row_bytes = (image_width * 3 + 3) / 4 * 4;  // DWORD 境界
        bmp_image = /*dib*/ 40; // = sizeof(BITMAPINFOHEADER)

        i = image_height - 1;
        while (cinfo.next_scanline < cinfo.image_height) {
            // BMP は格納形式が上下逆。
            dest = 0/*image_buffer*/;
            src = /*bmp_image*/ i * bmp_row_bytes ;
            for( j = 0; j < image_width; j++ ) {
                image_buffer[dest++] = dib[bmp_image + src + 2];
                image_buffer[dest++] = dib[bmp_image + src + 1];
                image_buffer[dest++] = dib[bmp_image + src + 0];
                src += 3;
            }
            i--;

            row_pointer = 0 /*image_buffer*/;
            jpegLibrary.jpeg_write_scanlines(cinfo.getPointer(), null/*row_pointer*/, 1);
        }

        /* 圧縮終了 */
        jpegLibrary.jpeg_finish_compress(cinfo.getPointer());

        /* JPEG 圧縮オブジェクトの開放 */
        jpegLibrary.jpeg_destroy_compress(cinfo.getPointer());

//        free( image_buffer );
        image_buffer = null;
//        free( new_dib );
        return true;
    }
}

/* */
