/*
 * Copyright (c) 2009 by KLab Inc., All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.resample;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.junit.Ignore;
import org.junit.Test;


/**
 * G2dResampleOpTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/10 nsano initial version <br>
 */
@Ignore
public class Java2DResampleOpTest {

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

    void test00() throws Exception {
        BufferedImage image = ImageIO.read(Java2DResampleOpTest.class.getResource("erika.jpg"));
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
                BufferedImageOp filter = new SimpleJava2DResampleOp(scale, scale);
                long t = System.currentTimeMillis();
                BufferedImage filteredImage = filter.filter(image, null);
                System.err.println("left: " + (System.currentTimeMillis() - t) + "ms");
                leftImageComponent.setImage(filteredImage);
                leftImageComponent.repaint();

                // right
                image = rightImage;
                filter = new G2dResampleOp(scale, scale);
                t = System.currentTimeMillis();
                filteredImage = filter.filter(image, null);
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
        frame.setTitle("AwtResampleOp | G2dResampleOp");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(scrollPane);
        frame.pack();
        split.setDividerLocation(0.5);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        new Java2DResampleOpTest().test00();
    }

    @Test
    public void test01() throws Exception {

        // Create the source image
        BufferedImage srcImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = srcImage.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, 50, 50);
        g2.setColor(Color.YELLOW);
        g2.fillOval(5, 5, 40, 40);
        g2.setColor(Color.BLACK);
        g2.fillOval(15, 15, 5, 5);
        g2.fillOval(30, 15, 5, 5);
        g2.drawOval(20, 30, 10, 7);
        g2.dispose();

        // Render the image untransformed
JOptionPane.showMessageDialog(null, null, "Untransformed", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(srcImage));

        BufferedImage image = new BufferedImage(100, 640, BufferedImage.TYPE_INT_RGB);
        g2 = image.createGraphics();

        // Render the image rotated (with NEAREST_NEIGHBOR)
        AffineTransform xform = new AffineTransform();
        xform.setToIdentity();
        xform.translate(15, 30);
        xform.rotate(Math.PI / 8, 25, 25);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.drawImage(srcImage, xform, null);

JOptionPane.showMessageDialog(null, new ImageIcon(srcImage), "Nearest Neighbor", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(image));

        // Render the image rotated (with BILINEAR)
        xform.setToIdentity();
        xform.translate(15, 90);
        xform.rotate(Math.PI / 8, 25, 25);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImage, xform, null);

JOptionPane.showMessageDialog(null, new ImageIcon(srcImage), "Bilinear", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(image));

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setPaint(new TexturePaint(srcImage, new Rectangle2D.Float(0, 0, srcImage.getWidth(), srcImage.getHeight())));
        xform.setToIdentity();
        xform.translate(15, 150);
        xform.rotate(Math.PI / 8, srcImage.getWidth() / 2, srcImage.getHeight() / 2);
        g2.transform(xform);
        g2.fillRect(0, 0, srcImage.getWidth(), srcImage.getHeight());

JOptionPane.showMessageDialog(null, new ImageIcon(srcImage), "Bilinear, Antialiased", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(image));

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g2.setPaint(new TexturePaint(srcImage, new Rectangle2D.Float(0, 0, srcImage.getWidth(), srcImage.getHeight())));
        xform.setToIdentity();
        xform.translate(0, 90);
//        xform.rotate(Math.PI / 8, srcImage.getWidth() / 2, srcImage.getHeight() / 2);
        g2.transform(xform);
        g2.fillRect(0, 0, srcImage.getWidth(), srcImage.getHeight());

JOptionPane.showMessageDialog(null, new ImageIcon(srcImage), "Nearest Neighbor, Antialiased", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(image));
    }
}

/* */
