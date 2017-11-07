/*
 * Copyright (c) 2011 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavix.awt.image.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;


/**
 * ColorThemeTest.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/07 nsano initial version <br>
 * @see "http://www.slideshare.net/marippe/ss-9003317"
 */
public class ColorThemeTest {

    Hsv hsv = new Hsv();
    {
        hsv.s = -1;
        hsv.v = -1;
    }
    Color getColor() {
        Hsv c = new Hsv();
        c.h = hsv.h;
        c.s = hsv.s >= 0 ? hsv.s : 100;
        c.v = hsv.v >= 0 ? hsv.v : 100;
System.err.println("hsv: " + hsv);
        return c.toRgb().toColor();
    }
    Color getBaseColor() {
        return getColor().brighter();
    }
    Color getAccentColor() {
        Hsv c = new Hsv();
        c.h = (hsv.h + 180) % 360;
        c.s = hsv.s >= 0 ? hsv.s : 100;
        c.v = hsv.v >= 0 ? hsv.v : 100;
        return c.toRgb().toColor();
    }

    static String toWebString(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    static class ColorSplittablePanel extends JPanel {
        {
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if (event.isAltDown()) {
                        split = split == 1 ? 0 : 1;
                    } else if (event.isControlDown()) {

                    } else if (event.isMetaDown()) {

                    } else {

                    }
                }
            });
        }
        int split = 0;
        Color color;
        void setColor(Color color) {
            this.color = color;
        }
        public void paint(Graphics g) {

        }
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        final ColorThemeTest app = new ColorThemeTest();

        final JPanel[] samples = new JPanel[3];
        final JTextField[] textFields = new JTextField[3];

        JPanel basePanel = new JPanel();
        basePanel.setPreferredSize(new Dimension(640, 400));
        basePanel.setBackground(new Color(0xe6, 0xe6, 0xe6));

        JPanel panel = new JPanel();
        GridLayout layout = new GridLayout(3, 1);
        layout.setHgap(3);
        layout.setVgap(3);
        panel.setLayout(layout);

        final JPanel panelH = new JPanel() {
            public void paint(Graphics g) {
                for (int i = 0; i < 360; i++) {
                    Hsv c = new Hsv();
                    c.h = i;
                    c.s = 100;
                    c.v = 100;
                    g.setColor(c.toRgb().toColor());
                    g.drawLine(i, 0, i, getHeight());
                }
            }
        };
        final JPanel panelS = new JPanel() {
            public void paint(Graphics g) {
                for (int i = 0; i < 360; i++) {
                    Hsv c = new Hsv();
                    c.h = app.hsv.h;
                    c.s = i * 100f / 360;
                    c.v = app.hsv.v >= 0 ? app.hsv.v : 100;
                    g.setColor(c.toRgb().toColor());
                    g.drawLine(i, 0, i, getHeight());
                }
            }
        };
        final JPanel panelV = new JPanel() {
            public void paint(Graphics g) {
                for (int i = 0; i < 360; i++) {
                    Hsv c = new Hsv();
                    c.h = app.hsv.h;
                    c.s = app.hsv.s >= 0 ? app.hsv.s : 100;
                    c.v = i * 100f / 360;
                    g.setColor(c.toRgb().toColor());
                    g.drawLine(i, 0, i, getHeight());
                }
            }
        };
        panelH.setPreferredSize(new Dimension(360, 40));
        panelH.setOpaque(true);
        panelH.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                app.hsv.h = e.getX();
                app.hsv.s = -1;
                app.hsv.v = -1;
                panelS.repaint();
                panelV.repaint();

                samples[0].setBackground(app.getBaseColor());
                samples[1].setBackground(app.getColor());
                samples[2].setBackground(app.getAccentColor());
                textFields[0].setText(toWebString(app.getBaseColor()));
                textFields[1].setText(toWebString(app.getColor()));
                textFields[2].setText(toWebString(app.getAccentColor()));
            }
        });
        panelS.setPreferredSize(new Dimension(360, 40));
        panelS.setOpaque(true);
        panelS.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                app.hsv.s = e.getX() * 100f / 360;
                panelV.repaint();

                samples[0].setBackground(app.getBaseColor());
                samples[1].setBackground(app.getColor());
                samples[2].setBackground(app.getAccentColor());
                textFields[0].setText(toWebString(app.getBaseColor()));
                textFields[1].setText(toWebString(app.getColor()));
                textFields[2].setText(toWebString(app.getAccentColor()));
            }
        });
        panelV.setPreferredSize(new Dimension(360, 40));
        panelV.setOpaque(true);
        panelV.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                app.hsv.v = e.getX() * 100f / 360;
                panelS.repaint();

                samples[0].setBackground(app.getBaseColor());
                samples[1].setBackground(app.getColor());
                samples[2].setBackground(app.getAccentColor());
                textFields[0].setText(toWebString(app.getBaseColor()));
                textFields[1].setText(toWebString(app.getColor()));
                textFields[2].setText(toWebString(app.getAccentColor()));
            }
        });

        panel.add(panelH);
        panel.add(panelS);
        panel.add(panelV);

        basePanel.add(panel);

        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(40, 10, 40, 10));
        panel.setPreferredSize(new Dimension(380, 140));
        double[] ws = { .7, .25, .05 };
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.fill = GridBagConstraints.BOTH;
        for (int i = 0; i < samples.length; i++) {
            samples[i] = new JPanel();
            samples[i].setBorder(new LineBorder(Color.black, 1));
            gbc.gridx = i;
            gbc.weighty = 1;
            gbc.weightx = ws[i];
            panel.add(samples[i], gbc);
        }
        basePanel.add(panel);
        samples[0].addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Color color = samples[0].getBackground();
                if (e.isShiftDown()) {
                    color = color.darker();
                } else {
                    color = color.brighter();
                }
                samples[0].setBackground(color);
            }
        });

        panel = new JPanel();
        panel.setPreferredSize(new Dimension(380, 90));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        layout = new GridLayout(3, 2);
        layout.setHgap(3);
        layout.setVgap(3);
        String[] labels = { "Base", "Main", "Accent" };
        panel.setLayout(layout);
        for (int i = 0; i < samples.length; i++) {
            textFields[i] = new JTextField(7);
            panel.add(new JLabel(labels[i]));
            panel.add(textFields[i]);
        }
        basePanel.add(panel);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Color Theme");
        frame.add(basePanel);
        frame.pack();
        frame.setVisible(true);
    }
}

/* */
