/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.jpeg;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * DumpJpeg. jpeg analyzer.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060322 nsano initial version <br>
 */
public class JpegFile {

    /** */
    private static final Map<Integer, String> markers = new HashMap<Integer, String>() {{
        put(0x01, "TEM"); // テンポラリ・マーカ (算術圧縮)
        put(0xc0, "SOF0"); // 標準DCT圧縮
        put(0xc1, "SOF1"); // プログレッシブ DCT 圧縮
        put(0xc2, "SOF2"); 
        put(0xc3, "SOF3"); 
        put(0xc4, "DHT"); // DCTで算出された値のエンコード用に作られたハフマンテーブルの情報 
        put(0xc5, "SOF5"); 
        put(0xc6, "SOF6"); 
        put(0xc7, "SOF7"); 
        put(0xc8, "JPG"); 
        put(0xc9, "SOF9"); 
        put(0xca, "SOF10"); 
        put(0xcb, "SOF11"); 
        put(0xcc, "DAC"); 
        put(0xcd, "SOF13"); 
        put(0xce, "SOF14"); 
        put(0xcf, "SOF15"); 
//      put(0xd8, "SOI"); 
//      put(0xd9, "EOI"); 
//      put(0xda, "SOS"); // Start of Scan (Required)
        put(0xdb, "DQT"); // 量子化テーブル定義 (Required)
        put(0xdc, "DNL"); // ライン数定義
        put(0xdd, "DRI"); // イメージのリスタート間隔を定義する (Option)
        put(0xde, "DHP"); // ハフマン・テーブルの定義
        put(0xdf, "EXP"); // エクスパンド・セグメント
        put(0xe0, "APP0"); // 解像度情報など (Option)
        put(0xe1, "APP1"); // アプリケーション・マーカー・セグメント (Option)
        put(0xe2, "APP2"); // (Option)
        put(0xee, "APP14"); // (Option)
        put(0xef, "APP15"); // (Option)
        put(0xf0, "JPG0"); // JPEG 拡張用に予約
        put(0xf1, "APP1"); //
        put(0xfd, "JPG13"); //
        put(0xfe, "COM"); // コメント・マーカー (Option)
    }};

    public JpegFile(InputStream is) throws IOException {
        DataInputStream dis = new DataInputStream(is);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
        List<Segment> segments = new ArrayList<>();

        boolean inStream = false;
end:
        while (true) {
            int c;
            try {
                c = dis.readUnsignedByte();
            } catch (EOFException e) {
                if (inStream) {
                    System.err.println("EOI  : *** NOT FOUND ***");
                }
                break;
            }
            if (c == 0xff) {
                Segment segment = null;
                c = dis.readUnsignedByte();
//System.err.printf("%02X :", c);
                if (markers.containsKey(c)) {
                    // format II
                    segment = readSegment(dis, markers.get(c));
                } else {
                    // format I
                    switch (c) {
                    case 0x00: // 0xff
                        if (inStream) {
                            baos.write(0xff);
                        } else {
                            assert false : "[ff 00] at out of stream"; 
                        }
                        continue end;
                    case 0xd8: // SOI
                        segment = new Segment("SOI");
                        break;
                    case 0xd9: // EOI
                        segment = new Segment("EOI");
                        inStream = false;
System.err.printf("rest: %d bytes\n", dis.available());
System.err.printf("data: %d bytes\n", baos.size());
                        break end;
                    case 0xd0: // RSTm リスタート・マーカー (Option)
                    case 0xd1:
                    case 0xd2:
                    case 0xd3:
                    case 0xd4:
                    case 0xd5:
                    case 0xd6:
                    case 0xd7:
                        segment = new Segment("RST" + (c - 0xd0));
                        break;
                    case 0xda: // Start of Scan
                        segment = readSegment(dis, "SOS");
                        inStream = true;
                        break;
                    case 0xff: //
                        System.err.println("FF FF: undefined");
                        break;
                    default:
                        segment = readSegment(dis, String.format("FF %02X", c));
                        break;
                    }
                }
                segments.add(segment);
            } else if (inStream) {
                baos.write(c);
            } else {
                assert false : String.format("bad placed data: %02X", c);
            }
        }
        
        System.err.println();
        for (Segment segment : segments) {
            System.err.println(segment);
        }
    }

    Segment readSegment(DataInputStream dis, String type) throws IOException {
        int size = dis.readUnsignedShort();
        byte[] data = new byte[size - 2];
        dis.readFully(data);
        Segment segment = new Segment(type, size - 2, data);
        return segment;
    }
}

/* */
