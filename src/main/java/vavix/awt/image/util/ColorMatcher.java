package vavix.awt.image.util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.lang.reflect.Field;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vavi.util.StringUtil;


/**
 * ColorMatcher.
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 041007 nsano initial version <br>
 */
public class ColorMatcher {
    
    class Rgb {
        int r;
        int g;
        int b;
        /**
         * @param r
         * @param g
         * @param b
         */
        public Rgb(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }
        /**
         * 
         */
        public Rgb() {
            this.r = 0;
            this.g = 0;
            this.b = 0;
        }
        /**
         * @param rgb
         * @param value
         */
        public void set(int rgb, int value) {
            switch (rgb) {
            case 'r':
            case 'R':
                this.r = value;
                break;
            case 'g':
            case 'G':
                this.g = value;
                break;
            case 'b':
            case 'B':
                this.g = value;
                break;
            default:
            }
        }
    }
    
    class Hs {
        int h;
        int s;
        int v;
    }
    
    int move[] = new int[4];
    private static final int R = 0;
    private static final int G = 1;
    private static final int B = 2;
    private static final int H = 3;
    
    Hs hs = new Hs();
    Rgb rgb;
    
    private int window_event_offsetX;
    private Rgb sw_style_backgroundColor;
    /** rgb(r,g,b) */
    private String window_event_srcElement_style_backgroundColor;
    private int[] h_style_pixelLeft;
    
    /** */
    private void click(int x, int s) {
        if (x < 10) {
            x = 10;
        }
        if (x > 265) {
            x = 265;
        }
        x -= 10;
//        h[s].style.left = x + 1;
        rgb.set(s, x);
        rg2hs(rgb);
        ud("0", rgb);
        sw_style_backgroundColor = new Rgb(rgb.r, rgb.g, rgb.b);
        dom();
    }
    
    /** */
    private void bclick(int s) {
        int x = window_event_offsetX + h_style_pixelLeft[s] - 1;
        click(x, s);
    }
    
    /** */
    private void sc(int s) {
        int x = window_event_offsetX;
        click(x, s);
    }
    
    /** */
    private void movee(int s) {
        move[s] = 0;
    }
    
    /** */
    private int rc(int x, int m) {
        if (x > m) {
            return m;
        }
        if (x < 0) {
            return 0;
        } else {
            return x;
        }
    }
    
    /** */
    private Hs rg2hs(Rgb rg) {
        int m = rg.r;
        if (rg.g < m) {
            m = rg.g;
        }
        if (rg.b < m) {
            m = rg.b;
        }
        int v = rg.r;
        if (rg.g > v) {
            v = rg.g;
        }
        if (rg.b > v) {
            v = rg.b;
        }
        double value = 100 * v / 255;
        double delta = v - m;
        
        if (v == 0.0) {
            hs.s = 0;
        } else {
            hs.s = (int) (100 * delta / v);
        }
        
        if (hs.s == 0) {
            hs.h = 0;
        } else {
            if (rg.r == v) {
                hs.h = (int) (60.0 * (rg.g - rg.b) / delta);
            } else if (rg.g == v) {
                hs.h = (int) (120.0 + 60.0 * (rg.b - rg.r) / delta);
            } else if (rg.b == v) {
                hs.h = (int) (240.0 + 60.0 * (rg.r - rg.g) / delta);
            }
            
            if (hs.h < 0.0) {
                hs.h = (int) (hs.h + 360.0);
            }
        }
        
        hs.v = (int) Math.round(value);
        hs.h = Math.round(hs.h);
        hs.s = Math.round(hs.s);
        
        return hs;
    }
    
    /** */
    private String rg2html(Rgb z) {
        return "#" + StringUtil.toHex2(z.r) + StringUtil.toHex2(z.g) + StringUtil.toHex2(z.b);
    }
    
    /** */
    private void c2r(int d) {
        String k = window_event_srcElement_style_backgroundColor;
        String[] j = (k.substring(4, k.indexOf(')') - 4)).split(",");
        click(Integer.parseInt(new String(j[0])) + 10, R);
        click(Integer.parseInt(j[1]) + 10, G);
        click(Integer.parseInt(j[2]) + 10, B);
    }
    
