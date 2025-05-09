import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;

import vavi.xml.util.XmlUtil;


/**
 * Jpeg2K. (JPEG 2000)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/26 nsano initial version <br>
 */
public class Jpeg2K {

    /**
     * @param args image output_base_name
     */
    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);
        File j2k_1 = new File(args[1] + "_normal.jp2");
        File j2k_2 = new File(args[1] + "_iwp.jp2");

        BufferedImage image = ImageIO.read(file);

        ImageIO.write(image, "JPEG2000", j2k_1);

        ImageWriter writer = ImageIO.getImageWritersByFormatName("JPEG2000").next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(Files.newOutputStream(j2k_2.toPath()));
        writer.setOutput(ios);
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        writer.write(null, new IIOImage(image, null, null), iwp);
        ios.flush();
        ios.close();

        ImageReader reader = ImageIO.getImageReadersByFormatName("JPEG2000").next();
        ImageInputStream iis = ImageIO.createImageInputStream(Files.newInputStream(j2k_2.toPath()));
        reader.setInput(iis);
        reader.read(0);
        IIOMetadata metaData = reader.getImageMetadata(0);
        String formatName = metaData.getNativeMetadataFormatName();
        XmlUtil.printNode("", (IIOMetadataNode) metaData.getAsTree(formatName));
    }
}
