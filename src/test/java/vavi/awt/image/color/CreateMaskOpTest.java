/*
 * Copyright (c) 2009 by KLab Inc., All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.color;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.junit.jupiter.api.Test;

import vavi.awt.image.gif.GifRenderer;
import vavi.awt.image.quantization.NeuralNetQuantizeOp;
import vavi.awt.image.resample.AwtResampleOp;
import vavi.awt.image.resample.ResampleMaskOp;
import vavix.awt.image.color.CreateMaskIndexOp;
import vavix.awt.image.color.FillTransparentDiffIndexOp;
import vavix.awt.image.color.MaskAsTransparentIndexOp;
import vavix.awt.image.color.PalettizeOp;

import vavi.xml.util.PrettyPrinter;
import vavi.xml.util.XmlUtil;


/**
 * CreateMaskOpTest.
 * 
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/13 nsano initial version <br>
 */
public class CreateMaskOpTest {

    BufferedImage image;

    public CreateMaskOpTest() throws IOException {
        this.image = ImageIO.read(CreateMaskOpTest.class.getResourceAsStream("/sample.gif"));
    }

//    @Test
    public void test02() throws Exception {
        int w = image.getWidth();
        int h = image.getHeight();
        int[] pixels = image.getRaster().getPixels(0, 0, w, h, (int[]) null);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int i = y * w + x;
                System.err.printf("%d, %d : %08x %08x\n", x, y, pixels[i], image.getRGB(x, y));
            }
        }
    }

    String output = "tmp/vavi.awt.image.color.CreateMaskOpTest.gif";

