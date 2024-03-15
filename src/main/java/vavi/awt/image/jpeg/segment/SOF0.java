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
public class SOF0 extends Segment {

    public SOF0() {
        super("SOF0");
    }

    public String toString() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(type);
            sb.append(", ");
            sb.append(size);
            sb.append('\n');

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
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            assert false;
            return null;
        }
    }

}
