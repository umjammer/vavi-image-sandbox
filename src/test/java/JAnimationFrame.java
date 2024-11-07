/*
 * "https://stackoverflow.com/questions/13546644/how-to-remove-drop-shadow-of-java-awt-frame-on-osx"
 * "http://nadeausoftware.com/articles/2009/01/mac_java_tip_how_control_window_decorations"
 */

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;

import vavi.awt.image.AnimationRenderer;
import vavi.awt.image.gif.GifRenderer;
import vavi.imageio.IIOUtil;


/**
 * JAnimationFrame.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/05/17 umjammer initial version <br>
 */
public class JAnimationFrame extends JFrame {

    static {
        IIOUtil.setOrder(ImageReaderSpi.class,
                "com.sun.imageio.plugins.gif.GIFImageReaderSpi",
                "vavi.imageio.gif.NonLzwGifImageReaderSpi");
    }

    private Point start;
    private Point point;

    AnimationRenderer renderer;

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

    public JAnimationFrame(AnimationRenderer renderer) {
        this.renderer = renderer;

        LoopCounter counter = new LoopCounter(renderer.size());

        addMouseListener(new MouseAdapter() {
            @Override public void mouseReleased(MouseEvent e) {
                Component c = e.getComponent();
                point = c.getLocation();
                c.setLocation(point);
            }
            @Override public void mousePressed(MouseEvent e) {
                start = e.getPoint();
                point = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                Component c = e.getComponent();
                point = c.getLocation(point);
                int x = point.x - start.x + e.getX();
                int y = point.y - start.y + e.getY();
                c.setLocation(x, y);
            }
        });

        JPanel panel = new JPanel() {
            @Override public void paintComponent(Graphics g) {
                g.drawImage(renderer.get(counter.get()), 0, 0, this);
            }
        };
        panel.setPreferredSize(new Dimension(renderer.get(0).getWidth(), renderer.get(0).getHeight()));

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        Runnable task = new Runnable() {
            @Override public void run() {
                counter.increment();
                repaint();
                scheduler.schedule(this, renderer.getDelayTime(counter.get()), TimeUnit.MILLISECONDS);
            }
        };
        repaint();
        scheduler.schedule(task, renderer.getDelayTime(0), TimeUnit.MILLISECONDS);

        // set window opacity
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        // eliminate window's shadow
        JRootPane root = getRootPane();
        root.putClientProperty("Window.shadow", false);

        getContentPane().add(panel);

        scheduler.close();
    }

    /** */
    public static void main(String[] args) throws IOException {
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream iis = ImageIO.createImageInputStream(JAnimationFrame.class.getResourceAsStream(args[0]));
        reader.setInput(iis, true);
        AnimationRenderer renderer = new GifRenderer();
        for (int i = 0;; i++) {
            BufferedImage image;
            try {
                image = reader.read(i);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
            IIOMetadata imageMetaData = reader.getImageMetadata(i);
            renderer.addFrame(image, imageMetaData);
        }
        iis.close();
        JFrame frame = new JAnimationFrame(renderer);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(100, 100);
        frame.pack();
        frame.setVisible(true);
    }
}
