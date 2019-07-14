package vavi.test.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLJPanel;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import com.sun.opengl.util.GLUT;


public class MyFrame extends JFrame implements GLEventListener{
    GLU glu;
    GLUT glut;

    GLCapabilities caps = new GLCapabilities();
    GLJPanel mycanvas   = new GLJPanel(caps);

    public MyFrame(String title){
        mycanvas.addGLEventListener(this);

        setSize(200,200);
        setContentPane(mycanvas);

    }
    public static void main(String[] args) {
        // TODO 自動生成されたメソッド・スタブ
        MyFrame myframe = new MyFrame("テスト");
        myframe.setLocationRelativeTo(null);
        myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myframe.setVisible(true);
    }

    public void init(GLAutoDrawable drawable){
        GL gl = drawable.getGL();
        glu = new GLU();
        glut = new GLUT();
        //
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glShadeModel(GL.GL_FLAT);
    }
    public void display(GLAutoDrawable drawable){
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT);
        gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glLoadIdentity();

        glu.gluLookAt(0.0, 0.0, 5.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

        glut.glutWireCube(1.0f);
        gl.glFlush();
    }
    public void displayChanged(GLAutoDrawable drawable, boolean modeChanged,
            boolean deviceChanged){

    }
    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h){
        GL gl = drawable.getGL();
        //
        gl.glViewport(0, 0, w, h);
        gl.glMatrixMode(GL.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(20.0, (double)w/(double)h, 1.0, 20.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }
}
