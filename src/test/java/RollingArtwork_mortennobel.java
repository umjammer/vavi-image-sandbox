
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

import vavix.awt.image.pixel.SimpleDrawOp;


/**
 * RollingArtwork. Ranczos3 (com.mortennobel.imagescaling)
 *
 * @see "https://github.com/mortennobel/java-image-scaling"
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/26 nsano initial version <br>
 */
public class RollingArtwork_mortennobel {

    String file = "/erika.jpg";

    @Test
    public void test01() throws Exception {
        BufferedImage image = ImageIO.read(RollingArtwork_mortennobel.class.getResourceAsStream(file));
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImageOp filter1 = new SimpleDrawOp(0, 0, w, h);
        BufferedImage image2 = filter1.filter(image, null);
        ResampleOp filter2 = new ResampleOp(w / 2, h / 2);
        filter2.setFilter(ResampleFilters.getLanczos3Filter());
        BufferedImage filteredImage = filter2.filter(image2, null);
System.err.println(filteredImage);
    }
}
