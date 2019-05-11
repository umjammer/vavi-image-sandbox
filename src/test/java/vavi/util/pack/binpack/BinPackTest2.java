/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.pack.binpack;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.jupiter.api.Disabled;


/**
 * t4
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (vavi)
 * @version 0.00 020427 vavi initial version <br>
 */
@Disabled
public class BinPackTest2 {

    public static void main(String[] args) throws Exception {
        BinPackTest2 app = new BinPackTest2();
        app.a2(args);
    }

    public void a2(String[] args) throws Exception {
        BinPacker packer = new BinPacker();
        final Random random = new Random(System.currentTimeMillis());
        final List<BinPacker.Dim> ds = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            int w = random.nextInt(30) + 16;
            int h = random.nextInt(30) + 16;
            ds.add(new BinPacker.Dim(w, h));
        }
        final int SIZE = 800;
        final List<List<BinPacker.Rect>> rss = packer.pack(ds, SIZE, true);
        int l = 0;
        for (List<BinPacker.Rect> rs : rss) {
            l += rs.size();
        }
System.err.println("pack: " + l + "/" + ds.size());
        JPanel panel = new JPanel() {
            {
                setOpaque(true);
                setBackground(Color.white);
                setPreferredSize(new Dimension(SIZE, SIZE));
            }
            public void paint(Graphics g) {
                g.setColor(Color.black);
                g.fillRect(0, 0, SIZE, SIZE);
                int c = 0;
                for (List<BinPacker.Rect> rs : rss) {
                    for (BinPacker.Rect r : rs) {
//System.err.printf("%d: %d, %d, %d, %d, %b\n", r.id, r.x, r.y, r.w, r.h, r);
                        g.setColor(new Color(random.nextInt(0xffffff)));
                        g.fillRect(r.x, r.y, r.w, r.h);
                        g.setColor(Color.red);
                        g.drawRect(r.x, r.y, r.w, r.h);
//                        g.drawString(String.valueOf(c++), (r.x + r.width) / 2, (r.y + r.height) / 2);
                    }
System.err.println("done: " + c++);
                    break;
                }
            }
        };
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }
}

/* */
