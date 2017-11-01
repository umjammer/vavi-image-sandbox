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
public class SOS extends Segment {

    public SOS() {
        super("SOS");
    }

    public String toString() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(type);
            sb.append(", ");
            sb.append(size);
            sb.append('\n');

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

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            assert false;
            return null;
        }
    }
}

/* */
