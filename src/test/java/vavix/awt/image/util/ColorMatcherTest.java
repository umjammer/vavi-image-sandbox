/*
 * Copyright (c) 2010 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;


/**
 * ColorMatcherTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/07 nsano initial version <br>
 */
public class ColorMatcherTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        final ColorMatcher cm = new ColorMatcher();

        final JPanel[] samples = new JPanel[6];
        for (int i = 0; i < samples.length; i++) {
            samples[i] = new JPanel();
            samples[i].setPreferredSize(new Dimension(80, 80));
            samples[i].setBorder(new LineBorder(Color.black, 1));
        }

        JPanel basePanel = new JPanel();
        basePanel.setPreferredSize(new Dimension(640, 400));
        basePanel.setBackground(new Color(0xe6, 0xe6, 0xe6));

        final JPanel colorPanel = new JPanel();
        colorPanel.setPreferredSize(new Dimension(80, 80));
        colorPanel.setBorder(new LineBorder(Color.black, 1));

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        final JSlider sliderR = new JSlider();
        sliderR.setPreferredSize(new Dimension(276, 21));
        sliderR.setMinimum(0);
        sliderR.setMaximum(255);
        sliderR.setOpaque(true);
        sliderR.setBackground(Color.red);
        final JSlider sliderG = new JSlider();
        sliderG.setPreferredSize(new Dimension(276, 21));
        sliderG.setMinimum(0);
        sliderG.setMaximum(255);
        sliderG.setOpaque(true);
        sliderG.setBackground(Color.green);
        final JSlider sliderB = new JSlider();
        sliderB.setPreferredSize(new Dimension(276, 21));
        sliderB.setMinimum(0);
        sliderB.setMaximum(255);
        sliderB.setOpaque(true);
        sliderB.setBackground(Color.blue);

        ChangeListener changeListener = e -> {
            Color color = new Color(sliderR.getValue(), sliderG.getValue(), sliderB.getValue());
            colorPanel.setBackground(color);
            Color[] colors = cm.getMatchedColors(color);
            for (int i = 0; i < 6; i++) {
                samples[i].setBackground(colors[i]);
            }
        };
        sliderR.addChangeListener(changeListener);
        sliderG.addChangeListener(changeListener);
        sliderB.addChangeListener(changeListener);

        panel.add(sliderR);
        panel.add(new JPanel());
        panel.add(sliderG);
        panel.add(colorPanel);
        panel.add(sliderB);
        panel.add(new JPanel());

        basePanel.add(panel);

        MouseListener mouseListener = new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Color color = ((JPanel) e.getSource()).getBackground();
                colorPanel.setBackground(color);
                sliderR.setValue(color.getRed());
                sliderG.setValue(color.getGreen());
                sliderB.setValue(color.getBlue());
            }
        };
        panel = new JPanel();
        for (JPanel sample : samples) {
            panel.add(sample);
            sample.addMouseListener(mouseListener);
        }
        basePanel.add(panel);

        sliderR.setValue(127);
        sliderG.setValue(127);
        sliderB.setValue(127);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Color Matcher");
        frame.add(basePanel);
        frame.pack();
        frame.setVisible(true);
    }
}
