import java.io.File;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;


/**
 * Jpeg2K_profiling. (JPEG 2000 profiling)
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/26 nsano initial version <br>
 */
public class Jpeg2K_profiling {

    /**
     * @param args image
     */
    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);

        ImageReader reader = ImageIO.getImageReadersByFormatName("JPEG2000").next();
        for (int i = 0; i < 10; i++) {
            ImageInputStream iis = ImageIO.createImageInputStream(Files.newInputStream(file.toPath()));
            reader.setInput(iis);
            reader.read(0);
            iis.close();
System.err.println(i);
        }
    }
}
