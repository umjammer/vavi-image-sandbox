/*
 * Copyright (c) 2009 by KLab Inc., All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.quantization;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import javax.swing.JOptionPane;

import org.junit.jupiter.api.Test;

import vavi.awt.image.gif.GifRenderer;
import vavi.awt.image.resample.AwtResampleOp;
import vavi.xml.util.PrettyPrinter;
import vavi.xml.util.XmlUtil;

import vavix.awt.image.color.FillTransparentDiffIndexOp;
import vavix.awt.image.color.PalettizeOp;
import vavix.awt.image.pixel.CropTransparentIndexOp;
import vavix.awt.image.quantization.FixedColorModelQuantizeOp;


/**
 * FixedColorModelQuantizerOPTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/06/02 nsano initial version <br>
 */
public class FixedColorModelQuantizerOpTest {

    String input = "src/test/resources/sample.gif";

    String output = "tmp/vavi.awt.image.resample.FixedColorModelQuantizerOpTest.gif";

    @Test
    public void test01() throws Exception {
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream iis = new FileImageInputStream(new File(input));
        reader.setInput(iis, true);

        List<BufferedImage> images = new ArrayList<>();
        List<BufferedImage> backupImages = new ArrayList<>();

        GifRenderer renderer = new GifRenderer();
        IndexColorModel colorModel = null;
        float scale = .5f;

        for (int i = 0;; i++) {
            BufferedImage image;
            try {
                image = reader.read(i);
                if (i == 0) {
                    colorModel = (IndexColorModel) image.getColorModel();
                }
            } catch (IndexOutOfBoundsException e) {
                break;
            }

            IIOMetadata imageMetaData = reader.getImageMetadata(i);
            BufferedImage renderedImage = renderer.addFrame(image, imageMetaData);
            BufferedImage tempImage1 = new AwtResampleOp(scale, scale).filter(renderedImage, null);
            BufferedImage tempImage2 = new FixedColorModelQuantizeOp(colorModel).filter(tempImage1, null);
            tempImage1.flush();
            BufferedImage tempImage3 = new PalettizeOp(256).filter(tempImage2, null);
            tempImage2.flush();
            BufferedImage processedImage;
            processedImage = tempImage3;
            backupImages.add(tempImage3);

System.out.println("Image:" + i);
            if (processedImage.getColorModel() instanceof IndexColorModel) {
                IndexColorModel originalModel = (IndexColorModel) processedImage.getColorModel();
                int size = originalModel.getMapSize();
                byte[] reds = new byte[size];
                byte[] greens = new byte[size];
                byte[] blues = new byte[size];
                originalModel.getReds(reds);
                originalModel.getGreens(greens);
                originalModel.getBlues(blues);
//for (int j = 0; j < size; j++) {
// System.out.print(reds[j] + ",");
// System.out.print(greens[j] + ",");
// System.out.println(blues[j]);
//}
            } else {
System.err.println("image does not have palette");
            }

            images.add(processedImage);
        }

        iis.close();

        //
        ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();
System.err.println("writer: " + writer);
        ImageOutputStream ios = new FileImageOutputStream(new File(output));

        ImageWriteParam imageWriteParam = writer.getDefaultWriteParam();

        writer.setOutput(ios);
        writer.prepareWriteSequence(null);

        for (BufferedImage image : images) {
            ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(image);
            IIOMetadata imageMetaData = writer.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
            String metaFormatName = imageMetaData.getNativeMetadataFormatName();
            IIOMetadataNode metadataNode = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

            IIOMetadataNode imageDescriptorNode = XmlUtil.getNode(metadataNode, "ImageDescriptor");
            imageDescriptorNode.setAttribute("imageLeftPosition", String.valueOf(0));
            imageDescriptorNode.setAttribute("imageTopPosition", String.valueOf(0));
            imageDescriptorNode.setAttribute("imageWidth", String.valueOf((int) (image.getWidth() * scale)));
            imageDescriptorNode.setAttribute("imageHeight", String.valueOf((int) (image.getHeight() * scale)));
            imageDescriptorNode.setAttribute("interlaceFlag", String.valueOf(false));
            metadataNode.appendChild(imageDescriptorNode);

            IIOMetadataNode commentExtensionsNode = new IIOMetadataNode("CommentExtensions");
            IIOMetadataNode commentExtensionNode = new IIOMetadataNode("CommentExtension");
            commentExtensionNode.setAttribute("value", "KLab");

            IIOMetadataNode graphicControlExtensionNode = new IIOMetadataNode("GraphicControlExtension");
            graphicControlExtensionNode.setAttribute("disposalMethod", "restoreToBackgroundColor");
            graphicControlExtensionNode.setAttribute("transparentColorFlag", String.valueOf(false));
            graphicControlExtensionNode.setAttribute("transparentColorIndex", String.valueOf(0));
            graphicControlExtensionNode.setAttribute("userInputFlag", String.valueOf(false));

            commentExtensionsNode.appendChild(commentExtensionNode);
            metadataNode.appendChild(commentExtensionsNode);

            imageMetaData.setFromTree(metaFormatName, metadataNode);

            if (System.getProperty("vavi.test", "").equals("ide"))
                JOptionPane.showMessageDialog(null, null, "01", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(image));
            writer.writeToSequence(new IIOImage(image, null, imageMetaData), imageWriteParam);
            image.flush();
        }

        writer.endWriteSequence();

        ios.flush();
        ios.close();
    }

    String output2 = "tmp/vavi.awt.image.resample.FixedColorModelQuantizerOpTest_2.gif";

