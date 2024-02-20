/*
 * Copyright (c) 2022 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;


/**
 * IdeTest.
 *
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (nsano)
 * @version 0.00 2022-09-09 nsano initial version <br>
 */
@EnabledIfSystemProperty(named = "vavi.test", matches = "ide")
class IdeTest {

    /** using cdl cause junit stops awt thread suddenly */
    CountDownLatch cdl;

    @BeforeEach
    void setup() {
        cdl = new CountDownLatch(1);
    }

    @Test
    void run_Scaling_awt_hexe() throws Exception {
        Scaling_awt_hexe.main(new String[] {"src/test/resources/erika.jpg"});
    }

    @Test
    void run_Scaling_awt_mortennobel() throws Exception {
        Scaling_awt_mortennobel.main(new String[] {"src/test/resources/erika.jpg"});
    }

    @Test
    void run_Scaling_awt_zhoumx() throws Exception {
        Scaling_awt_zhoumx.main(new String[] {"rc/test/resources/erika.jpg"});
    }

    @Test
    void run_RollingArtwork() throws Exception {
        RollingArtwork.main(new String[] {"src/test/resources/erika.jpg"});
    }

    @Test
    void run_GLImaging() throws Exception {
        GLImaging.main(new String[] {"src/test/resources/erika.jpg"});
    }

    @AfterEach
    void teardown() throws Exception {
        cdl.await(); // depends on each test frame's exit on close
    }
}
