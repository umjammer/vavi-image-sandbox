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
import vavi.util.StringUtil;


/**
 * DHT.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2015/12/17 umjammer initial version <br>
 */
public class DQT extends Segment {

    public DQT() {
        super("DQT");
    }

    public String toString() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(type);
            sb.append(", ");
            sb.append(size);
            sb.append('\n');

            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
            int pq_tq = dis.readUnsignedByte();
            sb.append("    Pq: "); // 量子化テーブル精度 0: 8bit, 1: 16bit 通常8bit
            sb.append((pq_tq & 0xf0) >> 4);
            sb.append('\n');
            sb.append("    Tq: "); // 量子化テーブル番号 0 から 3 までのいずれか
            sb.append(pq_tq & 0x0f);
            sb.append('\n');
            sb.append(StringUtil.getDump(data, 1, data.length - 1)); // 量子化テーブル 64 個の量子化係数が記録される

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            assert false;
            return null;
        }
    }

}
