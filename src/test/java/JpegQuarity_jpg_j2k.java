/*
 * Copyright (c) 2007 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vavi.awt.image.resample.AwtResampleOp;
import vavi.imageio.IIOUtil;
import vavi.swing.JImageComponent;
import vavi.util.properties.annotation.Property;
import vavi.util.properties.annotation.PropsEntity;


/**
 * Jpeg quality. (jpg, j2k)
 *
 * j2k reader は ImageConverter#convert のイメージをうまく扱えない
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 07xxxx nsano initial version <br>
 */
@PropsEntity(url = "file://${user.dir}/local.properties")
public class JpegQuarity_jpg_j2k {

    @Property(name = "image.writer.class", value = "com.sun.imageio.plugins.jpeg.JPEGImageWriter")
    String classNameL;
    @Property(name = "image.writer.class2", value = "com.sun.imageio.plugins.jpeg.JPEGImageWriter")
    String classNameR;

    {
        try {
            PropsEntity.Util.bind(this);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        new JpegQuarity_jpg_j2k(args);
    }

    BufferedImage rightImage;
    BufferedImage leftImage;
    JSlider qualitySlider;
    JImageComponent rightImageComponent;
    JImageComponent leftImageComponent;
    JLabel statusLabel;

    JpegQuarity_jpg_j2k(String[] args) throws Exception {
System.err.println(args[0]);
        BufferedImage image = ImageIO.read(new File(args[0]));
        int w = image.getWidth();
        int h = image.getHeight();
        final double S = 1d / 2.5;
        BufferedImageOp filter = new AwtResampleOp(S, S);
        leftImage = filter.filter(image, null);
        rightImage = filter.filter(image, null);
        qualitySlider = new JSlider();
        qualitySlider.setMaximum(95);
        qualitySlider.setMinimum(5);
        qualitySlider.setValue(75);
        qualitySlider.addChangeListener(new ChangeListener() {
            ImageWriter iwL;
            ImageWriter iwR;
            {
                // BUG? JPEG の ImageWriter が Thread Safe じゃない気がする
                iwL = IIOUtil.getImageWriter("JPEG", classNameL);
                iwR = IIOUtil.getImageWriter("JPEG", classNameR);
            }
            public void stateChanged(ChangeEvent event) {
                JSlider source = (JSlider) event.getSource();
                if (source.getValueIsAdjusting()) {
                    return;
                }
                float quality = source.getValue() / 100f;

                try {
                    // L
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageOutputStream ios = ImageIO.createImageOutputStream(baos);
                    iwL.setOutput(ios);

                    ImageWriteParam iwp = iwL.getDefaultWriteParam();
                    iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    iwp.setCompressionQuality(quality);
//System.err.println(iwp.getClass().getName());
                    if (iwp instanceof JPEGImageWriteParam) {
                        ((JPEGImageWriteParam) iwp).setOptimizeHuffmanTables(true);
                    }
//System.err.println(StringUtil.paramString(iwp.getCompressionTypes()));

                    //
                    iwL.write(null, new IIOImage(leftImage, null, null), iwp);
                    ios.flush();
                    ios.close();

                    //
                    BufferedImage processedImage = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));

                    //
                    leftImageComponent.setImage(processedImage);
                    leftImageComponent.repaint();

                    int sizeL = baos.size();

                    // R
                    baos = new ByteArrayOutputStream();
                    ios = ImageIO.createImageOutputStream(baos);
                    iwR.setOutput(ios);

                    double j2kQuality = quality * 2;
                    iwp = iwR.getDefaultWriteParam();
//System.err.println(StringUtil.paramString(iwp));
                    if (iwp instanceof com.github.jaiimageio.jpeg2000.J2KImageWriteParam) {
                        ((com.github.jaiimageio.jpeg2000.J2KImageWriteParam) iwp).setLossless(false);
                        ((com.github.jaiimageio.jpeg2000.J2KImageWriteParam) iwp).setFilter(com.github.jaiimageio.jpeg2000.J2KImageWriteParam.FILTER_97);
                        ((com.github.jaiimageio.jpeg2000.J2KImageWriteParam) iwp).setEncodingRate(j2kQuality);
                    }

                    //
//iwR.write(image);
                    iwR.write(null, new IIOImage(rightImage, null, null), iwp);
                    ios.flush();
                    ios.close();
System.err.println("quality: " + quality + ", size: " + baos.size());

                    //
                    byte[] j2kBytes = baos.toByteArray();
                    OutputStream os = Files.newOutputStream(Paths.get(String.format("tmp%sj2k_%1.2f.jp2", File.separator, j2kQuality)));
                    os.write(j2kBytes);
                    os.flush();
                    os.close();

//                    processedImage = ImageIO.read(new ByteArrayInputStream(baos.toByteArray()));
                    ImageReader imageReader = ImageIO.getImageReadersByFormatName("JPEG2000").next();
                    imageReader.setInput(ImageIO.createImageInputStream(new ByteArrayInputStream(j2kBytes)), false, true);
                    com.github.jaiimageio.jpeg2000.J2KImageReadParam irp = new com.github.jaiimageio.jpeg2000.J2KImageReadParam();
//System.err.println("decodingRate: " + irp.getDecodingRate());
                    irp.setDecodingRate(Double.MAX_VALUE);
                    processedImage = imageReader.read(0, irp);

                    //
                    rightImageComponent.setImage(processedImage);
                    rightImageComponent.repaint();

                    int sizeR = baos.size();
System.err.println("quality: " + quality + ", L size: " + sizeL + ", R size: " + sizeR + " " + iwL + ", " + iwR);
                    statusLabel.setText("quality: " + quality + ", L size: " + sizeL + ", R size: " + sizeR);
                } catch (IOException e) {
                    e.printStackTrace(System.err);
                }
            }
        });

        JPanel basePanel = new JPanel();
        basePanel.setLayout(new BorderLayout());
        basePanel.add(qualitySlider, BorderLayout.NORTH);

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
        frame.setTitle("JPEG | JPEG2000");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setContentPane(scrollPane);
        frame.pack();
        split.setDividerLocation(0.5);
        frame.setVisible(true);
    }
}
