/*
 * Copyright (c) 2006 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.awt.image.gif;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;

import vavi.awt.image.AnimationRenderer;
import vavi.util.Debug;
import vavi.xml.util.XmlUtil;


/**
 * GifRenderer.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 060616 nsano initial version <br>
 */
public class GifRenderer implements AnimationRenderer {

    /** transparent color */
    private static final Color color = new Color(0x00ffffff, true);

    /** rendered frame images */
    private List<BufferedImage> images = new ArrayList<>();
    /** the same index as {@link #images} */
    private List<IIOMetadata> imageMetaDataList = new ArrayList<>();
    /** the same index as {@link #images} */
    private List<Rectangle> imageBoundsList = new ArrayList<>();
    /** the first image width */
    private int imageWidth = 0;
    /** the first image height */
    private int imageHeight = 0;

    @Override
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

            IIOMetadataNode previousImageDescriptorNode = XmlUtil.getNode(previousMetadataNode, "ImageDescriptor");
            int px = Integer.parseInt(previousImageDescriptorNode.getAttribute("imageLeftPosition"));
            int py = Integer.parseInt(previousImageDescriptorNode.getAttribute("imageTopPosition"));
            int pw = Integer.parseInt(previousImageDescriptorNode.getAttribute("imageWidth"));
            int ph = Integer.parseInt(previousImageDescriptorNode.getAttribute("imageHeight"));

            IIOMetadataNode graphicControlExtensionNode = XmlUtil.getNode(previousMetadataNode, "GraphicControlExtension");
            String disposalMethod = graphicControlExtensionNode.getAttribute("disposalMethod");

            switch (disposalMethod) {
            case "notSpecified":
            case "none":
Debug.println(Level.FINE, disposalMethod + ": " + (images.size() - 1) + ", " + images.get(images.size() - 1));
                graphicsR.drawImage(image, null, x, y);
                break;
            case "doNotDispose":
Debug.println(Level.FINE, "disposalMethod: " + disposalMethod);
                graphicsR.drawImage(images.get(images.size() - 1), null, 0, 0);
                graphicsR.drawImage(image, null, x, y);
                break;
            case "restoreToPrevious":
Debug.println(Level.FINE, "restoreToPrevious: " + (images.size() - 1) + ", " + images.get(images.size() - 1));
                graphicsR.drawImage(images.get(images.size() - 1), null, 0, 0);
                break;
            case "restoreToBackgroundColor":
Debug.println(Level.FINER, "disposalMethod: " + disposalMethod);
                graphicsR.setBackground(color);
                graphicsR.clearRect(px, py, pw, ph);
                graphicsR.drawImage(image, null, x, y);
                break;
            }
        } else {
            graphicsR.drawImage(image, null, x, y);
        }

        images.add(imageR);
        imageBoundsList.add(new Rectangle(x, y, image.getWidth(), image.getHeight()));

        return imageR;
    }

    @Override
    public int getDelayTime(IIOMetadata imageMetaData) {
        String metaFormatName = imageMetaData.getNativeMetadataFormatName();
        IIOMetadataNode metadataNode = (IIOMetadataNode) imageMetaData.getAsTree(metaFormatName);

        IIOMetadataNode graphicControlExtensionNode = XmlUtil.getNode(metadataNode, "GraphicControlExtension");
        return Integer.parseInt(graphicControlExtensionNode.getAttribute("delayTime")) * 10;
    }

    @Override
    public int getDelayTime(int index) {
        return getDelayTime(imageMetaDataList.get(index));
    }

    @Override
    public Rectangle getBounds(int index) {
        return imageBoundsList.get(index);
    }

    @Override
    public int size() {
        return images.size();
    }

    @Override
    public BufferedImage get(int index) {
        return images.get(index);
    }

    @Override
    public Iterator<BufferedImage> iterator() {
        return images.iterator();
    }
}

/* */
