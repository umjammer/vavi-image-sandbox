package vavi.test.opengl;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;
import com.sun.opengl.util.GLUT;


public class MyFrame2  extends JFrame implements GLEventListener {
    GLU glu;
    GLUT glut;

    GLCapabilities caps = new GLCapabilities();
    GLJPanel mycanvas   = new GLJPanel(caps);

    String TitleString;
    double[] eye = {40.0, 10.0, 20.0};

    float[] light_ambient = { 0.2f, 0.2f, 0.2f, 1.0f},
          light_diffuse = { 1.0f, 1.0f, 1.0f, 1.0f},
          light_specular = { 1.0f, 1.0f, 1.0f, 1.0f};

    float[] emerald_ambient = {0.0215f,  0.1745f,   0.0215f,  1.0f},
          emerald_diffuse = {0.07568f, 0.61424f,  0.07568f, 1.0f},
          emerald_specular = {0.633f,   0.727811f, 0.633f,   1.0f},
          emerald_shininess = {76.8f};

    public MyFrame2(String title){
        mycanvas.addGLEventListener(this);

        TitleString = title;
        setSize(400,400);
        setContentPane(mycanvas);

    }

    public static void main(String[] args) {
        // TODO 自動生成されたメソッド・スタブ
        MyFrame2 myframe = new MyFrame2("材質カラー");
        myframe.setLocationRelativeTo(null);
        myframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myframe.setVisible(true);
    }

    public void init(GLAutoDrawable drawable){
        GL gl = drawable.getGL();
        glu  = new GLU();
        glut = new GLUT();
        //
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        float[] light_position0 = { 1,0f, 1.0f, 1.0f, 0.0f};
        float[] light_position1 = {-1.0f, 1.0f, 1.0f, 0.0f};

        gl.glLightfv(GL.GL_LIGHT0, GL.GL_POSITION, light_position0, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_POSITION, light_position1, 0);

        gl.glEnable(GL.GL_LIGHTING);
        gl.glEnable(GL.GL_LIGHT0);
        gl.glEnable(GL.GL_LIGHT1);
        gl.glEnable(GL.GL_DEPTH_TEST);
    }
    public void display(GLAutoDrawable drawable){
        GL gl = drawable.getGL();
        //
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        //gl.glColor3f(1.0f, 1.0f, 1.0f);
        gl.glLoadIdentity();

        glu.gluLookAt(eye[0], eye[1], eye[2], 0.0, 0.0, 0.0, 0.0, 1.0, 0.0);

        gl.glLightfv(GL.GL_LIGHT0, GL.GL_AMBIENT, light_ambient, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_DIFFUSE, light_diffuse, 0);
        gl.glLightfv(GL.GL_LIGHT0, GL.GL_SPECULAR,light_specular, 0);

        gl.glLightfv(GL.GL_LIGHT1, GL.GL_AMBIENT, light_ambient, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_DIFFUSE, light_diffuse, 0);
        gl.glLightfv(GL.GL_LIGHT1, GL.GL_SPECULAR,light_specular, 0);

        gl.glMaterialfv(GL.GL_FRONT, GL.GL_AMBIENT,   emerald_ambient, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_DIFFUSE,   emerald_diffuse, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SPECULAR,  emerald_specular, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL.GL_SHININESS, emerald_shininess, 0);

        glut.glutSolidDodecahedron();
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
        glu.gluPerspective(10.0, (double)w/(double)h, 1.0, 100.0);
        gl.glMatrixMode(GL.GL_MODELVIEW);
    }
}
