/*
 * Copyright (c) 2009 by KLab Inc., All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.color;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import org.junit.Test;

import vavix.awt.image.color.FillTransparentIndexOp;
import vavix.awt.image.pixel.SimpleDrawOp;


/**
 * FillTransparentIndexOpTest. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/10 nsano initial version <br>
 */
public class FillTransparentIndexOpTest {

    BufferedImage image;

    public FillTransparentIndexOpTest() throws IOException {
        this.image = ImageIO.read(FillTransparentIndexOpTest.class.getResourceAsStream("/sample.gif"));
    }

    @Test
    public void test01() {
        BufferedImage image2 = new FillTransparentIndex2Op(image).filter(image, null);
        BufferedImage image3 = new SimpleDrawOp(40, 40, image.getWidth() - 80, image.getHeight() - 80).filter(image, image2);
System.err.println("before: " + image.getType() + ", " + image.getColorModel().getColorSpace());
IndexColorModel icm = IndexColorModel.class.cast(image.getColorModel());
int mapSize = icm.getMapSize();
byte[] rs = new byte[mapSize];
byte[] gs = new byte[mapSize];
byte[] bs = new byte[mapSize];
icm.getReds(rs);
icm.getGreens(gs);
icm.getBlues(bs);
for (int i = 0; i < mapSize; i++) {
 System.err.printf("[%03d] %02x%02x%02x\n", i, rs[i], gs[i], bs[i]);   
}
System.err.println("after: " + image3.getType() + ", " + image3.getColorModel().getColorSpace());
icm = IndexColorModel.class.cast(image3.getColorModel());
mapSize = icm.getMapSize();
rs = new byte[mapSize];
gs = new byte[mapSize];
bs = new byte[mapSize];
icm.getReds(rs);
icm.getGreens(gs);
icm.getBlues(bs);
for (int i = 0; i < mapSize; i++) {
 System.err.printf("[%03d] %02x%02x%02x\n", i, rs[i], gs[i], bs[i]);   
}
JOptionPane.showMessageDialog(null, new ImageIcon(image3), "fillTrancerateIndex 01", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(image));
    }

    int leastColor = 10;

    @Test
    public void test02() {
        BufferedImage targetImage = image;

        int w = image.getWidth() + 80;
        int h = image.getHeight() + 80;
        BufferedImage dst;

        IndexColorModel icm = IndexColorModel.class.cast(targetImage.getColorModel());
        int trans = icm.getTransparentPixel();
        if (trans != -1) {
            // 1. 透明色が設定されていればそれを使用する
            dst = new BufferedImage(icm, icm.createCompatibleWritableRaster(w, h), icm.isAlphaPremultiplied(), null);
System.err.printf("trans defined: %d\n",  trans);
        } else {
            int mapSize = icm.getMapSize();
            byte[] rs = new byte[mapSize];
            byte[] gs = new byte[mapSize];
            byte[] bs = new byte[mapSize];
            icm.getReds(rs);
            icm.getGreens(gs);
            icm.getBlues(bs);

            // index, count
            Map<Integer, Integer> colorMap = new HashMap<>();
            int tw = targetImage.getWidth();
            int th = targetImage.getHeight();
            int[] data = targetImage.getData().getPixels(0, 0, tw, th, (int[]) null);
            for (int y = 0; y < th; y++) {
                for (int x = 0; x < tw; x++) {
                    int index = data[y * tw + x];
                    if (!colorMap.containsKey(index)) {
System.err.printf("used index: %d\n", index);
                        colorMap.put(index, 1);
                    } else {
                        colorMap.put(index, colorMap.get(index) + 1);
                    }
                }
            }
System.err.println("used colors: " + colorMap.size());

            //
            int unusedColor = -1;
            if (colorMap.size() < icm.getMapSize()) {
System.err.println("unused colors: " + (icm.getMapSize() - colorMap.size()));
                for (int i = 0; i < icm.getMapSize(); i++) {
                    if (!colorMap.containsKey(i)) {
                        unusedColor = i;
System.err.printf("unused color: %d\n", i);
                        break;
                    }
                }
            }

            if (unusedColor != -1) {
                // 2. 使用されていないパレットがあれば
                //    そのパレットを透明色にする
                trans = unusedColor;
                IndexColorModel newIcm = new IndexColorModel(icm.getPixelSize(), mapSize, rs, gs, bs, trans);
                dst = new BufferedImage(newIcm, newIcm.createCompatibleWritableRaster(w, h), newIcm.isAlphaPremultiplied(), null);
System.err.printf("trans unused color: %d %08x, %d/%d\n", trans, getRgb(trans, rs, bs, gs), icm.getMapSize() - colorMap.size(), colorMap.size());
            } else {
                List<Entry<Integer, Integer>> list = new ArrayList<>(colorMap.entrySet());
                Collections.sort(list, new Comparator<Entry<Integer, Integer>>() {
                    @Override
                    public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
                        return o2.getValue() - o1.getValue();
                    }
                });
//System.err.printf("most used color: %d: %08x count %d\n", list.get(0).getKey(), getRgb(list.get(0).getKey(), rs, gs, bs), list.get(0).getValue());
//System.err.printf("last used color: %d: %08x count %d\n", list.get(list.size() - 1).getKey(), getRgb(list.get(list.size() - 1).getKey(), rs, gs, bs), list.get(list.size() - 1).getValue());

                if (list.get(list.size() - 1).getValue() < leastColor) {
                    // 3. パレットのピクセル数が #leastColor 以下の場合
                    //    透明色になってもらう
                    trans = list.get(list.size() - 1).getKey();
                    IndexColorModel newIcm = new IndexColorModel(icm.getPixelSize(), mapSize, rs, gs, bs, trans);
                    dst = new BufferedImage(newIcm, newIcm.createCompatibleWritableRaster(w, h), newIcm.isAlphaPremultiplied(), null);
System.err.printf("trans last used color: %d %08x\n",  trans, getRgb(trans, rs, bs, gs));
                } else {
                    // 4. パレットのピクセル数がいちばん多い色で塗りつぶす
                    trans = list.get(0).getKey();
                    dst = new BufferedImage(icm, icm.createCompatibleWritableRaster(w, h), icm.isAlphaPremultiplied(), null);
//System.err.printf("fill most used color %d, %08x\n", trans, getRgb(trans, rs, bs, gs));
                }
            }
        }

