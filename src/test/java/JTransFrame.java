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

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;


/**
 * JTransFrame.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2019/05/17 umjammer initial version <br>
 */
public class JTransFrame extends JFrame {

    private Point start;
    private Point point;

    public JTransFrame(BufferedImage image) {

        addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                Component c = e.getComponent();
                point = c.getLocation();
                c.setLocation(point);
            }
            public void mousePressed(MouseEvent e) {
                start = e.getPoint();
                point = e.getPoint();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Component c = e.getComponent();
                point = c.getLocation(point);
                int x = point.x - start.x + e.getX();
                int y = point.y - start.y + e.getY();
                c.setLocation(x, y);
            }
        });

        JPanel panel = new JPanel() {
            public void paintComponent(Graphics g) {
                g.drawImage(image, 0, 0, this);
            }
        };
        panel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));

        // set window opacity
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        // eliminate window's shadow
        JRootPane root = getRootPane();
        root.putClientProperty("Window.shadow", false);

        getContentPane().add(panel);
    }

    public static void main(String[] args) throws IOException {
        BufferedImage image = ImageIO.read(JTransFrame.class.getResourceAsStream("panel.png"));
        JFrame frame = new JTransFrame(image);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(100, 100);
        frame.pack();
        frame.setVisible(true);
    }
}
