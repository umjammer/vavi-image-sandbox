
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import vavi.awt.image.resample.FfmpegResampleOp;


/**
 * Test6. (JPEG 2000 scale)
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/26 nsano initial version <br>
 */
public class Test6 {

    /**
     * @param args input output
     */
    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);
        File j2k = new File(args[1]);

        BufferedImage image = ImageIO.read(file);

        BufferedImage filteredImage = new FfmpegResampleOp(.5f, .5f, FfmpegResampleOp.Hint.LANCZOS).filter(image, null);

        ImageWriter writer = ImageIO.getImageWritersByFormatName("JPEG2000").next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(new FileOutputStream(j2k));
        writer.setOutput(ios);
        ImageWriteParam iwp = writer.getDefaultWriteParam();
        writer.write(null, new IIOImage(filteredImage, null, null), iwp);
        ios.flush();
        ios.close();
    }
}

/* */
