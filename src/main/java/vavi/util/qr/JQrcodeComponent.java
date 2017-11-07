/*
 * Copyright (c) 2004 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.util.qr;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;


/**
 * JQrcodeComponent.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 040912 nsano initial version <br>
 */
public class JQrcodeComponent extends JComponent {

    /** */
    private Image image;

    /**
     *
     * @param string use encoding
     * @param size size of one dot of QR code
     */
    public JQrcodeComponent(String string, int size) {
        this(string, size, System.getProperty("file.encoding"));
    }

    /**
     *
     * @param string use encoding
     * @param size size of one dot of QR code
     * @param encoding for string
     */
    public JQrcodeComponent(String string, int size, String encoding) {
        Toolkit t = Toolkit.getDefaultToolkit();
        this.image = t.createImage(new QrcodeImageSource(string, size, "Windows-31J"));

        int width = image.getWidth(null);
        setPreferredSize(new Dimension(width + size, width + size));
    }

    /** */
    public void update(Graphics g) {
        Border border = getBorder();
        int l = 0;
        int t = 0;
        if (border == null) {
            l = 0;
            t = 0;
        } else {
            Insets i = border.getBorderInsets(this);
            l = i.left;
            t = i.top;
        }
        if (image != null) {
            g.drawImage(image, l, t, this);
        }
    }

    /** */
    public void paint(Graphics g) {
        super.paint(g);
//        Dimension d = getSize();
//        g.clearRect(0, 0, d.width, d.height);
        update(g);
    }

    //----

    /** */
    public static void main(String[] args) throws Exception {
        int times = Integer.parseInt(args[1]);
        JQrcodeComponent component = new JQrcodeComponent(args[0], times, "Windows-31J");
        component.setBorder(new LineBorder(Color.white, times / 2));

        JFrame frame = new JFrame();
        frame.setTitle(args[0] +  " x " + times);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(component);
        frame.pack();
        frame.setVisible(true);
    }
}

/* */