        Graphics g = dst.getGraphics();
        g.setColor(Color.red);

        int[] pixels = new int[w * h];
        Arrays.fill(pixels, trans); // TODO why trans is index???
        dst.setRGB(0, 0, w, h, pixels, 0, w);

        g.drawRect(0, 0, w - 1, h - 1);
JOptionPane.showMessageDialog(null, new ImageIcon(dst), "fillTrancerateIndex 02 1", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(image));

        pixels = dst.getRaster().getPixels(0, 0, w, h, (int[]) null);
        Arrays.fill(pixels, trans);
        dst.getRaster().setPixels(0, 0, w, h, pixels);

        g.drawRect(0, 0, w - 1, h - 1);
JOptionPane.showMessageDialog(null, new ImageIcon(dst), "fillTrancerateIndex 02 2", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(image));
    }

    @Test
    public void test03() {
        BufferedImageOp filter = new SimpleDrawOp(40, 40, image.getWidth() + 80, image.getHeight() + 80); 
        BufferedImage dst = null;
        if (IndexColorModel.class.isInstance(image.getColorModel())) {
            BufferedImage temp = filter.createCompatibleDestImage(image, image.getColorModel());
//System.err.println(image.getColorModel());
            dst = new FillTransparentIndexOp(image).filter(temp, null);
JOptionPane.showMessageDialog(null, new ImageIcon(dst), "fillTrancerateIndex 03 1 0", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(image));
            temp.flush();
        }
        BufferedImage tempImage = filter.filter(image, dst);
JOptionPane.showMessageDialog(null, new ImageIcon(tempImage), "fillTrancerateIndex 03 1", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(image));

        filter = new SimpleDrawOp(40, 40, image.getWidth() + 80, image.getHeight() + 80); 
        dst = null;
        if (IndexColorModel.class.isInstance(image.getColorModel())) {
            BufferedImage temp = filter.createCompatibleDestImage(image, image.getColorModel());
//System.err.println(image.getColorModel());
            dst = new FillTransparentIndex2Op(image).filter(temp, null);
JOptionPane.showMessageDialog(null, new ImageIcon(dst), "fillTrancerateIndex 03 2 0", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(image));
            temp.flush();
        }
        tempImage = filter.filter(image, dst);
JOptionPane.showMessageDialog(null, new ImageIcon(tempImage), "fillTrancerateIndex 03 2", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(image));
    }

    /** */
    private int getRgb(int i, byte[] rs, byte[] gs, byte[] bs) {
        return ((rs[i] & 0xff) << 16) |
               ((gs[i] & 0xff) << 8) |
                (bs[i] & 0xff);   
    }
}

/* */
