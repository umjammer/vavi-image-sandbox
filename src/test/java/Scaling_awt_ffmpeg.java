/*
 * Copyright (c) 2024 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;

import vavi.awt.image.resample.AwtResampleOp;
import vavi.awt.image.resample.FfmpegResampleOp;
import vavi.swing.JImageComponent;
import vavix.awt.image.resample.ZhoumxLanczosResample2Op;


/**
 * Scaling. (awt, ffmpeg)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 240220 nsano initial version <br>
 */
public class Scaling_awt_ffmpeg {

    /**
     * @param args 0: image
     */
    public static void main(String[] args) throws Exception {
        new Scaling_awt_ffmpeg(args);
    }

    BufferedImage rightImage;
    BufferedImage leftImage;
    JSlider slider;
    JImageComponent rightImageComponent;
    JImageComponent leftImageComponent;
    JLabel statusLabel;

    Scaling_awt_ffmpeg(String[] args) throws Exception {
System.err.println(args[0]);
        BufferedImage image = ImageIO.read(new File(args[0]));
        int w = image.getWidth();
        int h = image.getHeight();
System.err.println(w + ", " + h);

        leftImage = image;
        rightImage = image;
        slider = new JSlider();
        slider.setMaximum(100);
        slider.setMinimum(1);
        slider.setValue(100);
        slider.addChangeListener(event -> {
            JSlider source = (JSlider) event.getSource();
            if (source.getValueIsAdjusting()) {
                return;
            }
            float scale = source.getValue() / 100f;

            // left
            BufferedImage image1 = leftImage;
            BufferedImageOp filter = new AwtResampleOp(scale, scale);
long t = System.currentTimeMillis();
            BufferedImage filteredImage = filter.filter(image1, null);
System.err.println("left: " + (System.currentTimeMillis() - t) + "ms");
            leftImageComponent.setImage(filteredImage);
            leftImageComponent.repaint();

            // right
            image1 = rightImage;
            filter = new FfmpegResampleOp(scale, scale);
t = System.currentTimeMillis();
            filteredImage = filter.filter(image1, null);
System.err.println("right: " + (System.currentTimeMillis() - t) + "ms");
System.err.println("filteredImage: " + filteredImage);
            rightImageComponent.setImage(filteredImage);
            rightImageComponent.repaint();

System.err.println("scale: " + scale);
            statusLabel.setText("scale: " + scale);
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
        frame.setTitle("AwtResampleOp | Ffmpeg (swscale)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(scrollPane);
        frame.pack();
        split.setDividerLocation(0.5);
        frame.setVisible(true);
    }
}
