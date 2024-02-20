/*
 * Copyright (c) 2023 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

import vavi.awt.rubberband.GlassPane;
import vavi.awt.rubberband.RubberBandAdapter;
import vavi.awt.rubberband.RubberBandEvent;
import vavi.util.Debug;
import vavix.awt.image.util.ImageUtil;


/**
 * Rubber Band Picker.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2023/12/04 umjammer initial version <br>
 */
public class RubberBandPicker {

    static RubberBandPicker app;

    /**
     * @param args 0: inDir, 1: outDir, 2: inExt, 3: outExt
     */
    public static void main(String[] args) throws Exception {
Debug.println("inDir: " + args[0]);
Debug.println("outDir: " + args[1]);
Debug.println("inExt: " + args[2]);
Debug.println("inExt: " + args[3]);
        app = new RubberBandPicker(args[0], args[1], args[2], args[3]);
    }

    static class Mark {
        BufferedImage image;
        Color color;

        Mark(String filename, Color color) throws IOException {
            this.color = color;
            this.image = ImageIO.read(new File(filename));
        }

        Mark(BufferedImage image, Color color) {
            this.color = color;
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp op = new ColorConvertOp(cs, null);
            this.image = op.filter(image, null);
        }
    }

    static class Page {
        static final double THRESHOLD = 0.6;
        Path path;
        BufferedImage original;
        BufferedImage image;
        Map<Mark, Rectangle> markers = new HashMap<>();
        double threshold = THRESHOLD;

        Page(Path path) throws IOException {
            System.err.println(path);
            this.path = path;
            this.original = ImageIO.read(path.toFile());
            this.image = ImageIO.read(path.toFile());
        }

        void mark(BiConsumer<Color, Rectangle> drawer) {
            for (Mark mark : markers.keySet()) {
                Rectangle rect = markers.get(mark);
                if (rect != null) {
                    drawer.accept(mark.color, rect);
                }
            }
        }

        private void origin() {
            this.image = ImageUtil.clone(original);
        }
    }

    interface View {
        void update(Model model);
        void requestFocus();
        void paint(Graphics g);
    }

    static class Model {

        int index = 0;
        List<Page> pages = new ArrayList<>();

        View view;

        Random random = new Random();

        Model(View view) throws IOException {
            this.view = view;
        }

        void up() {
            if (index > 0) {
                index--;
            }
        }

        void down() {
            if (index < pages.size() - 1) {
                index++;
            }
        }

        void update() {
//            System.err.println("index: " + index);
            view.update(this);
        }

        void origin() {
            Page page = pages.get(index);
            page.origin();
            update();
        }

        /** target is current page */
        void mark(BiConsumer<Color, Rectangle> drawer) {
            pages.get(index).mark(drawer);
        }

        /** target is current page */
        double getThreshold() {
            return pages.get(index).threshold;
        }

        /** target is current page */
        void setThreshold(double threshold) {
            pages.get(index).threshold = threshold;
        }

        void exec() {
System.err.println("exec start.");
            index = pages.size() - 1;
System.err.println("exec done.");
        }

        void load(String dir, String ext) throws IOException {
            Files.list(Paths.get(dir))
            .filter(p -> p.toString().endsWith("." + ext))
            .sorted()
            .forEach(p -> {
                try {
                    Page page = new Page(p);
                    pages.add(page);

                    index = pages.size() - 1;

                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            });
System.err.println("pages: " + pages.size());
            update();
        }

        void save(String dir, String type) throws IOException {
System.err.println("saving start.");
System.err.println("saving done.");
        }
    }

    Model model;

    public RubberBandPicker(String inDir, String outDir, String inExt, String outExt) throws Exception {

        JPanel panel = new JPanel() {
            @Override public void paint(Graphics g) {
                model.view.paint(g);
            }
        };
        GlassPane glassPane = new GlassPane();
        glassPane.addRubberBandListener(new RubberBandAdapter() {
            @Override public void selected(RubberBandEvent ev) {
                Rectangle rect = ev.getBounds();
                int w = Math.min(glassPane.getWidth(), model.pages.get(model.index).image.getWidth());
                int h = Math.min(glassPane.getHeight(), model.pages.get(model.index).image.getHeight());
                if (rect.width - rect.x < 4 || rect.height - rect.y < 4) {
System.err.println("selected rectangle should be larger equal 4x4");
                    return;
                }
                if (rect.x < 0) {
                    rect.x = 0;
                }
                if (rect.x + rect.width > w) {
                    rect.width = w - rect.x;
                } else {
                    rect.width -= rect.x;
                }
                if (rect.y < 0) {
                    rect.y = 0;
                }
                if (rect.y + rect.height > h) {
                    rect.height = h - rect.y;
                } else {
                    rect.height -= rect.y;
                }
            }
        });
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.add(glassPane);
        layeredPane.add(panel);

        JPanel basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());
        basePanel.add(layeredPane, BorderLayout.CENTER);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(basePanel);
        frame.addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_LEFT) {         // <-
                    model.up();
                } else if (code == KeyEvent.VK_RIGHT) { // ->
                    model.down();
                } else if (code == KeyEvent.VK_S) {     // S
                    try {
                        model.save(outDir, outExt);
                    } catch (IOException f) {
                        f.printStackTrace(System.err);
                    }
                } else if (code == KeyEvent.VK_E) {     // E
                    model.exec();
                } else if (code == KeyEvent.VK_O) {     // O
                    model.origin();
                }
                model.update();
                frame.setCursor(Cursor.getDefaultCursor());
            }
        });

        model = new Model(new View() {
            boolean initialized = false;
            BufferedImage image;
            double scale;
            @Override public void update(Model model) {
                BufferedImage target = model.pages.get(model.index).image;
                this.scale = ImageUtil.fitY(target, 0.8);
                this.image = scale != 1 ? ImageUtil.scale(target, scale) : target;
                if (!initialized) {
                    Dimension dimension = new Dimension(this.image.getWidth(), this.image.getHeight());
                    layeredPane.setPreferredSize(dimension);
                    panel.setSize(dimension);
                    glassPane.setSize(dimension);
                    frame.pack();
                    frame.setVisible(true);
                    frame.setFocusable(true);
                    initialized = true;
                }
                frame.setTitle("RubberBandPicker " + model.pages.get(model.index).path.getFileName());
                panel.repaint();
                frame.requestFocusInWindow();
            }
            @Override  public void paint(Graphics g) {
                g.drawImage(image, 0, 0, panel);
                model.mark((c, r) -> {
                    g.setColor(c);
                    g.drawRect((int) (r.x * scale), (int) (r.y * scale), (int) (r.width * scale), (int) (r.height * scale));
                });
            }
            @Override public void requestFocus() {
                frame.requestFocus();
            }
        });
        model.load(inDir, inExt);
    }
}

/* */
