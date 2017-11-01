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
public class APP0 extends Segment {

    public APP0() {
        super("APP0");
    }

    public String toString() {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(type);
            sb.append(", ");
            sb.append(size);
            sb.append('\n');

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

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            assert false;
            return null;
        }
    }
}

/* */
