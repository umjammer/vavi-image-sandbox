import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.swing.JFrame;


class GLImaging {

    //////////////// Variables /////////////////////////

    final String defaultImageFilename = "pics/duke_wave.gif";

    final String defaultFrameImageFilename = "pics/frame.png";

    // Databuffer that holds the loaded image.
    byte[] imgRGBA = null; // This is for the old JOGL version.

    ByteBuffer imgRGBABuf; // For JOGL version higher 1.1.
    // Image size retrieved durung loading,
    // re-used when image is drawn.

    int imgHeight;

    int imgWidth;

    // To copy the content of the current frame.
    int frameWidth;

    int frameHeight;

    ///////////////// Functions /////////////////////////

    public GLImaging() {
    }

    public void init(int width, int height) {
        frameWidth = width;
        frameHeight = height;

        File outputFile = new File(defaultFrameImageFilename);
        if (outputFile.exists()) {
            outputFile.delete();
        }
    }

    ///////////// load Image ////////////////////////////////////

    // Format: int[] of pixels from a Java
    // image with BufferedImage.getRGB()
    // returns data in Java's default ARGB format, meaning
    // the 32-bit ints are packed with 8 bits each of alpha
    // mask (translucency): red, green, and blue, in that
    // order. GL does not define a constant for this color
    // model.
    //
    // Moreover, the use of int is potentially hazardous on
    // machines with "little-endian" architectures, such as
    // Intel x86 CPUs. In the "little-endian" world, the least
    // significant 16 bits of a 32-bit integer come first,
    // followed by the most significant 16 bits. It is not a
    // problem when we are entirely in Java, but when we pass
    // such an array from Java to the native OpenGL, the
    // endianness can turn what we thought was ARGB into GBAR,
    // meaning our colors and transparency get scrambled.
    //
    // One more problem: OpenGL puts (0,0) at the bottom
    // left. Java images have (0,0) at the upper left, what
    // makes the image look upside down in the OpenGL world.
    //
    // To provide glDrawPixels() with a suitable array,
    // we need to do the following:
    // 1. Convert to a color model that OpenGL understands.
    // 2. Use a byte array to keep the color values
    //    properly arranged.
    // 3. Flip the image vertically so that the row order
    //    puts the bottom of the image on the first row.
    //
    // to flip the image, we define an AffineTransform that
    // moves the image into negative y coefficients, then
    // scales the pixels into a reverse ordering by
    // multiplying their y coefficients by -1, which also
    // moves them back into positive values. This
    // transformation is applied to the BufferedImage
    // offscreen Graphics2D, and the image is drawn into the
    // Graphics2D, picking up the transformation in the
    // process.
    //
    // Objects in the Processing chain:
    // img -> bufImg(raster, colorModel) -> imgBuf -> imgRGBA
    //
    public void loadImage(String filename) {
        // Load image and get height and width for raster.
        //
        if (filename == null) {
            filename = defaultImageFilename;
        }
        Image img = Toolkit.getDefaultToolkit().createImage(filename);
        MediaTracker tracker = new MediaTracker(new Canvas());
        tracker.addImage(img, 0);
        try {
            //Starts loading all images tracked by
            // this media tracker (wait max para ms).
            tracker.waitForAll(1000);
        } catch (InterruptedException ie) {
            System.out.println("MediaTracker Exception");
        }

        imgHeight = img.getHeight(null);
        imgWidth = img.getWidth(null);
        //System.out.println( "Image, width=" + imgWidth +
        //", height=" + imgHeight ); //ddd

        // Create a raster with correct size,
        // and a colorModel and finally a bufImg.
        //
        WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, imgWidth, imgHeight, 4, null);
        ComponentColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                                                                 new int[] {
                                                                     8, 8, 8, 8
                                                                 },
                                                                 true,
                                                                 false,
                                                                 ComponentColorModel.TRANSLUCENT,
                                                                 DataBuffer.TYPE_BYTE);
        BufferedImage bufImg = new BufferedImage(colorModel, // color model
                                                 raster,
                                                 false, // isRasterPremultiplied
                                                 null); // properties

        // Filter img into bufImg and perform
        // Coordinate Transformations on the way.
        //
        Graphics2D g = bufImg.createGraphics();
        AffineTransform gt = new AffineTransform();
        gt.translate(0, imgHeight);
        gt.scale(1, -1d);
        g.transform(gt);
        g.drawImage(img, null, null);
        // Retrieve underlying byte array (imgBuf)
        // from bufImg.
        DataBufferByte imgBuf = (DataBufferByte) raster.getDataBuffer();
        imgRGBA = imgBuf.getData();
        //System.out.println( "Image length=" + imgRGBA.length );//ddd
        if (imgRGBA == null) {
            System.out.println("ERROR: Could not load image.");
            return;
        }
        // Put image into the ByteBuffer for the new JOGL version.
        imgRGBABuf = ByteBuffer.allocateDirect(imgRGBA.length);
        imgRGBABuf.put(imgRGBA);
        //System.out.println( "imgRGBABuf=" + imgRGBABuf.capacity()+
        //          " / "+ imgRGBABuf.limit() ); //ddd
        g.dispose();
    }

    ///////////// save Image ///////////////////////////////

    private ByteBuffer getFrameData(GL gl, ByteBuffer pixelsRGB) {
        // Read Frame back into our ByteBuffer.
        gl.glReadBuffer(GL.GL_BACK);
        gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 1);
        gl.glReadPixels(0, 0, frameWidth, frameHeight, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, pixelsRGB);

        return pixelsRGB;
    }

    private BufferedImage copyFrame(GL gl) {
        // Create a ByteBuffer to hold the frame data.
        java.nio.ByteBuffer pixelsRGB =
                //BufferUtils.newByteBuffer
                ByteBuffer.allocateDirect(frameWidth * frameHeight * 3);

        // Get date from frame as ByteBuffer.
        getFrameData(gl, pixelsRGB);

        return transformPixelsRGBBuffer2ARGB_ByHand(pixelsRGB);
    }

    // Copies the Frame to an integer array.
    // Do the necessary conversion by hand.
    //
    private BufferedImage transformPixelsRGBBuffer2ARGB_ByHand(ByteBuffer pixelsRGB) {
        // Transform the ByteBuffer and get it as pixeldata.

        int[] pixelInts = new int[frameWidth * frameHeight];

        // Convert RGB bytes to ARGB ints with no transparency.
        // Flip image vertically by reading the
        // rows of pixels in the byte buffer in reverse
        // - (0,0) is at bottom left in OpenGL.
        //
        // Points to first byte (red) in each row.
        int p = frameWidth * frameHeight * 3;
        int q; // Index into ByteBuffer
        int i = 0; // Index into target int[]
        int w3 = frameWidth * 3; // Number of bytes in each row
        for (int row = 0; row < frameHeight; row++) {
            p -= w3;
            q = p;
            for (int col = 0; col < frameWidth; col++) {
                int iR = pixelsRGB.get(q++);
                int iG = pixelsRGB.get(q++);
                int iB = pixelsRGB.get(q++);
                pixelInts[i++] = 0xFF000000 | ((iR & 0x000000FF) << 16) | ((iG & 0x000000FF) << 8) | (iB & 0x000000FF);
            }
        }

        // Create a new BufferedImage from the pixeldata.
        BufferedImage bufferedImage = new BufferedImage(frameWidth, frameHeight, BufferedImage.TYPE_INT_ARGB);
        bufferedImage.setRGB(0, 0, frameWidth, frameHeight, pixelInts, 0, frameWidth);

        return bufferedImage;
    }

    // Function returns if filename already exsits.
    // In this way it does not save each frame, when
    // calles from the display() function.
    public void saveFrameAsPNG(GL gl, String fileName) {
        // Open File
        if (fileName == null) {
            fileName = defaultFrameImageFilename;
        }
        File outputFile = new File(fileName);
        // Do not overwrite existing image file.
        if (outputFile.exists()) {
            return;
        }

        // Write file.
        try {
            javax.imageio.ImageIO.write(copyFrame(gl), "PNG", outputFile);
        } catch (IOException e) {
            System.out.println("Error: ImageIO.write.");
            e.printStackTrace();
        }
    }

    ///////////// draw /////////////////////////////////////

    public void draw(GL gl) {
        // Load image, if necessary.
        if (imgRGBA == null) {
            loadImage(defaultImageFilename);
        }

        gl.glPushAttrib(GL.GL_DEPTH_BUFFER_BIT);
        gl.glPushAttrib(GL.GL_COLOR_BUFFER_BIT);
        {

            gl.glDisable(GL.GL_DEPTH_TEST);

            // enable alpha mask (import from gif sets alpha bits)
            gl.glEnable(GL.GL_BLEND);
            gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

            // Draw a rectangle under part of image
            // to prove alpha works.
            gl.glColor4f(.5f, 0.1f, 0.2f, .5f);
            gl.glRecti(0, 0, 100, 330);
            gl.glColor3f(0.0f, 0.0f, 0.0f);

            // Draw image as bytes.
            // gl.glRasterPos2i( 150, 100 );
            gl.glWindowPos2i(600, 600);
            gl.glPixelZoom(1.0f, 1.0f); // x-factor, y-factor
            gl.glDrawPixels(imgWidth, imgHeight, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, imgRGBABuf.rewind());
            //gl.glPixelZoom( -2.0f, 3.0f ); // x-factor, y-factor
            gl.glWindowPos2i(600, 300);
            gl.glDrawPixels(imgWidth, imgHeight, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, imgRGBABuf.rewind());

//     // Draw a rectangle under part of image
//     // to prove alpha works.
//      gl.glColor4f( .5f, 0.1f, 0.2f, .5f );
//      gl.glRecti( 0, 0, 100, 330 );

            // Copy the Image: FrameBuf to FrameBuf
            gl.glPixelZoom(2.0f, 2.0f); // x-factor, y-factor
            gl.glWindowPos2i(500, 0);
            gl.glCopyPixels(400, 300, 500, 600, GL.GL_COLOR);

        }
        gl.glPopAttrib();
        gl.glPopAttrib();
    }

    public static void main(String[] args) throws Exception {
        final GLImaging a = new GLImaging();
        a.init(400, 400);
        a.loadImage(args[0]);

        JFrame myframe = new JFrame();
        myframe.setLocationRelativeTo(null);
        myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GLCapabilities caps = new GLCapabilities();
        GLJPanel mycanvas = new GLJPanel(caps);

        mycanvas.addGLEventListener(new GLEventListener() {
            @Override
            public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
                GL gl = drawable.getGL();

                // at reshape we're going to tell OPENGL that we'd like to
                // treat the screen on a pixel by pixel basis by telling
                // it to use Orthographic projection.
                gl.glMatrixMode(GL.GL_PROJECTION);
                gl.glLoadIdentity();

                gl.glOrtho(0, width, height, 0, -1, 1);
            }

            @Override
            public void init(GLAutoDrawable drawable) {
            }

            @Override
            public void displayChanged(GLAutoDrawable drawable, boolean arg1, boolean arg2) {
            }

            @Override
            public void display(GLAutoDrawable drawable) {
                a.draw(drawable.getGL());
            }
        });

        myframe.setSize(400, 400);
        myframe.setContentPane(mycanvas);

        myframe.setVisible(true);
    }
}
