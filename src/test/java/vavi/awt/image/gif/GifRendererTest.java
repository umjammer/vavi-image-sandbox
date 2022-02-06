/*
 * Copyright (c) 2009 by KLab Inc., All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.gif;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import vavi.imageio.IIOUtil;


/**
 * GifRendererTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/20 nsano initial version <br>
 */
public class GifRendererTest {

    static {
        IIOUtil.setOrder(ImageReaderSpi.class, "com.sun.imageio.plugins.gif.GIFImageReaderSpi", "vavi.imageio.gif.NonLzwGifImageReaderSpi");
    }

    @Test
    public void test01() throws Exception {

        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream iis = ImageIO.createImageInputStream(GifRendererTest.class.getResourceAsStream("/anigif.gif"));
        reader.setInput(iis, true);

        GifRenderer renderer = new GifRenderer();

        for (int i = 0;; i++) { // reader.getNumImages(B) sometimes got error
            BufferedImage image;
            try {
                image = reader.read(i);
            } catch (IndexOutOfBoundsException e) {
                break;
            }

            IIOMetadata imageMetaData = reader.getImageMetadata(i);

            BufferedImage renderedImage = renderer.addFrame(image, imageMetaData);
if (System.getProperty("vavi.test") == null)
 JOptionPane.showMessageDialog(null, new ImageIcon(image), "gif renderer", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(renderedImage));
        }

        iis.close();
    }

    @Test
    public void test02() throws Exception {
        // animation gif image should be created by {@link ImageIcon#init(URL)}, not by ImageIO
        Icon imageIcon = new ImageIcon(GifRendererTest.class.getResource("/sample.gif"));
        JLabel label = new JLabel(imageIcon);
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(label);
        frame.pack();
        frame.setVisible(true);
    }

    static class LoopCounter {
        int count = 0;
        final int max;
        LoopCounter(int max) {
            this.max = max;
        }
        void increment() {
            this.count = count < max - 1 ? count + 1 : 0;
        }
        int get() {
            return count;
        }
    }

    @Test
    public void test03() throws Exception {

        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream iis = ImageIO.createImageInputStream(GifRendererTest.class.getResourceAsStream("/anigif.gif"));
        reader.setInput(iis, true);

        GifRenderer renderer = new GifRenderer();

        List<BufferedImage> images = new ArrayList<>();
        List<Integer> delayTimes = new ArrayList<>();

        for (int i = 0;; i++) { // reader.getNumImages(B) sometimes got error
            BufferedImage image;
            try {
                image = reader.read(i);
            } catch (IndexOutOfBoundsException e) {
                break;
            }

            IIOMetadata imageMetaData = reader.getImageMetadata(i);

            images.add(renderer.addFrame(image, imageMetaData));
            delayTimes.add(renderer.getDelayTime(imageMetaData));
System.err.println(renderer.getDelayTime(imageMetaData));
        }

        iis.close();

        //
        LoopCounter counter = new LoopCounter(images.size());

        JPanel panel = new JPanel() {
            public void paintComponent(Graphics g) {
                g.drawImage(images.get(counter.get()), 0, 0, this);
            }
        };
        panel.setPreferredSize(new Dimension(images.get(0).getWidth(), images.get(0).getHeight()));

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = new Runnable() {
            public void run() {
                counter.increment();
                panel.repaint();
                scheduler.schedule(this, renderer.getDelayTime(counter.get()), TimeUnit.MILLISECONDS);
            }
        };
        panel.repaint();
        scheduler.schedule(task, renderer.getDelayTime(0), TimeUnit.MILLISECONDS);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    @Test
    public void test04() throws Exception {

        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream iis = ImageIO.createImageInputStream(GifRendererTest.class.getResourceAsStream("/anigif.gif"));
        reader.setInput(iis, true);

        GifRenderer renderer = new GifRenderer();

        List<BufferedImage> images = new ArrayList<>();

        for (int i = 0;; i++) { // reader.getNumImages(B) sometimes got error
            BufferedImage image;
            try {
                image = reader.read(i);
            } catch (IndexOutOfBoundsException e) {
                break;
            }

            IIOMetadata imageMetaData = reader.getImageMetadata(i);

            images.add(renderer.addFrame(image, imageMetaData));
        }

        iis.close();

        //
        LoopCounter counter = new LoopCounter(images.size());

        JPanel panel = new JPanel() {
            public void paintComponent(Graphics g) {
                g.drawImage(images.get(counter.get()), 0, 0, this);
            }
        };
        panel.setPreferredSize(new Dimension(images.get(0).getWidth(), images.get(0).getHeight()));

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                counter.increment();
                panel.repaint();
                frame.setTitle((counter.get() + 1) + "/" + counter.max);
            }
        });
        frame.getContentPane().add(panel);
        frame.setTitle((counter.get() + 1) + "/" + counter.max);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {
        GifRendererTest app = new GifRendererTest();
        app.test03();
    }
}

/* */
