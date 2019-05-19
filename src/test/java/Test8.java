
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.junit.jupiter.api.Disabled;

import vavi.imageio.IIOUtil;


/**
 * Test8. (JPEG SIMD)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/26 nsano initial version <br>
 */
@Disabled
public class Test8 {

    /**
     * @param args input
     */
    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);
        BufferedImage image = ImageIO.read(file);

        ImageWriter iw = IIOUtil.getImageWriter("JPEG", "vavix.imageio.jpeg.JPEGImageWriter");

        OutputStream os = new FileOutputStream(args[1]);
        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        iw.setOutput(ios);

        ImageWriteParam iwp = iw.getDefaultWriteParam();

        //
        iw.write(null, new IIOImage(image, null, null), iwp);
        ios.flush();
        ios.close();
    }
}

/* */