    /** */
    private Rgb h2r(Hs hs) {
        Rgb rg = new Rgb();
        if (hs.s == 0) {
            rg.r = rg.g = rg.b = (int) Math.round(hs.v * 2.55);
            return rg;
        }
        hs.s = hs.s / 100;
        hs.v = hs.v / 100;
        hs.h /= 60;
        int i = (int) Math.floor(hs.h);
        int f = hs.h - i;
        int p = hs.v * (1 - hs.s);
        int q = hs.v * (1 - hs.s * f);
        int t = hs.v * (1 - hs.s * (1 - f));
        switch (i) {
        case 0:
            rg.r = hs.v;
            rg.g = t;
            rg.b = p;
            break;
        case 1:
            rg.r = q;
            rg.g = hs.v;
            rg.b = p;
            break;
        case 2:
            rg.r = p;
            rg.g = hs.v;
            rg.b = t;
            break;
        case 3:
            rg.r = p;
            rg.g = q;
            rg.b = hs.v;
            break;
        case 4:
            rg.r = t;
            rg.g = p;
            rg.b = hs.v;
            break;
        default:
            rg.r = hs.v;
            rg.g = p;
            rg.b = q;
        }
        rg.r = Math.round(rg.r * 255);
        rg.g = Math.round(rg.g * 255);
        rg.b = Math.round(rg.b * 255);
        
        return rg;
    }
    
    /** */
    private void ps(int x) {
        System.out.println(
                   "<td>" +
                   "<div style=\"width:53;height:53;background-color:rgb(0,0,0);cursor:hand\" " +
                     "class=s " +
                     "id=\"sw" + x + "\" " +
                     "onClick=\"c2r()\" " +
                     "title=\"Click to promote to primary color\">" +
                     "                                         </div>" +
                     "                                         </td>                                         ");
    }
    
    /** */
    private void ph(int x) {
        System.out.println(
                    "<td>" +
                     "<div class=t id=\"hc" + x + "\">" +
                     "                                         #000000" +
                     "</div>" +
                     "                                         </td>                                         ");
    }
    
    /**
     * @param x "0", "1"
     */
    private void ud(String x, Rgb c) {
        try {
            eval("sw" + x + "style.backgroundColor").set(this, "rgb(" + c.r + "," + c.g + "," + c.b + ")");
            eval("hc" + x + "innerHTML").set(this, rg2html(c));
        } catch (Exception e) {
            throw (RuntimeException) new IllegalStateException().initCause(e);
        }
    }
    
    /**
     * @param string
     * @return field
     */
    private Field eval(String string) {
        try {
            return ColorMatcher.class.getField(string);
        } catch (Exception e) {
            throw (RuntimeException) new IllegalStateException().initCause(e);
        }
    }

    /** */
    private void pl(int t, int c, int l) {
        System.out.println(
                   "<div " +
                    "style=\"position:absolute;left:30;top:" + t + ";background-color:black\">" +
                   "<div " +
                    "class=s style=\"width:276;" +
                   "height:21;" +
                   "background-color:" + c + ";" +
                   "filter:alpha(style=1,startx=360,finishx=0);" +
                   "\" onMouseDown=\"move" + l + "=1;" +
                   "sc(\'" + l + "\');" +
                   "\" onMouseMove=\"if(move" + l + "==1){sc(\'' + l + '\');" +
               "}\">                                                                      </div>                                                                      <div class=s2 id=h" + l + " onMouseDown=\"move" + l + "=1;" +
                   "bclick(\'" + l + "\');" +
                   "\" onMouseUp=\"movee(\'" + l + "\');" +
                   "\" onMouseMove=\"if(move" + l + "==1){bclick(\'" + l + "\');" +
               "}\">                                                                      </div>                                                                      </div>                                                                      ");
    }
    
