package vavi.test.opengl;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.Hashtable;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.swing.JFrame;


@SuppressWarnings("restriction")
public class MyFrame3  extends JFrame implements GLEventListener {

    int width, height;

    public MyFrame3(String title){
        GLCapabilities caps = new GLCapabilities();
        GLJPanel mycanvas   = new GLJPanel(caps);

        mycanvas.addGLEventListener(this);

        width = 400;
        height = 400;
        setSize(width, height);
        setContentPane(mycanvas);
     }

    public static void main(String[] args) throws Exception {
        bufferedImage = ImageIO.read(new File(args[0]));

        MyFrame3 myframe = new MyFrame3("texture 2d");
        myframe.setLocationRelativeTo(null);
        myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myframe.setVisible(true);
    }

    static BufferedImage bufferedImage;

    public void init(GLAutoDrawable drawable){
        GL gl = drawable.getGL();

        // enable textures since we're going to use these for our sprites
        gl.glEnable(GL.GL_TEXTURE_2D);

        // set the background colour of the display to black
        gl.glClearColor(0, 0, 0, 0);
        // set the area being rendered
        gl.glViewport(0, 0, width, height);
        // disable the OpenGL depth test since we're rendering 2D graphics
        gl.glDisable(GL.GL_DEPTH_TEST);

        IntBuffer tmp = IntBuffer.allocate(1);
        gl.glGenTextures(1, tmp);
        textureID = tmp.get(0);

        gl.glBindTexture(GL.GL_TEXTURE_2D, textureID);

        // produce a texture from the byte buffer
        gl.glTexImage2D(GL.GL_TEXTURE_2D,
                      0,
                      GL.GL_RGBA,
                      bufferedImage.getWidth(),
                      bufferedImage.getHeight(),
                      0,
                      GL.GL_RGB,
                      GL.GL_BYTE,
                      getImageBuffer());
    }

    ColorModel glAlphaColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                                                new int[] {8,8,8,8},
                                                true,
                                                false,
                                                ComponentColorModel.TRANSLUCENT,
                                                DataBuffer.TYPE_BYTE);

    ColorModel glColorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB),
                                                new int[] {8,8,8,0},
                                                false,
                                                false,
                                                ComponentColorModel.OPAQUE,
                                                DataBuffer.TYPE_BYTE);

    private Buffer getImageBuffer() {
//        return IntBuffer.wrap(bufferedImage.getRaster().getPixels(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), (int[]) null));

        ByteBuffer imageBuffer = null;
        WritableRaster raster;
        BufferedImage texImage;

        int texWidth = 2;
        int texHeight = 2;

        // find the closest power of 2 for the width and height
        // of the produced texture
        while (texWidth < bufferedImage.getWidth()) {
            texWidth *= 2;
        }
        while (texHeight < bufferedImage.getHeight()) {
            texHeight *= 2;
        }

        // create a raster that can be used by OpenGL as a source
        // for a texture
        if (bufferedImage.getColorModel().hasAlpha()) {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 4, null);
            texImage = new BufferedImage(glAlphaColorModel, raster, false, new Hashtable<>());
        } else {
            raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE, texWidth, texHeight, 3, null);
            texImage = new BufferedImage(glColorModel, raster, false, new Hashtable<>());
        }

        // copy the source image into the produced image
        Graphics g = texImage.getGraphics();
        g.setColor(new Color(0f,0f,0f,0f));
        g.fillRect(0,0,texWidth,texHeight);
        g.drawImage(bufferedImage,0,0,null);

        // build a byte buffer from the temporary image
        // that be used by OpenGL to produce a texture.
        byte[] data = ((DataBufferByte) texImage.getRaster().getDataBuffer()).getData();

        imageBuffer = ByteBuffer.allocateDirect(data.length);
        imageBuffer.order(ByteOrder.nativeOrder());
        imageBuffer.put(data, 0, data.length);
        imageBuffer.flip();

        return imageBuffer;
    }

    int textureID;

    public void display(GLAutoDrawable drawable){
        GL gl = drawable.getGL();

        // clear the screen and setup for rendering
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        gl.glMatrixMode(GL.GL_MODELVIEW);
        gl.glLoadIdentity();

        gl.glPushMatrix();

        // bind to the appropriate texture for this sprite
        gl.glBindTexture(GL.GL_TEXTURE_2D, textureID);

        // translate to the right location and prepare to draw
        gl.glTranslatef(0, 0, 0);
        gl.glColor3f(1, 1, 1);

        // draw a quad textured to match the sprite
        gl.glBegin(GL.GL_QUADS);
        {
            gl.glTexCoord2f(0, 0);
            gl.glVertex2f(0, 0);
            gl.glTexCoord2f(0, bufferedImage.getHeight());
            gl.glVertex2f(0, height);
            gl.glTexCoord2f(bufferedImage.getWidth(), bufferedImage.getHeight());
            gl.glVertex2f(width,height);
            gl.glTexCoord2f(bufferedImage.getWidth(), 0);
            gl.glVertex2f(width,0);
        }
        gl.glEnd();

        // restore the model view matrix to prevent contamination
        gl.glPopMatrix();

        // flush the graphics commands to the card
        gl.glFlush();
    }

    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged){

    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
        GL gl = drawable.getGL();

        // at reshape we're going to tell OPENGL that we'd like to
        // treat the screen on a pixel by pixel basis by telling
        // it to use Orthographic projection.
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();

        gl.glOrtho(0, width, height, 0, -1, 1);
    }
}
