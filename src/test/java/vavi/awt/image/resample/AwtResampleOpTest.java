/*
 * Copyright (c) 2009 by KLab Inc., All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.resample;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * AwtResampleOpTest. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/15 nsano initial version <br>
 */
public class AwtResampleOpTest {

    class JImageComponent extends JComponent {

        /** */
        private Image image;

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
            update(g);
        }

        /** */
        public void setImage(Image image) {
            synchronized (image) {
                this.image = image;
            }
        }
    }

    BufferedImage rightImage;

    BufferedImage leftImage;

    JSlider slider;

    JImageComponent rightImageComponent;

    JImageComponent leftImageComponent;

    JLabel statusLabel;

//    String input = "erika.jpg";
    String input = "voiceInboundCall.jpg";
    
    void test00() throws Exception {
        BufferedImage image = ImageIO.read(AwtResampleOpTest.class.getResource(input));
        int w = image.getWidth();
        int h = image.getHeight();
System.err.println(w + ", " + h);

        leftImage = image;
        rightImage = image;
        slider = new JSlider();
        slider.setMaximum(100);
        slider.setMinimum(1);
        slider.setValue(100);
        slider.addChangeListener(new ChangeListener() {
            ImageWriter iw = ImageIO.getImageWritersByFormatName("JPEG").next(); // ちょっと適当か？
            {
                String className = "com.sun.imageio.plugins.jpeg.JPEGImageWriter";
                Class<?> clazz;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("no such ImageWriter: " + className);
                }
                Iterator<ImageWriter> iws = ImageIO.getImageWritersByFormatName("JPEG");
                while (iws.hasNext()) {
                    ImageWriter tmpIw = iws.next();
                    // BUG? JPEG の ImageWriter が Thread Safe じゃない気がする
                    if (clazz.isInstance(tmpIw)) {
                        iw = tmpIw;
System.err.println("ImageWriter: " + iw.getClass());
                        break;
                    }
                }
                if (iw == null) {
                    throw new IllegalStateException("no suitable ImageWriter");
                }
            }
            public void stateChanged(ChangeEvent event) {
                JSlider source = (JSlider) event.getSource();
                if (source.getValueIsAdjusting()) {
                    return;
                }
                float scale = source.getValue() / 100f;

                // left
                BufferedImage image = leftImage;
                BufferedImageOp filter = new AwtResampleOp(scale, scale);
long t = System.currentTimeMillis();
                BufferedImage filteredImage = filter.filter(image, null);
System.err.println("left: " + (System.currentTimeMillis() - t) + "ms");
                leftImageComponent.setImage(filteredImage);
                leftImageComponent.repaint();

                // right
                image = rightImage;
                filter = new AwtResampleOp(scale, scale, Image.SCALE_SMOOTH);
//                BufferedImage dst = new BufferedImage((int) (image.getWidth() * scale), (int) (image.getHeight() * scale), BufferedImage.TYPE_INT_ARGB);
                BufferedImage dst = null;
t = System.currentTimeMillis();
                filteredImage = filter.filter(image, dst); 
System.err.println("right: " + (System.currentTimeMillis() - t) + "ms");
                rightImageComponent.setImage(filteredImage);
                rightImageComponent.repaint();

System.err.println("scale: " + scale);
                statusLabel.setText("scale: " + scale);
            }
        });

        JPanel basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());
        basePanel.add(slider, BorderLayout.NORTH);

        leftImageComponent = new JImageComponent();
        leftImageComponent.setImage(leftImage);
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(w, h));
        leftPanel.add(leftImageComponent, BorderLayout.CENTER);

        rightImageComponent = new JImageComponent();
        rightImageComponent.setImage(rightImage);
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setPreferredSize(new Dimension(w, h));
        rightPanel.add(rightImageComponent, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane();
        split.setLeftComponent(leftPanel);
        split.setRightComponent(rightPanel);
        split.setPreferredSize(new Dimension(800, 600));

        basePanel.add(split, BorderLayout.CENTER);
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(basePanel);

        statusLabel = new JLabel();
        statusLabel.setText("original");
        basePanel.add(statusLabel, BorderLayout.SOUTH);

        JFrame frame = new JFrame();
        frame.setTitle("same color model | new color model");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(scrollPane);
        frame.pack();
        split.setDividerLocation(0.5);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        new AwtResampleOpTest().test00();
    }
}

/* */
