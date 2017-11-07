/*
 * Copyright (c) 2015 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.jpeg.segment;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import vavi.awt.image.jpeg.Segment;


/**
 * DHT.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2015/12/17 umjammer initial version <br>
 */
public class DHT extends Segment {

    public DHT() {
        super("DHT");
    }

    public String toString() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(type);
            sb.append(", ");
            sb.append(size);
            sb.append('\n');

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

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            assert false;
            return null;
        }
    }

}

/* */
