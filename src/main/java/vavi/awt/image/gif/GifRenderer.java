/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.gif;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import vavi.xml.util.XmlUtil;


/**
 * GifRenderer.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class GifRenderer implements Iterable<BufferedImage> {

    /** transparent color */
    private static final Color color = new Color(0x00ffffff, true);

    /** rendered frame images */
    private List<BufferedImage> images = new ArrayList<>();
    /** the same index as {@link #images} */
    private List<IIOMetadata> imageMetaDataList = new ArrayList<>();
    /** the first image width */
    private int imageWidth = 0;
    /** the first image height */
    private int imageHeight = 0;

    /**
     * Adds a frame image with it's image meta data.
     * @return rendered frame image
     */
    public BufferedImage addFrame(BufferedImage image, IIOMetadata imageMetaData) {

        if (images.size() == 0) {
            imageWidth = image.getWidth();
            imageHeight = image.getHeight();
        }

        imageMetaDataList.add(imageMetaData);

        String metaFormatName = imageMetaData.getNativeMetadataFormatName();
        IIOMetadataNode metadataNode = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);
//try {
// new PrettyPrinter(System.err).print(metadataNode);
//} catch (IOException e) {
// throw new IllegalStateException(e);
//}
        IIOMetadataNode imageDescriptorNode = XmlUtil.getNode(metadataNode, "ImageDescriptor");
        int x = Integer.parseInt(imageDescriptorNode.getAttribute("imageLeftPosition"));
        int y = Integer.parseInt(imageDescriptorNode.getAttribute("imageTopPosition"));
//        int w = Integer.parseInt(imageDescriptorNode.getAttribute("imageWidth"));
//        int h = Integer.parseInt(imageDescriptorNode.getAttribute("imageHeight"));

        BufferedImage imageR = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphicsR = imageR.createGraphics();

        if (images.size() > 0) {
            IIOMetadataNode previousMetadataNode = (IIOMetadataNode) imageMetaDataList.get(imageMetaDataList.size() - 2).getAsTree(metaFormatName);

            IIOMetadataNode previouosImageDescriptorNode = XmlUtil.getNode(previousMetadataNode, "ImageDescriptor");
            int px = Integer.parseInt(previouosImageDescriptorNode.getAttribute("imageLeftPosition"));
            int py = Integer.parseInt(previouosImageDescriptorNode.getAttribute("imageTopPosition"));
            int pw = Integer.parseInt(previouosImageDescriptorNode.getAttribute("imageWidth"));
            int ph = Integer.parseInt(previouosImageDescriptorNode.getAttribute("imageHeight"));

            IIOMetadataNode graphicControlExtensionNode = XmlUtil.getNode(previousMetadataNode, "GraphicControlExtension");
            String disposalMethod = graphicControlExtensionNode.getAttribute("disposalMethod");
//System.err.println("disposalMethod: " + disposalMethod);

            if (disposalMethod.equals("notSpecified") || disposalMethod.equals("none")) {
                graphicsR.drawImage(images.get(images.size() - 1), null, 0, 0);
                graphicsR.drawImage(image, null, x, y);
            } else if (disposalMethod.equals("doNotDispose")) {
                graphicsR.drawImage(image, null, x, y);
            } else if (disposalMethod.equals("restoreToPrevious")) {
                graphicsR.drawImage(images.get(images.size() - 1), null, 0, 0);
            } else if (disposalMethod.equals("restoreToBackgroundColor")) {
                graphicsR.setBackground(color);
                graphicsR.clearRect(px, py, pw, ph);
                graphicsR.drawImage(image, null, x, y);
            }
        } else {
            graphicsR.drawImage(image, null, x, y);
        }

        images.add(imageR);

        return imageR;
    }

    /** Gets delay time of frame image in [msec]. */
    public int getDelayTime(IIOMetadata imageMetaData) {
        String metaFormatName = imageMetaData.getNativeMetadataFormatName();
        IIOMetadataNode metadataNode = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

        IIOMetadataNode graphicControlExtensionNode = XmlUtil.getNode(metadataNode, "GraphicControlExtension");
        return Integer.parseInt(graphicControlExtensionNode.getAttribute("delayTime")) * 10;
    }

    /** Gets delay time of frame image in [msec]. */
    public int getDelayTime(int index) {
        return getDelayTime(imageMetaDataList.get(index));
    }

    /** Gets a number of frame images */
    public int size() {
        return images.size();
    }

    /** Gets a rendered frame image specified by the index number. */
    public BufferedImage get(int index) {
        return images.get(index);
    }

    /* Gets a rendered frame image iterator. */
    public Iterator<BufferedImage> iterator() {
        return images.iterator();
    }
}

/* */
