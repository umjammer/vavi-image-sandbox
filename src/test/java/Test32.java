/*
 * Copyright (c) 2018 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.DataBufferByte;
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
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import org.opencv.core.Core;
import org.opencv.core.Core.MinMaxLocResult;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import vavi.awt.rubberband.GlassPane;
import vavi.awt.rubberband.RubberBandAdapter;
import vavi.awt.rubberband.RubberBandEvent;
import vavi.swing.binding.Component;
import vavi.swing.binding.Components;
import vavi.swing.binding.Updater;

import vavix.awt.image.pixel.AwtCropOp;
import vavix.awt.image.util.ImageUtil;


/**
 * watermark remover.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 0.00 2018/02/05 umjammer initial version <br>
 * @see "https://stackoverflow.com/questions/17001083/opencv-template-matching-example-in-android/17516753#17516753"
 * @see "https://github.com/johnoneil/subimage/blob/master/subimage/find_subimage.py"
 * @see "http://workpiles.com/2015/05/opencv-matchtemplate-java/"
 */
public class Test32 {

    /**
     * @see "http://autolab-minoya.hatenablog.com/entry/2017/11/04/224627"
     */
    static {
//        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
         nu.pattern.OpenCV.loadLocally();
    }

    static Rectangle findSubimage(Mat img, Mat tmpl, double threshold) {
        int matchMethod = Imgproc.TM_CCOEFF_NORMED;

        // Create the result matrix
        int resultCols = img.cols() - tmpl.cols() + 1;
        int resultRows = img.rows() - tmpl.rows() + 1;
        Mat result = new Mat(resultRows, resultCols, CvType.CV_32FC1); 

        // Do the Matching and Normalize
        Imgproc.matchTemplate(img, tmpl, result, matchMethod);
//        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        Imgproc.threshold(result, result, threshold, 1.0, Imgproc.THRESH_TOZERO);

        // Localizing the best match with minMaxLoc
        MinMaxLocResult mmr = Core.minMaxLoc(result);

        Point matchLoc;
        boolean valid;
        if (matchMethod == Imgproc.TM_SQDIFF || matchMethod == Imgproc.TM_SQDIFF_NORMED) {
            matchLoc = mmr.minLoc;
            valid = mmr.minVal <= threshold;
            if (!valid && mmr.minVal != 0) {
                System.err.println("minVal: " + mmr.minVal);
            }
        } else {
            matchLoc = mmr.maxLoc;
            valid = mmr.maxVal >= threshold;
            if (!valid && mmr.maxVal != 0) {
                System.err.println("maxVal: " + mmr.maxVal);
            }
        }

        if (valid) {
            return new Rectangle ((int) matchLoc.x, (int) matchLoc.y, tmpl.cols(), tmpl.rows());
        } else {
            return null;
        }
    }

    static Test32 app;

    public static void main(String[] args) throws Exception {
        app = new Test32(args);
    }

    static class Mark {
        Mat mat;
        BufferedImage image;
        Color color;

        Mark(String filename, Color color) throws IOException {
            this.color = color;
            this.image = ImageIO.read(new File(filename));
            this.mat = Imgcodecs.imread(filename, Imgcodecs.IMREAD_GRAYSCALE);
            Imgproc.Canny(mat, mat, 32, 128);
        }

