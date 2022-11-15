
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import vavi.imageio.IIOUtil;


/**
 * Jpeg_simd. (JPEG SIMD)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/26 nsano initial version <br>
 */
public class Jpeg_simd {

    /**
     * @param args input
     */
    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);
        BufferedImage image = ImageIO.read(file);

        ImageWriter iw = IIOUtil.getImageWriter("JPEG", "vavix.imageio.jpeg.JPEGImageWriter");

        OutputStream os = Files.newOutputStream(Paths.get(args[1]));
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
