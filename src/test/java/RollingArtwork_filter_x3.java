
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.Test;

import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;

import vavi.awt.image.resample.AwtResampleOp;


/**
 * RollingArtwork. filters x3
 *
 * @see "https://github.com/mortennobel/java-image-scaling"
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2009/05/26 nsano initial version <br>
 */
public class RollingArtwork_filter_x3 {

    String name = "/erika.jpg";

    @Test
    public void test01() throws Exception {
        BufferedImage image = ImageIO.read(RollingArtwork_filter_x3.class.getResourceAsStream(name));
        BufferedImage image2 = new AwtResampleOp(.5f, .5f).filter(image, null);
        ImageIO.write(image2, "JPG", new File("tmp", name.substring(0, name.lastIndexOf('.')) + "_" + "AreaAverage" + ".jpg"));
        BufferedImage image3 = new AwtResampleOp(.5f, .5f, Image.SCALE_REPLICATE).filter(image, null);
        ImageIO.write(image3, "JPG", new File("tmp", name.substring(0, name.lastIndexOf('.')) + "_" + "Replicate" + ".jpg"));
        ResampleOp filter = new ResampleOp(image.getWidth() / 2, image.getHeight() / 2);
        filter.setFilter(ResampleFilters.getLanczos3Filter());
        BufferedImage image4 = filter.filter(image, null);
        ImageIO.write(image4, "JPG", new File("tmp", name.substring(0, name.lastIndexOf('.')) + "_" + "Lanczos3" + ".jpg"));
    }
}