    /** */
    private void dom() {
        Rgb z = new Rgb();
        Hs y = new Hs();
        Hs yx = new Hs();

        y.s = hs.s;
        y.h = hs.h;
        if (hs.v > 70) {
            y.v = hs.v - 30;
        } else {
            y.v = hs.v + 30;
        }
        z = h2r(y);
        ud("1", z);

        if ((hs.h >= 0) && (hs.h < 30)) {
            yx.h = y.h = hs.h + 20;
            yx.s = y.s = hs.s;
            y.v = hs.v;
            if (hs.v > 70) {
                yx.v = hs.v - 30;
            } else {
                yx.v = hs.v + 30;
            }
        }
        if ((hs.h >= 30) && (hs.h < 60)) {
            yx.h = y.h = hs.h + 150;
            y.s = rc(hs.s - 30, 100);
            y.v = rc(hs.v - 20, 100);
            yx.s = rc(hs.s - 70, 100);
            yx.v = rc(hs.v + 20, 100);
        }
        if ((hs.h >= 60) && (hs.h < 180)) {
            yx.h = y.h = hs.h - 40;
            y.s = yx.s = hs.s;
            y.v = hs.v;
            if (hs.v > 70) {
                yx.v = hs.v - 30;
            } else {
                yx.v = hs.v + 30;
            }
        }
        if ((hs.h >= 180) && (hs.h < 220)) {
            yx.h = hs.h - 170;
            y.h = hs.h - 160;
            yx.s = y.s = hs.s;
            y.v = hs.v;
            if (hs.v > 70) {
                yx.v = hs.v - 30;
            } else {
                yx.v = hs.v + 30;
            }
        }
        if ((hs.h >= 220) && (hs.h < 300)) {
            yx.h = y.h = hs.h;
            yx.s = y.s = rc(hs.s - 60, 100);
            y.v = hs.v;
            if (hs.v > 70) {
                yx.v = hs.v - 30;
            } else {
                yx.v = hs.v + 30;
            }
        }
        if (hs.h >= 300) {
            if (hs.s > 50) {
                y.s = yx.s = hs.s - 40;
            } else {
                y.s = yx.s = hs.s + 40;
            }
            yx.h = y.h = (hs.h + 20) % 360;
            y.v = hs.v;
            if (hs.v > 70) {
                yx.v = hs.v - 30;
            } else {
                yx.v = hs.v + 30;
            }
        }

        z = h2r(y);
        ud("2", z);

        z = h2r(yx);
        ud("3", z);

        y.h = 0;
        y.s = 0;
        y.v = 100 - hs.v;
        z = h2r(y);
        ud("4", z);

        y.h = 0;
        y.s = 0;
        y.v = hs.v;
        z = h2r(y);
        ud("5", z);
    }

    /*
p {margin:0px;
   margin-bottom:4px;
   font:11px tahoma;
   line-height: 12px;
   }
td  {vertical-align:top;}
h1  {font-size:12px;
   font-weight:bold
   margin:0px;
   margin-bottom:8px;
   }

 .s{border:inset 1;width:50}
 .s2{border:outset 1;width:19;height:19;background-color:#E6E6E6;position:absolute;left:1;top:1}
 .t{font:11px tahoma;color:#555555}
 .t2{font:bold 36px verdana}
     */
    public static void main(String[] args) {
        ColorMatcher cm = new ColorMatcher();

        JPanel[] samples = new JPanel[6];
        for (int i = 0; i < samples.length; i++) {
            samples[i] = new JPanel();
            samples[i].setPreferredSize(new Dimension(80, 80));
            samples[i].setBorder(new LineBorder(Color.black, 1));
        }

        JPanel basePanel = new JPanel();
        basePanel.setPreferredSize(new Dimension(640, 400));
        basePanel.setBackground(new Color(0xe6, 0xe6, 0xe6));

        JPanel colorPanel = new JPanel();
        colorPanel.setPreferredSize(new Dimension(80, 80));
        colorPanel.setBorder(new LineBorder(Color.black, 1));

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));
        JSlider sliderR = new JSlider();
        sliderR.setPreferredSize(new Dimension(276, 21));
        sliderR.setMinimum(0);
        sliderR.setMinimum(255);
        sliderR.setValue(127);
        sliderR.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
//              "\" onMouseDown=\"move" + l + "=1;" +
//              "sc(\'" + l + "\');" +
//              "\" onMouseMove=\"if(move" + l + "==1){sc(\'' + l + '\');" +
//          "}\">                                                                      </div>                                                                      <div class=s2 id=h" + l + " onMouseDown=\"move" + l + "=1;" +
//              "bclick(\'" + l + "\');" +
//              "\" onMouseUp=\"movee(\'" + l + "\');" +
//              "\" onMouseMove=\"if(move" + l + "==1){bclick(\'" + l + "\');" +
            }
        });
        JSlider sliderG = new JSlider();
        sliderG.setPreferredSize(new Dimension(276, 21));
        sliderG.setMinimum(0);
        sliderG.setMinimum(255);
        sliderG.setValue(127);
        JSlider sliderB = new JSlider();
        sliderB.setPreferredSize(new Dimension(276, 21));
        sliderB.setMinimum(0);
        sliderB.setMinimum(255);
        sliderB.setValue(127);
        panel.add(sliderR);
        panel.add(sliderG);
        panel.add(sliderB);

        panel.add(colorPanel);

        basePanel.add(panel);

        panel = new JPanel();
        for (JPanel sample : samples) {
            panel.add(sample);
        }
        basePanel.add(panel);

        JFrame frame = new JFrame();
        frame.add(basePanel);
        frame.pack();
        frame.setVisible(true);
    }
}

/* */
