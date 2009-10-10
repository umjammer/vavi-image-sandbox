

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;


/**
 * Test8. (JPEG SIMD) 
 *
 * @author <a href="mailto:sano-n@klab.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/26 nsano initial version <br>
 */
public class Test8 {

    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);
        BufferedImage image = ImageIO.read(file);

        ImageWriter iw = null;

        String className = "vavix.imageio.jpeg.JPEGImageWriter";
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("no such ImageWriter: " + className);
        }
        Iterator<ImageWriter> iws = ImageIO.getImageWritersByFormatName("JPEG");
        while (iws.hasNext()) {
            ImageWriter tmpIw = iws.next();
            // BUG? JPEG �� ImageWriter �� Thread Safe ����Ȃ��C������
            if (clazz.isInstance(tmpIw)) {
                iw = tmpIw;
System.err.println("ImageWriter: " + iw.getClass());
                break;
            }
        }
        if (iw == null) {
            throw new IllegalStateException("no suitable ImageWriter");
        }

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
