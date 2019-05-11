/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vavi.util.Debug;
import vavi.util.StringUtil;


/**
 * DumpJpeg. jpeg analyzer.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060322 nsano initial version <br>
 */
public class DumpJpeg {

    /** */
    public static void main(String[] args) throws Exception {
         new DumpJpeg(args[0]);
    }

    /** */
    static final Map<Integer, String> markers = new HashMap<Integer, String>() {{
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

    DumpJpeg(String file) throws IOException {
System.err.println(file);
        InputStream is = new BufferedInputStream(new FileInputStream(file));
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

    static class Segment {
        String type;
        int size;
        byte[] data;
        Segment(String type) {
            this.type = type;
            this.size = 0;
            this.data = null;
System.err.printf("%-5s: format I\n", type);
        }
        Segment(String type, int size, byte[] data) {
            this.type = type;
            this.size = size;
            this.data = data;
System.err.printf("%-5s: format II : %5d\n", type, size + 2);
        }
        public String toString() {
            try {
                StringBuilder sb = new StringBuilder();
                sb.append(type);
                sb.append(", ");
                sb.append(size);
                sb.append('\n');
                if (type.equals("APP0")) {
                    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
                    int c1 = dis.readUnsignedByte();
                    int c2 = dis.readUnsignedByte();
                    int c3 = dis.readUnsignedByte();
                    int c4 = dis.readUnsignedByte();
                    int c5 = dis.readUnsignedByte();
                    sb.append("  ID: "); // ASCII 文字で "JFIF" とヌル終端
                    sb.append((char) c1);
                    sb.append((char) c2);
                    sb.append((char) c3);
                    sb.append((char) c4);
                    sb.append('#');
                    sb.append(c5);
                    sb.append('\n');
                    int formatVersion = dis.readUnsignedShort();
                    sb.append("  Ver: "); // JFIF のバージョン
                    sb.append(formatVersion);
                    sb.append('\n');
                    int unit = dis.readUnsignedByte();
                    sb.append("  U: "); // 解像度単位 (0: 単位なし、アスペクト比を表す 1: dpi, 2: dpcm)
                    sb.append(unit);
                    sb.append('\n');
                    int horizontalDencity = dis.readUnsignedShort();
                    sb.append("  Xd: "); // 横解像度
                    sb.append(horizontalDencity);
                    sb.append('\n');
                    int verticalDencity = dis.readUnsignedShort();
                    sb.append("  Yd: "); // 縦解像度
                    sb.append(verticalDencity);
                    sb.append('\n');
                    int thambnailWidth = dis.readUnsignedByte();
                    sb.append("  Xt: "); // サムネイル横サイズ
                    sb.append(thambnailWidth);
                    sb.append('\n');
                    int thambnailHeight = dis.readUnsignedByte();
                    sb.append("  Yt: "); // サムネイル縦サイズ
                    sb.append(thambnailHeight);
                    sb.append('\n');
                } else if (type.equals("COM")) {
                    sb.append(StringUtil.getDump(data, 1, data.length - 1));
                } else if (type.equals("SOF0")) {
                    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
                    int samplingPrecision = dis.readUnsignedByte();
                    sb.append("  P: "); // 成分数
                    sb.append(samplingPrecision);
                    sb.append('\n');
                    int verticalSize = dis.readUnsignedShort();
                    sb.append("  Y: "); // 画像縦サイズ
                    sb.append(verticalSize);
                    sb.append('\n');
                    int horizontalSize = dis.readUnsignedShort();
                    sb.append("  X: "); // 画像横サイズ
                    sb.append(horizontalSize);
                    sb.append('\n');
                    int samplingCount = dis.readUnsignedByte();
                    sb.append("  Nf: "); // 成分数
                    sb.append(samplingCount);
                    sb.append('\n');
                    for (int i = 0; i < samplingCount; i++) {
                        int article = dis.readUnsignedByte();
                        sb.append("    C" + (i + 1) + ": "); // 成分ID
                        sb.append(article);
                        sb.append('\n');
                        int factor = dis.readUnsignedByte();
                        sb.append("    H" + (i + 1) + ": "); // 水平サンプリング値
                        sb.append((factor & 0xf0) >> 4);
                        sb.append('\n');
                        sb.append("    V" + (i + 1) + ": "); // 垂直サンプリング値
                        sb.append(factor & 0x0f);
                        sb.append('\n');
                        int tableSelector = dis.readUnsignedByte();
                        sb.append("    Tq" + (i + 1) + ": "); // 対応量子化テーブル番号
                        sb.append(tableSelector);
                        sb.append('\n');
                    }
                } else if (type.equals("SOS")) {
                    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
                    int nsCount = dis.readUnsignedByte();
                    sb.append("  Ns: "); // 成分数
                    sb.append(nsCount);
                    sb.append('\n');
                    for (int i = 0; i < nsCount; i++) {
                        int cs = dis.readUnsignedByte();
                        sb.append("    Cs" + (i + 1) + ": "); // 成分ID
                        sb.append(cs);
                        sb.append('\n');
                        int td_ta = dis.readUnsignedByte();
                        sb.append("    Ta" + (i + 1) + ": "); // DC 成分ハフマンテーブル番号
                        sb.append((td_ta & 0xf0) >> 4);
                        sb.append('\n');
                        sb.append("    Td" + (i + 1) + ": "); // AC 成分ハフマンテーブル番号
                        sb.append(td_ta & 0x0f);
                        sb.append('\n');
                    }
                    int ss = dis.readUnsignedByte();
                    sb.append("    Ss: "); // 量子化係数開始番号
                    sb.append(ss);
                    sb.append('\n');
                    int se = dis.readUnsignedByte();
                    sb.append("    Se: "); // 量子化係数終了番号
                    sb.append(se);
                    sb.append('\n');
                    int a = dis.readUnsignedByte();
                    sb.append("    Ah: "); // 前回のスキャンの係数値分割シフト量 (最初のスキャンは 0)
                    sb.append((a & 0xf0) >> 4);
                    sb.append('\n');
                    sb.append("    Al: "); // 係数値分割シフト量 (最後のスキャンは 0)
                    sb.append(a & 0x0f);
                    sb.append('\n');
                } else if (type.equals("DQT")) {
                    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
                    int pq_tq = dis.readUnsignedByte();
                    sb.append("    Pq: "); // 量子化テーブル精度 0: 8bit, 1: 16bit 通常8bit
                    sb.append((pq_tq & 0xf0) >> 4);
                    sb.append('\n');
                    sb.append("    Tq: "); // 量子化テーブル番号 0 から 3 までのいずれか
                    sb.append(pq_tq & 0x0f);
                    sb.append('\n');
                    sb.append(StringUtil.getDump(data, 1, data.length - 1)); // 量子化テーブル 64 個の量子化係数が記録される
                } else if (type.equals("DHT")) {
                    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
                    int tc_th = dis.readUnsignedByte();
                    sb.append("    Tc: "); // ハフマンテーブルクラス 0: DC 成分, 1: AC 成分
                    sb.append((tc_th & 0xf0) >> 4);
                    sb.append('\n');
                    sb.append("    Th: "); // ハフマンテーブル番号 0 から 3 までのいずれか
                    sb.append(tc_th & 0x0f);
                    sb.append('\n');
                    int[] l = new int[16];
                    int c = 0;
                    for (int i = 0; i < 16; i++) {
                        l[i] = dis.readUnsignedByte();
                        sb.append("    L" + (i + 1) + ": "); // ハフマンテーフルビット配分数
                        sb.append(l[i]);
                        sb.append('\t');
                        for (int j = 0; j < Math.min(16, l[i]); j++) {
                            // ハフマンテーブル定義データ
                            // DC 成分 データービット数
                            // AC 成分 上位 4bit ランレングス数
                            // 下位 4bit データービット数
                            sb.append(String.format("%02X ", data[17 + c + j]));
                        }
                        if (l[i] > 16) {
                            sb.append("...");
                        }
                        sb.append('\n');
                        c += l[i];
                    }
                } else if (type.equals("APP1")) {
                    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
                    sb.append(StringUtil.getDump(dis));
                    sb.append('\n');
                } else {
                }
                return sb.toString();
            } catch (IOException e) {
                e.printStackTrace(System.err);
                assert false;
                return null;
            }
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
