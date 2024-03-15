/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.prefs.Preferences;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;
import vavi.awt.image.AbstractBufferedImageOp;
import vavi.swing.JImageComponent;
import vavi.swing.binding.Component;
import vavi.swing.binding.Components;
import vavi.swing.binding.Updater;
import vavi.swing.binding.binder.JTextFieldBinder;
import vavi.util.Debug;


/**
 * ImageMagick filter.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 061013 nsano initial version <br>
 */
public class ImageMagickFilter {

    static {
        UIManager.getDefaults().put("SplitPane.border", BorderFactory.createEmptyBorder());
        UIManager.getDefaults().put("TextField.border", BorderFactory.createLineBorder(UIManager.getColor("Panel.background"), 4));
        UIManager.getDefaults().put("ScrollPane.border", BorderFactory.createEmptyBorder());
    }

    static ImageMagickFilter app;

    public static void main(String[] args) throws Exception {
        app = new ImageMagickFilter(args);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
Debug.println("shutdownHook");
            app.prefs.putInt("lastX", app.frame.getX());
            app.prefs.putInt("lastY", app.frame.getY());
            app.prefs.putInt("lastWidth", Math.max(app.split.getWidth(), 800));
            app.prefs.putInt("lastHeight", Math.max(app.split.getHeight(), 600));
        }));
    }

    Preferences prefs = Preferences.userNodeForPackage(ImageMagickFilter.class);

    JFrame frame;
    JPanel split;

    MagickParams params = new MagickParams();

    BufferedImage image;

    JSlider contrastSlider;
    JSlider modulationSlider;
    JSlider thresholdSlider;
    JSlider gammaSlider;

    JCheckBox channelRedSeparateCheckBox;
    JCheckBox normalizeCheckBox;
    JCheckBox modulationCheckBox;
    JCheckBox thresholdCheckBox;
    JCheckBox gammaCheckBox;
    JCheckBox autoLevelCheckBox;
    JCheckBox equalizeCheckBox;
    JCheckBox typeGrayscaleCheckBox;
    JTextField rawArgs;

    JImageComponent rightImageComponent;
    JImageComponent leftImageComponent;

    JTextField statusLabel;

    /** */
    void updateModel(BufferedImage image) {
        leftImageComponent.setImage(image);
        rightImageComponent.setImage(image);
        if (this.image != null) {
            params.reset();
        }
        updateView();
        this.image = image;
    }

    /** */
    void updateView() {
        leftImageComponent.repaint();
        rightImageComponent.repaint();
    }

    ImageMagickFilter(String[] args) throws Exception {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = prefs.getInt("lastX", 0);
        int y = prefs.getInt("lastY", 0);
        int w = prefs.getInt("lastWidth", (screenSize.width / 2) * 8 / 10);
        int h = prefs.getInt("lastHeight", screenSize.height * 8 / 10);

        if (args.length > 0) {
            updateModel(ImageIO.read(new File(args[0])));
        }

        channelRedSeparateCheckBox = new JCheckBox("channel red separate");
        normalizeCheckBox = new JCheckBox("normalize");
        modulationCheckBox = new JCheckBox("modulation");
        modulationCheckBox.addItemListener(e -> { if (modulationCheckBox.isSelected()) modulationSlider.requestFocus(); });
        thresholdCheckBox = new JCheckBox("threshold");
        thresholdCheckBox.addItemListener(e -> { if (thresholdCheckBox.isSelected()) thresholdSlider.requestFocus(); });
        gammaCheckBox = new JCheckBox("gamma");
        gammaCheckBox.addItemListener(e -> { if (gammaCheckBox.isSelected()) gammaSlider.requestFocus(); });
        autoLevelCheckBox = new JCheckBox("auto level");
        equalizeCheckBox = new JCheckBox("equalize");
        typeGrayscaleCheckBox = new JCheckBox("type grayscale");
        rawArgs = new JTextField("raw args", 16);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(new JLabel("raw args: "));
        buttonPanel.add(rawArgs);
        buttonPanel.add(channelRedSeparateCheckBox);
        buttonPanel.add(normalizeCheckBox);
        buttonPanel.add(modulationCheckBox);
        buttonPanel.add(thresholdCheckBox);
        buttonPanel.add(autoLevelCheckBox);
        buttonPanel.add(equalizeCheckBox);
        buttonPanel.add(typeGrayscaleCheckBox);
        buttonPanel.add(gammaCheckBox);

        JPanel upperPanel = new JPanel();
        upperPanel.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        upperPanel.setLayout(new BoxLayout(upperPanel, BoxLayout.Y_AXIS));
        contrastSlider = new JSlider();
        contrastSlider.setMaximum(10);
        contrastSlider.setMinimum(0);
        contrastSlider.setToolTipText("contrast");

        modulationSlider = new JSlider();
        modulationSlider.setMaximum(150);
        modulationSlider.setMinimum(80);
        modulationSlider.setToolTipText("modulation");

        thresholdSlider = new JSlider();
        thresholdSlider.setMaximum(100);
        thresholdSlider.setMinimum(0);
        thresholdSlider.setToolTipText("threshold");

        gammaSlider = new JSlider();
        gammaSlider.setMaximum(100);
        gammaSlider.setMinimum(0);
        gammaSlider.setToolTipText("gamma");

        upperPanel.add(gammaSlider);
        upperPanel.add(thresholdSlider);
        upperPanel.add(contrastSlider);
        upperPanel.add(modulationSlider);
        upperPanel.add(buttonPanel);

        JPanel basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());
        basePanel.add(upperPanel, BorderLayout.NORTH);

        leftImageComponent = new JImageComponent();
        leftImageComponent.setPreferredSize(new Dimension(400, 600));

        rightImageComponent = new JImageComponent(true);
        rightImageComponent.setPreferredSize(new Dimension(400, 600));
        rightImageComponent.addPropertyChangeListener(e -> {
            if (e.getPropertyName().equals("droppedImage")) {
                updateModel((BufferedImage) e.getNewValue());
            }
        });

        split = new JPanel();
        split.setLayout(new GridLayout(1, 2));
        split.add(leftImageComponent);
        split.add(rightImageComponent);
        split.setPreferredSize(new Dimension(w, h));

        basePanel.add(split, BorderLayout.CENTER);

        statusLabel = new JTextField();
        statusLabel.setBackground(UIManager.getColor("Panel.background"));
        statusLabel.setText("original");
        basePanel.add(statusLabel, BorderLayout.SOUTH);

        frame = new JFrame();
        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                updateView();
            }
        });

        frame.setLocation(x, y);
        frame.setTitle("original | magick");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(basePanel);
        frame.pack();

        frame.setVisible(true);

        updateView();

        Components.Util.bind(params, this);
    }

    /** */
    public static class MyUpdater implements Updater<MagickParams> {
        public void update(MagickParams params) {
            if (app.image == null) {
Debug.println("no image");
                params.reset();
            } else {
                app.updateRightImage(params);
            }
        }
    }

    @Components(updater = MyUpdater.class)
    public static class MagickParams {
        String path = System.getProperty("ImageMagickFilter.path", "/usr/local/bin/");
        @Component(name = "modulationSlider")
        int modulation = 100;
        @Component(name = "contrastSlider")
        int contrast;
        @Component(name = "channelRedSeparateCheckBox")
        boolean channelRedSeparationFlag;
        @Component(name = "normalizeCheckBox")
        boolean normalizationFlag;
        @Component(name = "modulationCheckBox")
        boolean modulationFlag;
        @Component(name = "thresholdSlider")
        int threshold;
        @Component(name = "gammaSlider")
        int gamma = 100;
        @Component(name = "thresholdCheckBox")
        boolean thresholdFlag;
        @Component(name = "autoLevelCheckBox")
        boolean autoLevel;
        @Component(name = "equalizeCheckBox")
        boolean equalize;
        @Component(name = "typeGrayscaleCheckBox")
        boolean typeGrayscale;
        @Component(name = "gammaCheckBox")
        boolean gammaFlag;
        @Component(name = "parallelCheckBox")
        boolean parallelFlag;
        @Component(name = "rawArgs", args = JTextFieldBinder.DETECT_ENTER)
        String rawArgs;
        /** generated ImageMagick options */
        String command;
        void reset() {
            modulation = 100;
            gamma = 100;
            contrast = 0;
            channelRedSeparationFlag = false;
            normalizationFlag = false;
            modulationFlag = false;
            thresholdFlag = false;
            threshold = 0;
            autoLevel = false;
            typeGrayscale = false;
            gammaFlag = false;
            rawArgs = null;
            Components.Util.rebind(this, app);
        }
    }

    /** */
    void updateRightImage(MagickParams params) {
        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        //
        BufferedImageOp filter = new ImageMagickOp(params);
        BufferedImage filteredImage = filter.filter(image, null);

        //
        rightImageComponent.setImage(filteredImage);
        rightImageComponent.repaint();

        // params is not bound
        statusLabel.setText(params.command);

        frame.setCursor(Cursor.getDefaultCursor());
    }

    /** */
    static class ImageMagickOp extends AbstractBufferedImageOp {
        MagickParams params;
        ImageMagickOp(MagickParams params) {
            this.params = params;
        }
        /**
         * @param dst not used
         */
        public BufferedImage filter(BufferedImage src, BufferedImage dst) {
            try {
                //
                File outFile = File.createTempFile("magick_in_", ".png");
                outFile.deleteOnExit();
                File inFile = File.createTempFile("magick_out_", ".png");
                inFile.deleteOnExit();

                ImageIO.write(src, "png", outFile);

                IMOperation op = new IMOperation();
                op.addImage(outFile.getAbsolutePath());

                if (params.channelRedSeparationFlag) {
                    op.channel("RED");
                    op.separate();
                }

                if (params.typeGrayscale) {
                    op.type("Grayscale");
                }

                if (params.autoLevel) {
                    op.autoLevel();
                }

                if (params.equalize) {
                    op.equalize();
                }

                if (params.modulationFlag) {
                    op.modulate((double) params.modulation);
                }

                if (params.thresholdFlag) {
                    op.blackThreshold((double) params.threshold, true);
                }

                if (params.gammaFlag) {
                    op.gamma(params.gamma / 100d);
                }

                if (params.normalizationFlag) {
                    op.normalize();
                }

                for (int i = 0; i < params.contrast; i++) {
                    op.p_contrast();
                }

                if (params.rawArgs != null && !params.rawArgs.isEmpty()) {
                    op.addRawArgs(params.rawArgs.trim().split("[\\s+]"));
                }

                op.strip();

                op.addImage(inFile.getAbsolutePath());

                List<String> args = op.getCmdArgs();
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < args.size() - 1; i++) {
                     sb.append(args.get(i)).append(" ");
                }
                params.command = sb.toString();
Debug.println(params.command);

long t = System.currentTimeMillis();
                ConvertCmd convert = new ConvertCmd();
                convert.setSearchPath(params.path);
                convert.run(op);
Debug.println((System.currentTimeMillis() - t) + " ms");

                //
                dst = ImageIO.read(inFile);
                return dst;

            } catch (IOException | InterruptedException | IM4JavaException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}