        Mark(BufferedImage image, Color color) {
            this.color = color;
            ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
            ColorConvertOp op = new ColorConvertOp(cs, null);
            this.image = op.filter(image, null);
            this.mat = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC1);
            byte[] data = ((DataBufferByte) this.image.getRaster().getDataBuffer()).getData();
            mat.put(0, 0, data);
            Imgproc.Canny(mat, mat, 32, 128);
        }
    }

    static class Page {
        static final double THRESHOLD = 0.6;
        Path path;
        Mat mat;
        BufferedImage original;
        BufferedImage image;
        Map<Mark, Rectangle> markers = new HashMap<>();
        double threshold = THRESHOLD;
        Method method = Method.white;
        boolean isBlack;

        Page(Path path) throws IOException {
            System.err.println(path);
            this.path = path;
            this.original = ImageIO.read(path.toFile());
            this.mat = Imgcodecs.imread(path.toString(), Imgcodecs.IMREAD_GRAYSCALE);
            Imgproc.Canny(mat, mat, 32, 128);
        }

        void clear() {
            if (markers.size() > 0) {
                markers.clear();
            }
        }

        void find(Set<Mark> marks) {
            clear();
            for (Mark mark : marks) {
                Rectangle rect = findSubimage(mat, mark.mat, threshold);
                if (rect != null &&
                    ((rect.x > rect.width + 10 && rect.x < image.getWidth() - rect.width - 10) ||
                     (rect.y > rect.height + 10 && rect.y < image.getHeight() - rect.height - 10))) {
                    // local
                    markers.put(mark, null);
                } else {
                    markers.put(mark, rect);
                }
            }
        }

        int found() {
            int count = 0;
            for (Rectangle rect : markers.values()) {
                if (rect != null) {
                    count++;
                }
            }
            return count;
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

        void erase() {
            for (Mark mark : markers.keySet()) {
                Rectangle rect = markers.get(mark);
                if (rect != null) {
                    method.exec(image, mark, rect);
                }
            }
        }

        void setMethod(Page.Method method) {
            this.method = method;
        }

        void setBlack(boolean isBlack) {
            this.isBlack = isBlack;
        }

        enum Method {
            xor {
                void exec(BufferedImage image, Mark mark, Rectangle rect) {
                    for (int y = rect.y; y < rect.y + rect.height; y++) {
                        for (int x = rect.x; x < rect.x + rect.width; x++) {
                            int c1 = image.getRGB(x, y);
                            int c2 = mark.image.getRGB(x - rect.x, y - rect.y);
                            int r1 = c1 & 0x00ff0000 >> 16; 
                            int g1 = c1 & 0x0000ff00 >> 8; 
                            int b1 = c1 & 0x000000ff; 
                            int r2 = c2 & 0x00ff0000 >> 16; 
                            int g2 = c2 & 0x0000ff00 >> 8; 
                            int b2 = c2 & 0x000000ff;
                            int r, g, b;
                            // xor
                            r = ~(r1 ^ r2) & 0xff;
                            g = ~(g1 ^ g2) & 0xff;
                            b = ~(b1 ^ b2) & 0xff;
                            // subtract
//                            int r3 = ~(r1 - r2) & 0xff;
//                            int g3 = ~(g1 - g2) & 0xff;
//                            int b3 = ~(b1 - b2) & 0xff;
//                            r = (r - r3) & 0xff;
//                            g = (g - g3) & 0xff;
//                            b = (b - b3) & 0xff;
                            image.setRGB(x, y, r << 16 | g << 8 | b);
                        }
                    }
                }
            },
            white {
                void exec(BufferedImage image, Mark mark, Rectangle rect) {
                    for (int y = rect.y; y < rect.y + rect.height; y++) {
                        for (int x = rect.x; x < rect.x + rect.width; x++) {
                            image.setRGB(x, y, 0xffffffff);
                        }
                    }
                }
            },
            black {
                void exec(BufferedImage image, Mark mark, Rectangle rect) {
                    for (int y = rect.y; y < rect.y + rect.height; y++) {
                        for (int x = rect.x; x < rect.x + rect.width; x++) {
                            image.setRGB(x, y, 0);
                        }
                    }
                }
            };
            abstract void exec(BufferedImage image, Mark mark, Rectangle rect);
        }
    }

    interface View {
        void update(BufferedImage image);
        void paint(Graphics g);
        void addMark(Mark mark);
        void removeMark(Mark mark);
        public BufferedImage getMarkImage(Mark mark);
        void setMarkSelected(int index);
        int getMarkSelected();
        double getScale();
        void requestFocus();
    }

    static class Model {
        static final String[] markFiles = {
//            "tmp/mgm1.png",
//            "tmp/mgm2.png",
//            "tmp/mgm3.png",
//            "tmp/mgm4.png",
//            "tmp/mgm5.png",
        };

        int index = 0;
        Set<Mark> marks = new HashSet<>();
        List<Page> pages = new ArrayList<>();

        View view;

        Random random = new Random();

        Model(View view) throws IOException {
            this.view = view;
            for (int i = 0; i < markFiles.length; i++) {
                addMark(new Mark(markFiles[i], new Color(random.nextInt(0xffffff))));
            }
        }

        void addMark(Mark mark) {
            marks.add(mark);
            view.addMark(mark);
        }

        void removeMark(Mark mark) {
            marks.remove(mark);
            view.removeMark(mark);
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
            view.update(pages.get(index).image);
        }

        void erase() {
            erase(false);
        }

        void erase(boolean initial) {
            Page page = pages.get(index);
            page.origin();
            if (initial) {
                page.setMethod(Page.Method.white);
            }
            page.find(marks);
            if (initial && page.found() == 0) {  // local
                page.setMethod(Page.Method.xor);
                page.threshold = 0.25;
                page.find(marks);
            }
            page.erase();
            update();
        }

        void origin() {
            Page page = pages.get(index);
            page.origin();
            page.clear();
            update();
        }

        /** target is current page */
        void mark(BiConsumer<Color, Rectangle> drawer) {
            pages.get(index).mark(drawer);
        }

        /** target is current page */
        void setMethod(Page.Method method) {
            pages.get(index).setMethod(method);
        }

        /** target is current page */
        void setBlack(boolean isBlack) {
            pages.get(index).setBlack(isBlack);
        }

        /** target is current page */
        boolean isXor() {
            return pages.get(index).method.equals(Page.Method.xor);
        }

        /** target is current page */
        boolean isBlack() {
            return pages.get(index).method.equals(Page.Method.black);
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
            for (index = 0; index < pages.size(); index++) {
                erase(true);
            }
            index = pages.size() - 1;
System.err.println("exec done.");
        }

        void load(String dir, String ext) throws IOException {
            Files.list(Paths.get(dir))
            .filter(p -> p.toString().endsWith(ext))
            .sorted()
            .forEach(p -> {
                try {
                    Page page = new Page(p);
                    pages.add(page);

                    index = pages.size() - 1;

                    erase(true);
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            });
        }

        void save(String dir, String type) throws IOException { // "jpg"
System.err.println("saving start.");
            for (Page page : pages) {
                ImageIO.write(page.image, type, Paths.get(dir, page.path.getFileName().toString()).toFile());
            }
System.err.println("saving done.");
        }

        void addMark(Rectangle rectangle) {
            AwtCropOp filter = new AwtCropOp(rectangle);
            BufferedImage image = filter.filter(pages.get(index).image, null);
//try {
// ImageIO.write(image, "PNG", Paths.get(".", "selected.png").toFile());
//} catch (IOException e) {
// e.printStackTrace();
//}
            addMark(new Mark(image, new Color(random.nextInt(0xffffff))));
        }
    }

    Model model;

    JCheckBox xorCheckBox;
    JSlider thresholdJSlider;
    JComboBox<Mark> marksComboBox;
    JCheckBox bwCheckBox;

    /** */
    public static class MyUpdater implements Updater<Params> {
        public void update(Params params) {
            app.model.setMethod(params.xor ? Page.Method.xor : params.isBlack ? Page.Method.black : Page.Method.white);
            app.model.setThreshold(params.threshold / 100d);
//System.err.println(app.model.isXor() + ", " + app.model.getThreshold());
            app.thresholdJSlider.setToolTipText(String.valueOf(app.model.getThreshold())); // to be gathered
            app.model.erase();
            app.model.view.requestFocus();
        }
    }

    /** TODO inside model ??? */
    @Components(updater = MyUpdater.class)
    public static class Params {
        @Component(name = "thresholdJSlider")
        int threshold;
        @Component(name = "xorCheckBox")
        boolean xor;
        @Component(name = "bwCheckBox")
        boolean isBlack;
    }

    public Test32(String[] args) throws Exception {

        marksComboBox = new JComboBox<>();
        marksComboBox.setRenderer(new ListCellRenderer<Mark>() {
            BufferedImage image;
            JPanel cell = new JPanel();
            JPanel icon = new JPanel() {
                public void paint(Graphics g) {
                    g.drawImage(image, 0, 0, this);
                }
            };
            JLabel color = new JLabel();
            {
                cell.setLayout(new FlowLayout(FlowLayout.LEFT));
                cell.setOpaque(true);
                color.setOpaque(true);
                cell.add(icon);
                cell.add(color);
            }
            public java.awt.Component getListCellRendererComponent(JList<? extends Mark> list,
                                                                   Mark value,
                                                                   int index,
                                                                   boolean isSelected,
                                                                   boolean cellHasFocus) {
                if (model != null && index >= 0) {
                    image = model.view.getMarkImage(value);
                    color.setText(String.valueOf(index) + ": 0x" + Integer.toHexString(value.color.getRGB()));
                    color.setBackground(value.color);
                }
                if (isSelected) {
                    cell.setBackground(UIManager.getColor("ComboBox.selectionBackground"));
                    model.view.setMarkSelected(index);
                } else {
                    cell.setBackground(UIManager.getColor("ComboBox.background"));
                }
                return cell;
            }
        });
        marksComboBox.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_BACK_SPACE) {
                    Mark mark = marksComboBox.getItemAt(model.view.getMarkSelected());
                    if (mark != null) {
                        model.removeMark(mark);
                        model.view.requestFocus();
                    }
                }
            }
        });
        xorCheckBox = new JCheckBox("xor");
        bwCheckBox = new JCheckBox("BW");
        thresholdJSlider = new JSlider();
        thresholdJSlider.setMaximum(100);
        thresholdJSlider.setMinimum(0);
        thresholdJSlider = new JSlider();
        JLabel thresholdLabel = new JLabel();
        thresholdLabel.setText("threshold");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(marksComboBox);
        buttonPanel.add(xorCheckBox);
        buttonPanel.add(thresholdLabel);
        buttonPanel.add(thresholdJSlider);
        buttonPanel.add(bwCheckBox);

        JPanel panel = new JPanel() {
            public void paint(Graphics g) {
                model.view.paint(g);
            }
        };
        GlassPane glassPane = new GlassPane();
        glassPane.addRubberBandListener(new RubberBandAdapter() {
            public void selected(RubberBandEvent ev) {
                Rectangle rect = ev.getBounds();
                int w = Math.min(glassPane.getWidth(), model.pages.get(model.index).image.getWidth());
                int h = Math.min(glassPane.getHeight(), model.pages.get(model.index).image.getHeight());
                if (rect.width - rect.x < 4 || rect.height - rect.y < 4) {
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
                double scale = model.view.getScale();
System.err.println(rect + ", " + scale);
                model.addMark(new Rectangle((int) (rect.x / scale), (int) (rect.y / scale),
                                            (int) (rect.width / scale),(int) (rect.height / scale)));
            }
        });
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.add(glassPane);
        layeredPane.add(panel);

        JPanel basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());
        basePanel.add(buttonPanel, BorderLayout.NORTH);
        basePanel.add(layeredPane, BorderLayout.CENTER);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(basePanel);
        frame.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                int code = e.getKeyCode();
                if (code == KeyEvent.VK_LEFT) {         // <-
                    model.up();
                } else if (code == KeyEvent.VK_RIGHT) { // ->
                    model.down();
                } else if (code == KeyEvent.VK_S) {     // S
                    try {
                        model.save(args[1], "jpg");
                    } catch (IOException f) {
                        f.printStackTrace();
                    }
                } else if (code == KeyEvent.VK_E) {     // E
                    model.exec();
                } else if (code == KeyEvent.VK_A) {     // A
                    model.erase(true);
                } else if (code == KeyEvent.VK_O) {     // O
                    model.origin();
                }
                model.update();
                frame.setCursor(Cursor.getDefaultCursor());
            }
        });

        Params params = new Params();
        Components.Util.bind(params, this);

        model = new Model(new View() {
            boolean initialized = false;
            BufferedImage image;
            Map<Mark, BufferedImage> markImages = new HashMap<>();
            double scale;
            int markIndex;
            public void update(BufferedImage image) {
                // TODO Components.Util.copy(model, params); ???
                params.xor = model.isXor();
                params.threshold = (int) (model.getThreshold() * 100);
                params.isBlack = model.isBlack();
                Components.Util.rebind(params, Test32.this);
                //
                this.scale = ImageUtil.scale(image, 0.8);
                this.image = scale != 1 ? ImageUtil.fit(image, scale) : image;
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
                panel.repaint();
                frame.requestFocusInWindow();
            }
            public void paint(Graphics g) {
                g.drawImage(image, 0, 0, panel);
                model.mark((c, r) -> {
                    g.setColor(c);
                    g.drawRect((int) (r.x * scale), (int) (r.y * scale), (int) (r.width * scale), (int) (r.height * scale));
                });
            }
            public void addMark(Mark mark) {
                marksComboBox.addItem(mark);
            }
            public void removeMark(Mark mark) {
                marksComboBox.removeItem(mark);
                markImages.remove(mark);
            }
            public BufferedImage getMarkImage(Mark mark) {
                if (markImages.get(mark) == null) {
                    BufferedImage image = mark.image;
                    int h = buttonPanel.getHeight();
                    double scale = (double) h / image.getHeight();
System.err.println(image.getWidth() + ", " + image.getHeight());
                    markImages.put(mark, ImageUtil.fit(image, scale));
                }
                return markImages.get(mark);
            }
            public void setMarkSelected(int index) {
                markIndex = index;
            }
            public int getMarkSelected() {
                return markIndex;
            }
            public double getScale() {
                return scale;
            }
            public void requestFocus() {
                frame.requestFocus();
            }
        });
        model.load(args[0], ".jpg");
    }
}

/* */