//    @Test
    public void test01() throws Exception {
        List<BufferedImage> images = new ArrayList<>();
        List<IIOMetadataNode> metadataNodes = new ArrayList<>();

        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream iis = new FileImageInputStream(new File("src/test/resources/sample.gif"));
        reader.setInput(iis, true);
        for (int i = 0;; i++) {
            try {
                BufferedImage image = reader.read(i);

                IIOMetadata imageMetaData = reader.getImageMetadata(i);
                String metaFormatName = imageMetaData.getNativeMetadataFormatName();
                IIOMetadataNode metadataNode = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

                BufferedImage maskImage = new CreateMaskIndexOp().filter(image, null);
                BufferedImage tempImage = new ResampleMaskOp(.5f, .5f).filter(maskImage, null);
if (System.getProperty("vavi.test", "").equals("ide"))
 JOptionPane.showMessageDialog(null, new ImageIcon(maskImage), "mask", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(tempImage));
                maskImage.flush();
                maskImage = tempImage;

                BufferedImage halfImage = new AwtResampleOp(.5, .5).filter(image, null);
                tempImage = new MaskAsTransparentIndexOp(maskImage).filter(halfImage, null);
if (System.getProperty("vavi.test", "").equals("ide"))
 JOptionPane.showMessageDialog(null, new ImageIcon(halfImage), "half", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(tempImage));
                halfImage.flush();
                halfImage = tempImage;

                scaleMetadata(metadataNode, .5f, .5f);

                images.add(halfImage);
                metadataNodes.add(metadataNode);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }
        iis.close();

        //
        ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();
        ImageOutputStream ios = new FileImageOutputStream(new File(output));

        ImageWriteParam imageWriteParam = writer.getDefaultWriteParam();

        writer.setOutput(ios);
        writer.prepareWriteSequence(null);

        for (int i = 0; i < images.size(); i++) {
            ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(images.get(i));
            IIOMetadata imageMetaData = writer.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
            String metaFormatName = imageMetaData.getNativeMetadataFormatName();
            imageMetaData.setFromTree(metaFormatName, metadataNodes.get(i));

            writer.writeToSequence(new IIOImage(images.get(i), null, imageMetaData), imageWriteParam);
        }

        writer.endWriteSequence();

        ios.flush();
        ios.close();
    }

    /** */
    private void scaleMetadata(IIOMetadataNode metadataNode, float scaleX, float scaleY) {
        IIOMetadataNode imageDescriptorNode = XmlUtil.getNode(metadataNode, "ImageDescriptor");

        int imageLeftPosition = Integer.parseInt(imageDescriptorNode.getAttribute("imageLeftPosition"));
        int imageTopPosition = Integer.parseInt(imageDescriptorNode.getAttribute("imageTopPosition"));
        int imageWidth = Integer.parseInt(imageDescriptorNode.getAttribute("imageWidth"));
        int imageHeight = Integer.parseInt(imageDescriptorNode.getAttribute("imageHeight"));
        boolean interlaceFlag = Boolean.parseBoolean(imageDescriptorNode.getAttribute("interlaceFlag"));
        imageDescriptorNode.setAttribute("imageLeftPosition", String.valueOf((int) (imageLeftPosition * scaleX)));
        imageDescriptorNode.setAttribute("imageTopPosition", String.valueOf((int) (imageTopPosition * scaleY)));
        imageDescriptorNode.setAttribute("imageWidth", String.valueOf((int) (imageWidth * scaleX)));
        imageDescriptorNode.setAttribute("imageHeight", String.valueOf((int) (imageHeight * scaleY)));
        imageDescriptorNode.setAttribute("interlaceFlag", String.valueOf(interlaceFlag));
    }

//    @Test
    public void test04() throws Exception {
        List<BufferedImage> images = new ArrayList<>();
        List<IIOMetadataNode> metadataNodes = new ArrayList<>();

        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream iis = new FileImageInputStream(new File("src/test/resources/sample.gif"));
        reader.setInput(iis, true);

        BufferedImage image1 = null;
        BufferedImage image2 = null;
        Graphics2D graphics1 = null;
        Graphics2D graphics2 = null;
        int imageWidth = 0;
        int imageHeight = 0;
        ColorModel colorModel = null; 
        Map<RenderingHints.Key, Object> map = new HashMap<>();
        map.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        map.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        map.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        map.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        map.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        map.put(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        map.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
        RenderingHints hints = new RenderingHints(map);

        final Color color = new Color(0xffffff, true);

        for (int i = 0;; i++) {
            try {
                BufferedImage image = reader.read(i);

                if (i == 0) {
                    imageWidth = image.getWidth(); 
                    imageHeight = image.getHeight(); 
                    image1 = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
                    graphics1 = image1.createGraphics();
                    graphics1.setBackground(color);
                    graphics1.clearRect(0, 0, imageWidth, imageHeight);
                }

                IIOMetadata imageMetaData = reader.getImageMetadata(i);
                String metaFormatName = imageMetaData.getNativeMetadataFormatName();
                IIOMetadataNode metadataNode = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
XmlUtil.printNode(String.valueOf(i), metadataNode);

                IIOMetadataNode imageDescriptorNode = XmlUtil.getNode(metadataNode, "ImageDescriptor");
                int x = Integer.parseInt(imageDescriptorNode.getAttribute("imageLeftPosition"));
                int y = Integer.parseInt(imageDescriptorNode.getAttribute("imageTopPosition"));
                int w = Integer.parseInt(imageDescriptorNode.getAttribute("imageWidth"));
                int h = Integer.parseInt(imageDescriptorNode.getAttribute("imageHeight"));

                IIOMetadataNode graphicControlExtensionNode = XmlUtil.getNode(metadataNode, "GraphicControlExtension");
                String disposalMethod = graphicControlExtensionNode.getAttribute("disposalMethod");
System.err.println("disposalMethod: " + disposalMethod);

                BufferedImage renderedImage = null;
                if (disposalMethod.equals("notSpecified") || disposalMethod.equals("doNotDispose")) {
                    graphics1.drawImage(image, null, x, y);
                    renderedImage = image1;
                } else if (disposalMethod.equals("restoreToPrevious")) {
                    if (image2 == null) {
                        image2 = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
                        graphics2 = image2.createGraphics();
                        graphics2.setBackground(color);
                        graphics2.clearRect(0, 0, w, h);
                    }
                    int data1[] = ((DataBufferInt) image1.getRaster().getDataBuffer()).getData();
                    int data2[] = ((DataBufferInt) image2.getRaster().getDataBuffer()).getData();
                    System.arraycopy(data1, 0, data2, 0, data1.length);
                    graphics2.drawImage(image, null, x, y);
                    renderedImage = image2;
                } else if (disposalMethod.equals("restoreToBackgroundColor")) {
                    if (image2 == null) {
                        image2 = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
                        graphics2 = image2.createGraphics();
                    }
                    int data1[] = ((DataBufferInt) image1.getRaster().getDataBuffer()).getData();
                    int data2[] = ((DataBufferInt) image2.getRaster().getDataBuffer()).getData();
                    System.arraycopy(data1, 0, data2, 0, data1.length);
                    graphics2.drawImage(image, null, x, y);
                    renderedImage = image2;
                    graphics1.setBackground(color);
                    graphics1.clearRect(x, y, w, h);
                }

                BufferedImage tempImage1 = new AwtResampleOp(.5, .5).filter(renderedImage, null);
                BufferedImage tempImage2 = new NeuralNetQuantizeOp(256).filter(tempImage1, null);
                tempImage1.flush();
                BufferedImage tempImage3 = new PalettizeOp(256).filter(tempImage2, null);
                tempImage2.flush();
                BufferedImage resampledImage;
                if (i == 0) {
                    resampledImage = tempImage3;
                    colorModel = resampledImage.getColorModel();
                } else {
                    BufferedImage tempImage4 = new BufferedImage(colorModel, colorModel.createCompatibleWritableRaster(tempImage3.getWidth(), tempImage3.getHeight()), colorModel.isAlphaPremultiplied(), null); 
                    new ColorConvertOp(tempImage3.getColorModel().getColorSpace(), hints).filter(tempImage3, tempImage4);
                    tempImage3.flush();
                    resampledImage = new FillTransparentDiffIndexOp(images.get(i - 1)).filter(tempImage4, null);
                    tempImage3.flush();
                }
System.err.println(resampledImage);
if (System.getProperty("vavi.test", "").equals("ide"))
 JOptionPane.showMessageDialog(null, new ImageIcon(tempImage3), "resampled", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(resampledImage));
                images.add(resampledImage);

                imageDescriptorNode.setAttribute("imageLeftPosition", String.valueOf(0));
                imageDescriptorNode.setAttribute("imageTopPosition", String.valueOf(0));
                imageDescriptorNode.setAttribute("imageWidth", String.valueOf((int) (imageWidth * .5)));
                imageDescriptorNode.setAttribute("imageHeight", String.valueOf((int) (imageHeight * .5)));
                imageDescriptorNode.setAttribute("interlaceFlag", String.valueOf(false));
                graphicControlExtensionNode.setAttribute("disposalMethod", "doNotDispose");
                metadataNodes.add(metadataNode);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        iis.close();

        //
        ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();
        ImageOutputStream ios = new FileImageOutputStream(new File(output));

        ImageWriteParam imageWriteParam = writer.getDefaultWriteParam();

        writer.setOutput(ios);
        writer.prepareWriteSequence(null);

        for (int i = 0; i < images.size(); i++) {
            ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(images.get(i));
            IIOMetadata imageMetaData = writer.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
            String metaFormatName = imageMetaData.getNativeMetadataFormatName();
            imageMetaData.setFromTree(metaFormatName, metadataNodes.get(i));

            writer.writeToSequence(new IIOImage(images.get(i), null, imageMetaData), imageWriteParam);
            images.get(i).flush();
        }

        writer.endWriteSequence();

        ios.flush();
        ios.close();

        image1.flush();
        if (image2 != null) {
            image2.flush();
        }
    }

    @Test
    public void test06() throws Exception {
        float scale = .5f;

        GifRenderer renderer = new GifRenderer();

        List<BufferedImage> images = new ArrayList<>();
        List<BufferedImage> backupImages = new ArrayList<>();
        List<IIOMetadataNode> metadataNodes = new ArrayList<>();

        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream iis = new FileImageInputStream(new File("src/test/resources/sample.gif"));
        reader.setInput(iis, true);

        for (int i = 0;; i++) {
            BufferedImage image;
            try {
                image = reader.read(i);
            } catch (IndexOutOfBoundsException e) {
                break;
            }

            IIOMetadata imageMetaData = reader.getImageMetadata(i);

            BufferedImage renderedImage = renderer.addFrame(image, imageMetaData);

            BufferedImage tempImage1 = new AwtResampleOp(scale, scale).filter(renderedImage, null);
            BufferedImage tempImage2 = new NeuralNetQuantizeOp(256).filter(tempImage1, null);
            tempImage1.flush();
            BufferedImage tempImage3 = new PalettizeOp(256).filter(tempImage2, null);
            tempImage2.flush();
            BufferedImage processedImage;
            if (i == 0) {
                processedImage = tempImage3;
            } else {
                processedImage = new FillTransparentDiffIndexOp(backupImages.get(i - 1)).filter(tempImage3, null);
            }
System.err.println(processedImage);
if (System.getProperty("vavi.test", "").equals("ide"))
 JOptionPane.showMessageDialog(null, new ImageIcon(tempImage3), "resampled", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(processedImage));
            backupImages.add(tempImage3);
            images.add(processedImage);

            String metaFormatName = imageMetaData.getNativeMetadataFormatName();
            IIOMetadataNode metadataNode = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
            IIOMetadataNode imageDescriptorNode = XmlUtil.getNode(metadataNode, "ImageDescriptor");
            imageDescriptorNode.setAttribute("imageLeftPosition", String.valueOf(0));
            imageDescriptorNode.setAttribute("imageTopPosition", String.valueOf(0));
            imageDescriptorNode.setAttribute("imageWidth", String.valueOf((int) (processedImage.getWidth() * scale)));
            imageDescriptorNode.setAttribute("imageHeight", String.valueOf((int) (processedImage.getHeight() * scale)));
            imageDescriptorNode.setAttribute("interlaceFlag", String.valueOf(false));
            IIOMetadataNode graphicControlExtensionNode = XmlUtil.getNode(metadataNode, "GraphicControlExtension");
            graphicControlExtensionNode.setAttribute("disposalMethod", "doNotDispose");
            graphicControlExtensionNode.setAttribute("transparentColorFlag", String.valueOf(false));
            graphicControlExtensionNode.setAttribute("transparentColorIndex", String.valueOf(0));
            graphicControlExtensionNode.setAttribute("userInputFlag", String.valueOf(false));
            metadataNodes.add(metadataNode);
        }

        iis.close();

        //
        ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();
        ImageOutputStream ios = new FileImageOutputStream(new File(output));

        ImageWriteParam imageWriteParam = writer.getDefaultWriteParam();

        writer.setOutput(ios);
        writer.prepareWriteSequence(null);

        for (int i = 0; i < images.size(); i++) {
            ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(images.get(i));
            IIOMetadata imageMetaData = writer.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
            String metaFormatName = imageMetaData.getNativeMetadataFormatName();
new PrettyPrinter(System.err).print(metadataNodes.get(i));
            imageMetaData.setFromTree(metaFormatName, metadataNodes.get(i));

            writer.writeToSequence(new IIOImage(images.get(i), null, imageMetaData), imageWriteParam);
            images.get(i).flush();
        }

        writer.endWriteSequence();

        ios.flush();
        ios.close();
    }

//    @Test
    public void test05() throws Exception {
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream iis = new FileImageInputStream(new File(output));
        reader.setInput(iis, true);
System.err.println("-------- test05 --------");

        for (int i = 0;; i++) {
            try {
                BufferedImage image = reader.read(i);
System.err.println(image);
            } catch (IndexOutOfBoundsException e) {
                break;
            }
        }

        iis.close();
    }

    @Test
    public void test99() throws Exception {
        final Image image = Toolkit.getDefaultToolkit().createImage(output);
        JPanel panel = new JPanel() {
            public void paint(Graphics g) {
                g.drawImage(image, 0, 0, this);
            }
        };
        MediaTracker mt = new MediaTracker(panel);
        mt.addImage(image, 0);
        mt.waitForID(0);
        panel.setPreferredSize(new Dimension(image.getWidth(panel), image.getHeight(panel)));
        JFrame frame = new JFrame();
        frame.setContentPane(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        Thread.sleep(15000);
    }
}

/* */