    @Test
    public void test02() throws Exception {
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream iis = new FileImageInputStream(new File(input));
        reader.setInput(iis, true);

        List<BufferedImage> images = new ArrayList<>();
        List<BufferedImage> backupImages = new ArrayList<>();
        List<IIOMetadataNode> metadataNodes = new ArrayList<>();

        GifRenderer renderer = new GifRenderer();

        float scale = .5f;

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
            BufferedImage tempImage2 = new NeuralNetQuantizeOp(255).filter(tempImage1, null);
            tempImage1.flush();
            BufferedImage tempImage3 = new PalettizeOp(255).filter(tempImage2, null);
            tempImage2.flush();
            BufferedImage processedImage;
            Rectangle bounds = new Rectangle();
            if (i == 0) {
                processedImage = tempImage3;
            } else {
                BufferedImage tempImage4 = new FillTransparentDiffIndexOp(backupImages.get(i - 1)).filter(tempImage3, null);
                processedImage = new CropTransparentIndexOp(bounds).filter(tempImage4, null);
                tempImage4.flush();
            }
System.err.println(processedImage);
if (System.getProperty("vavi.test", "").equals("ide"))
 JOptionPane.showMessageDialog(null, new ImageIcon(tempImage3), "resampled", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(processedImage));
            backupImages.add(tempImage3);
            images.add(processedImage);

            // see "https://docs.oracle.com/javase/8/docs/api/javax/imageio/metadata/doc-files/gif_metadata.html"
            String metaFormatName = imageMetaData.getNativeMetadataFormatName();
            IIOMetadataNode metadataNode = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
            IIOMetadataNode imageDescriptorNode = XmlUtil.getNode(metadataNode, "ImageDescriptor");
            imageDescriptorNode.setAttribute("imageLeftPosition", String.valueOf(bounds.x));
            imageDescriptorNode.setAttribute("imageTopPosition", String.valueOf(bounds.y));
            imageDescriptorNode.setAttribute("imageWidth", String.valueOf((int) (processedImage.getWidth() * scale)));
            imageDescriptorNode.setAttribute("imageHeight", String.valueOf((int) (processedImage.getHeight() * scale)));
            imageDescriptorNode.setAttribute("interlaceFlag", String.valueOf(false));
            IIOMetadataNode graphicControlExtensionNode = XmlUtil.getNode(metadataNode, "GraphicControlExtension");
            graphicControlExtensionNode.setAttribute("disposalMethod", "doNotDispose");
            graphicControlExtensionNode.setAttribute("userInputFlag", String.valueOf(false));
            graphicControlExtensionNode.setAttribute("transparentColorFlag", String.valueOf(false));
            graphicControlExtensionNode.setAttribute("transparentColorIndex", String.valueOf(0));
            metadataNodes.add(metadataNode);
        }

        iis.close();

        //
        ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();
        ImageOutputStream ios = new FileImageOutputStream(new File(output2));

        ImageWriteParam imageWriteParam = writer.getDefaultWriteParam();

        writer.setOutput(ios);
        writer.prepareWriteSequence(null);

        for (int i = 0; i < images.size(); i++) {
            ImageTypeSpecifier imageTypeSpecifier = new ImageTypeSpecifier(images.get(i));
            IIOMetadata imageMetaData = writer.getDefaultImageMetadata(imageTypeSpecifier, imageWriteParam);
            String metaFormatName = imageMetaData.getNativeMetadataFormatName();
new PrettyPrinter(System.err).print(metadataNodes.get(i));
            imageMetaData.setFromTree(metaFormatName, metadataNodes.get(i));

if (System.getProperty("vavi.test", "").equals("ide"))
 JOptionPane.showMessageDialog(null, null, "02", JOptionPane.INFORMATION_MESSAGE, new ImageIcon(images.get(i)));
            writer.writeToSequence(new IIOImage(images.get(i), null, imageMetaData), imageWriteParam);
            images.get(i).flush();
        }

        writer.endWriteSequence();

        ios.flush();
        ios.close();
    }

    @Test
    public void test03() throws Exception {

        int[] cmap = {
            0x00000000, 0xFF202020, 0xFF0000FF, 0xFF808080,
            0xFF00FF00, 0xFF00FFFF, 0xFFFF0000, 0xFFFF00FF,
            0xFFFFFF00, 0xFFFFFFFF, 0x00000000, 0x00000000,
            0x00000000, 0x00000000, 0x00000000, 0x00000000,
        };

        IndexColorModel colorModel = new IndexColorModel(4, cmap.length, cmap, 0, true, 0, java.awt.image.DataBuffer.TYPE_BYTE);

        BufferedImage bufImg = new BufferedImage(30, 22, BufferedImage.TYPE_BYTE_INDEXED, colorModel);
        java.awt.Graphics g = bufImg.getGraphics();
        for (int i = 0; i < 90; i++)
            for (int j = 0; j < 100; j++) {
                int r = ((cmap[i % 10 + 1] >> 16) & 0x000000ff);
                int gr = ((cmap[i % 10 + 1] >> 8) & 0x000000ff);
                int b = ((cmap[i % 10 + 1] >> 0) & 0x000000ff);
//                System.err.printf("%x %x %x\n", r, gr, b);
                g.setColor(new java.awt.Color(r, gr, b));
                // g.setPaintMode();
                g.fillRect(j * 1, i, 1, 1);

            }
        g.dispose();
        BufferedImage tempImage3 = new PalettizeOp(16).filter(bufImg, null);
        bufImg = tempImage3;
        ImageIO.write(bufImg, "png", new File("tmp/out_test03.png"));
    }
 }
