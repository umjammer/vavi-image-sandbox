/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.im4java.core.ConvertCmd;
import org.im4java.core.IM4JavaException;
import org.im4java.core.IMOperation;

import vavi.awt.dnd.BasicDTListener;
import vavi.awt.image.AbstractBufferedImageOp;
import vavi.swing.JImageComponent;
import vavi.swing.binding.Component;
import vavi.swing.binding.Components;
import vavi.swing.binding.Updater;

import vavix.awt.image.util.ImageUtil;


/**
 * ImageMagick filter.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 061013 nsano initial version <br>
 */
public class t146_11 {

    static {
        UIManager.getDefaults().put("SplitPane.border", BorderFactory.createEmptyBorder());
        UIManager.getDefaults().put("TextField.background", UIManager.getColor("Panel.background"));
        UIManager.getDefaults().put("TextField.border", BorderFactory.createLineBorder(UIManager.getColor("Panel.background"), 4));
        UIManager.getDefaults().put("ScrollPane.border", BorderFactory.createEmptyBorder());
    }

    static t146_11 app;

    public static void main(String[] args) throws Exception {
        app = new t146_11(args);
    }

    double scale;

    BufferedImage image;
    BufferedImage leftImage;
    BufferedImage rightImage;

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
    JCheckBox typeGrayscaleCheckBox;

    JImageComponent rightImageComponent;
    JImageComponent leftImageComponent;

    JTextField statusLabel;

    /** */
    void updateModel(String file) throws IOException {
System.err.println(file);
        image = ImageIO.read(new File(file));
        scale = ImageUtil.scale(image, 0.8);
        leftImage = ImageUtil.fit(image, scale);
        rightImage = ImageUtil.clone(leftImage);
    }

    /** */
    void updateView() {
        leftImageComponent.setImage(leftImage);
        rightImageComponent.setImage(rightImage);
        leftImageComponent.getParent().getParent().repaint();
    }

    t146_11(String[] args) throws Exception {
        updateModel(args[0]);

        channelRedSeparateCheckBox = new JCheckBox("channel red separate");
        normalizeCheckBox = new JCheckBox("normalize");
        modulationCheckBox = new JCheckBox("modulation");
        thresholdCheckBox = new JCheckBox("threshold");
        gammaCheckBox = new JCheckBox("gamma");
        autoLevelCheckBox = new JCheckBox("auto level");
        typeGrayscaleCheckBox = new JCheckBox("type grayscale");
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(channelRedSeparateCheckBox);
        buttonPanel.add(normalizeCheckBox);
        buttonPanel.add(modulationCheckBox);
        buttonPanel.add(thresholdCheckBox);
        buttonPanel.add(autoLevelCheckBox);
        buttonPanel.add(typeGrayscaleCheckBox);
        buttonPanel.add(gammaCheckBox);

        JPanel upperPanel = new JPanel();
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
        leftImageComponent.setImage(leftImage);
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BorderLayout());
        leftPanel.add(leftImageComponent, BorderLayout.CENTER);
        leftPanel.setPreferredSize(new Dimension(leftImage.getWidth(), leftImage.getHeight()));

        rightImageComponent = new JImageComponent() {
            {
                // this is the DnD target sample for a file name from external applications
                new DropTarget(
                    this, 
                    DnDConstants.ACTION_COPY_OR_MOVE,
                    new BasicDTListener() {

                        @Override
                        protected boolean isDragFlavorSupported(DropTargetDragEvent ev) {
                            return ev.isDataFlavorSupported(DataFlavor.javaFileListFlavor);
                        }

                        @Override
                        protected DataFlavor chooseDropFlavor(DropTargetDropEvent ev) {
                            if (ev.isLocalTransfer() == true && ev.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                                return DataFlavor.javaFileListFlavor;
                            }
                            DataFlavor chosen = null;
                            if (ev.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                                chosen = DataFlavor.javaFileListFlavor;
                            }
                            return chosen;
                        }

                        @SuppressWarnings("unchecked")
                        @Override
                        protected boolean dropImpl(DropTargetDropEvent ev, Object data) {
                            try {
                                updateModel(((List<File>) data).get(0).getPath());
                                updateView();
                                return true;
                            } catch (IOException e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                    },
                    true);
            }
        };
        rightImageComponent.setImage(rightImage);
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.add(rightImageComponent, BorderLayout.CENTER);
        rightPanel.setPreferredSize(new Dimension(leftImage.getWidth(), leftImage.getHeight()));

        final JSplitPane split = new JSplitPane();
        split.setLeftComponent(leftPanel);
        split.setRightComponent(rightPanel);
        split.setPreferredSize(new Dimension(leftImage.getWidth() * 2 + 16, leftImage.getHeight()));

        basePanel.add(split, BorderLayout.CENTER);
        basePanel.addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent event) {
                split.setDividerLocation(0.5);
            }
            public void componentResized(ComponentEvent event) {
                split.setDividerLocation(0.5);
            }
        });

        statusLabel = new JTextField();
        statusLabel.setText("original");
        basePanel.add(statusLabel, BorderLayout.SOUTH);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(basePanel);

        JFrame frame = new JFrame();
        frame.setTitle("original | magick");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(scrollPane);
        frame.pack();
        frame.setVisible(true);

        leftImageComponent.repaint();
        rightImageComponent.repaint();

        Components.Util.bind(new MagickParams(), this);
    }

    /** */
    public static class MyUpdater implements Updater<MagickParams> {
        public void update(MagickParams params) {
            app.updateRightImage(params);
        }
    }

    @Components(updater = MyUpdater.class)
    public static class MagickParams {
        String path = "/usr/local/bin/";
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
        @Component(name = "typeGrayscaleCheckBox")
        boolean typeGrayscale;
        @Component(name = "gammaCheckBox")
        boolean gammaFlag;
        /** generated ImageMagick options */
        String command;
    }

    void updateRightImage(MagickParams params) {
        //
        BufferedImageOp filter = new ImageMagickOp(params);
        BufferedImage filteredImage = ImageUtil.fit(filter.filter(image, null), scale);

        //
        rightImageComponent.setImage(filteredImage);
        rightImageComponent.repaint();

        //
        statusLabel.setText(params.command);
    }

    /** */
    static class ImageMagickOp extends AbstractBufferedImageOp {
        MagickParams params;
        ImageMagickOp(MagickParams params) {
            this.params = params;
        }
        /**
         * {@link #size} will be set.
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

                op.strip();

                op.addImage(inFile.getAbsolutePath());

                List<String> args = op.getCmdArgs();
                params.command = "";
                for (int i = 1; i < args.size() - 1; i++) {
                    params.command += args.get(i) + " ";
                }
                ConvertCmd convert = new ConvertCmd();

                convert.setSearchPath(params.path);
                convert.run(op);

                //
                dst = ImageIO.read(inFile);
                return dst;

            } catch (IOException | InterruptedException | IM4JavaException e) {
                throw new IllegalStateException(e);
            }
        }
    }
}

/* */
