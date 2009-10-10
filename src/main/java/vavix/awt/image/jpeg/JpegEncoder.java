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

        // DIB���
//        image_width = *(long  *)(dib+4);
//        image_height = *(long  *)(dib+8) ;

        // 24 bit �J���[�ɕϊ�
//        new_dib = bmp_convert_dib_24color( dib ) ;
        if( new_dib == null ) return false;
        dib = new_dib;

        /* JPEG���k�I�u�W�F�N�g�����蓖�ĂāA������ */
        cinfo.err = jpegLibrary.jpeg_std_error(jerr.getPointer());
        jpegLibrary.jpeg_create_compress(cinfo.getPointer());

        jpegLibrary.jpeg_stdio_dest(cinfo.getPointer(), /*fp*/null);

        /* ���k�p�̃p�����[�^��ݒ� */
        cinfo.image_width  = image_width;     /* �C���[�W�̃s�N�Z�����ƍ��� */
        cinfo.image_height = image_height;
        cinfo.input_components = 3;           /* �s�N�Z��������̐F */
        cinfo.in_color_space = JpegLibrary.jpeg_compress_struct.JCS_RGB;       /* �J���[�X�y�[�X */

        /* �f�t�H���g���k�p�����[�^��ݒ肷�� */
        jpegLibrary.jpeg_set_defaults(cinfo.getPointer());

        /* ��f�t�H���g�p�����[�^�̐ݒ� */
        jpegLibrary.jpeg_set_quality(cinfo.getPointer(), quality, -1);

        /* Progression �I�v�V������ǉ�  */
        if( progression )  jpegLibrary.jpeg_simple_progression (cinfo.getPointer());

        /* ���k�J�n */
        jpegLibrary.jpeg_start_compress(cinfo.getPointer(), -1);

        row_stride = image_width * 3; /* JSAMPLEs per row in image_buffer */
        image_buffer = new byte[row_stride];
        bmp_row_bytes = (image_width * 3 + 3) / 4 * 4;  // DWORD ���E
        bmp_image = /*dib*/ 40; // = sizeof(BITMAPINFOHEADER)

        i = image_height - 1;
        while (cinfo.next_scanline < cinfo.image_height) {
            // BMP �͊i�[�`�����㉺�t�B
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

        /* ���k�I�� */
        jpegLibrary.jpeg_finish_compress(cinfo.getPointer());

        /* JPEG ���k�I�u�W�F�N�g�̊J�� */
        jpegLibrary.jpeg_destroy_compress(cinfo.getPointer());

//        free( image_buffer );
        image_buffer = null;
//        free( new_dib );
        return true;
    }
}

/* */
