/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.pack.arevalo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.junit.Ignore;


/**
 * t4
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (vavi)
 * @version 0.00 020427 vavi initial version <br>
 */
@Ignore
public class RectanglePackerTest2 {

    public static void main(String[] args) throws Exception {
        RectanglePackerTest2 app = new RectanglePackerTest2();
        app.a1(args);
    }

    public void a1(String[] args) throws Exception {
        RectanglePacker packer = new ArevaloRectanglePacker(200, 200);
        final Random random = new Random(System.currentTimeMillis());
        final List<Rectangle> rs = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Point p = new Point();
            int w = random.nextInt(148) + 16;
            int h = random.nextInt(148) + 16;
            if (packer.tryPack(w, h, p)) {
                rs.add(new Rectangle(p.x, p.y, w, h));
            } else {
                System.err.println(i + ": " + p.x + ", " + p.y);
            }
        }
        JPanel panel = new JPanel() {
            {
                setOpaque(true);
                setBackground(Color.white);
                setPreferredSize(new Dimension(640, 480));
            }
            public void paint(Graphics g) {
                g.setColor(Color.black);
                g.fillRect(0, 0, 640, 640);
                int c = 0;
                for (Rectangle r : rs) {
System.err.printf("%d, %d, %d, %d, %b\n", r.x, r.y, r.width, r.height, r);
                    g.setColor(new Color(random.nextInt(0x7fffffff), true));
                    g.fillRect(r.x, r.y, r.width, r.height);
                    g.setColor(Color.red);
                    g.drawRect(r.x, r.y, r.width, r.height);
                    g.setColor(Color.white);
                    g.drawString(String.valueOf(c++), (r.x + r.width) / 2, (r.y + r.height) / 2);
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
