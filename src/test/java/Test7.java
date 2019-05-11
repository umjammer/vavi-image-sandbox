
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.junit.jupiter.api.Disabled;


/**
 * Test7. (JPEG 2000 profiling)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/26 nsano initial version <br>
 */
@Disabled
public class Test7 {

    /**
     * @param args image
     */
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
