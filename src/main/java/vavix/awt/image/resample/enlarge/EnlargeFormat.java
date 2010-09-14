/*
 * SmillaEnlarger  -  resize, especially magnify bitmaps in high quality
 *
 * Copyright (C) 2009 Mischa Lusteck
 * 
 * This program is free software;
 * you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation;
 * either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */

package vavix.awt.image.resample.enlarge;


/**
 * EnlargeFormat. 
 *
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 2010/09/14 nsano initial version <br>
 */
public class EnlargeFormat {
    public int srcWidth;
    public int srcHeight;
    public float scaleX;
    public float scaleY;
    public int clipX0, clipY0;
    public int clipX1, clipY1;

    public void setSourceSize(int w, int h) {
        srcWidth = w;
        srcHeight = h;
    }

    public void setScaleFact(float f) {
        scaleX = scaleY = f;
        setFullClip();
    }

    public void setScaleFact(float fx, float fy) {
        scaleX = fx;
        scaleY = fy;
        setFullClip();
    }

    public int dstWidth() {
        return (int) ((srcWidth) * scaleX + 0.5);
    }

    public int dstHeight() {
        return (int) ((srcHeight) * scaleY + 0.5);
    }

    public void setDstClip(int cx0, int cy0, int cx1, int cy1) {
        clipX0 = cx0;
        clipY0 = cy0;
        clipX1 = cx1;
        clipY1 = cy1;
    }

    public void setSrcClip(float sx0, float sy0, float sx1, float sy1) {
        clipX0 = (int) (scaleX * sx0);
        clipY0 = (int) (scaleY * sy0);
        clipX1 = clipX0 + (int) ((sx1 - sx0) * scaleX + 0.5);
        clipY1 = clipY0 + (int) ((sy1 - sy0) * scaleY + 0.5);
    }

    public void getSrcClip(float[] sx0, float[] sy0, float[] sx1, float[] sy1) {
        sx0[0] = clipX0 / scaleX;
        sy0[0] = clipY0 / scaleY;
        sx1[0] = clipX1 / scaleX;
        sy1[0] = clipY1 / scaleY;
    }

    public void setFullClip() {
        clipX0 = 0;
        clipY0 = 0;
        clipX1 = dstWidth();
        clipY1 = dstHeight();
    }

    public int clipW() {
        return clipX1 - clipX0;
    }

    public int clipH() {
        return clipY1 - clipY0;
    }
}