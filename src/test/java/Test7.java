

import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;


/**
 * Test7. (JPEG 2000 profiling) 
 *
 * @author <a href="mailto:sano-n@klab.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/26 nsano initial version <br>
 */
public class Test7 {

    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);

        ImageReader reader = ImageIO.getImageReadersByFormatName("JPEG2000").next();
        for (int i = 0; i < 10; i++) {
            ImageInputStream iis = ImageIO.createImageInputStream(new FileInputStream(file));
            reader.setInput(iis);
            reader.read(0);
            iis.close();
System.err.println(String.valueOf(i));
        }
    }
}

/* */
