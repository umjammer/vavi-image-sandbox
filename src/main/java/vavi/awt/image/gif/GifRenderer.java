/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.gif;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import vavix.util.XmlUtil;


/**
 * GifRenderer.
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class GifRenderer {

    /** */
    private static final Color color = new Color(0xffffff, true);

    /** */
    private BufferedImage image1 = null;
    /** */
    private BufferedImage image2 = null;
    /** */
    private Graphics2D graphics1 = null;
    /** */
    private Graphics2D graphics2 = null;
    /** */
    private int imageWidth = 0;
    /** */
    private int imageHeight = 0;

    /** */
    private boolean inited;

    /** */
    public BufferedImage render(BufferedImage image, IIOMetadata imageMetaData) {

        if (!inited) {
            imageWidth = image.getWidth();
            imageHeight = image.getHeight();
            image1 = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
            graphics1 = image1.createGraphics();
            graphics1.setBackground(color);
            graphics1.clearRect(0, 0, imageWidth, imageHeight);
            inited = true;
        }

        String metaFormatName = imageMetaData.getNativeMetadataFormatName();
        IIOMetadataNode metadataNode = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

        IIOMetadataNode imageDescriptorNode = XmlUtil.getNode(metadataNode, "ImageDescriptor");
        int x = Integer.parseInt(imageDescriptorNode.getAttribute("imageLeftPosition"));
        int y = Integer.parseInt(imageDescriptorNode.getAttribute("imageTopPosition"));
        int w = Integer.parseInt(imageDescriptorNode.getAttribute("imageWidth"));
        int h = Integer.parseInt(imageDescriptorNode.getAttribute("imageHeight"));

        IIOMetadataNode graphicControlExtensionNode = XmlUtil.getNode(metadataNode, "GraphicControlExtension");
        String disposalMethod = graphicControlExtensionNode.getAttribute("disposalMethod");
//System.err.println("disposalMethod: " + disposalMethod);

        BufferedImage renderedImage = null;
        if (disposalMethod.equals("notSpecified") || disposalMethod.equals("doNotDispose")||disposalMethod.equals("none")) {
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

        return renderedImage;
    }

    public void dispose() {
        if (graphics1 != null) {
            graphics1.dispose();
        }
        if (image1 != null) {
            image1.flush();
        }
        if (graphics2 != null) {
            graphics2.dispose();
        }
        if (image2 != null) {
            image2.flush();
        }
    }
}

/* */
