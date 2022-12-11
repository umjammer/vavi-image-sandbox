/*
 * http://www.gamedev.net/community/forums/topic.asp?topic_id=392413
 */

package vavix.awt.image.pack.binpack;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * BinPacker.
 * 
 * @author jyk
 * @author <a href="mailto:umjammer@gmail.com">Naohide Sano</a> (umjammer)
 * @version 11 May 2006
 */
public class BinPacker {

    private int packSize;
    private int numPacked;
    private List<Rect> rects = new ArrayList<>();
    private List<Rect> packs = new ArrayList<>();
    private List<Integer> roots = new ArrayList<>();
    private List<List<Rect>> results = new ArrayList<>();

    /**
     * Packs rectangles.
     * 
     * @param rects An array containing the width and height of each input rect.
     *            The IDs for the rects are derived from the order in which they
     *            appear in the array.
     * @param packSize rectangle size to pack to.
     * @param allowRotation when true, the packer is allowed the option of
     *            rotating the rects in the process of trying to
     *            fit them into the current working area.
     * @return the array contains the packs (therefore
     *         the number of packs is packs.size()).
     */
    public List<List<Rect>> pack(final List<Dimension> rects, int packSize, boolean allowRotation) {

        this.packSize = packSize;

        // Add rects to member array, and check to make sure none is too big
        for (int i = 0; i < rects.size(); i++) {
            if (rects.get(i).width > this.packSize || rects.get(i).height > this.packSize) {
                assert false : "All rect dimensions must be <= the pack size";
            }
            this.rects.add(new Rect(0, 0, rects.get(i).width, rects.get(i).height, i));
        }

        // Sort from greatest to least area
        Collections.sort(this.rects);

        // Pack
        while (this.numPacked < this.rects.size()) {
            int i = this.packs.size();
            this.packs.add(new Rect(this.packSize));
            this.roots.add(i);
            fill(i, allowRotation);
        }

        // Write out
        for (Integer root : this.roots) {
            List<Rect> array = new ArrayList<>();
            addPackToArray(root, array);
            results.add(array);
        }

        // Check and make sure all rects were packed
        for (Rect rect : this.rects) {
            if (!rect.packed) {
                assert false : "Not all rects were packed";
            }
        }

        return results;
    }

    /** */
    public static class Rect extends Rectangle implements Comparable<Rect>, Cloneable {

        public int id;
        private int[] children = new int[2];
        public boolean rotated;
        private boolean packed;

        /** */
        private Rect(int size) {
            this.x = 0;
            this.y = 0;
            this.width = size;
            this.height = size;
            this.id = -1;
            this.rotated = false;
            this.packed = false;

            children[0] = -1;
            children[1] = -1;
//System.err.println("new rect: " + this);
        }

        /** */
        private Rect(int x, int y, int w, int h, int id) {
            this.x = x;
            this.y = y;
            this.width = w;
            this.height = h;
            this.id = id;
            this.rotated = false;
            this.packed = false;

            children[0] = -1;
            children[1] = -1;
//System.err.println("new rect: " + this);
        }

        private final int getArea() {
            return width * height;
        }

        private void rotate() {
            int t = width;
            width = height;
            height = t;
            rotated = !rotated;
        }

        /* reverse order */
        @Override
        public int compareTo(Rect o) {
            return o.getArea() - getArea();
        }

        public String toString() {
            return String.format("%d: %d, %d - %d, %d: %b, %b, %d, %d",
                                 id,
                                 x,
                                 y,
                                 width,
                                 height,
                                 rotated,
                                 packed,
                                 children[0],
                                 children[1]);
        }

        public Object clone() {
            Rect rect = new Rect(x, y, width, height, id);
            rect.rotated = false;
            rect.packed = false;
            rect.children[0] = children[0];
            rect.children[1] = children[1];
            return rect;
        }
    }

    private void fill(int pack, boolean allowRotation) {
        assert isPackValid(pack);

        int i = pack;

        // For each rect
        for (int j = 0; j < rects.size(); j++) {
            // If it's not already packed
            if (!rects.get(j).packed) {
                // If it fits in the current working area
                if (fits(rects.get(j), packs.get(i), allowRotation)) {
                    // Store in lower-left of working area, split, and recurse
                    numPacked++;
                    split(i, j);
                    fill(packs.get(i).children[0], allowRotation);
                    fill(packs.get(i).children[1], allowRotation);
                    return;
                }
            }
        }
    }

    /**
     * Split the working area either horizontally or vertically with respect
     * to the rect we're storing, such that we get the largest possible
     * child area.
     */
    private void split(int pack, int rect) {
        assert isPackValid(pack);
        assert isRectValid(rect);

        int i = pack;
        int j = rect;

        Rect left = (Rect) this.packs.get(i).clone();
        Rect right = (Rect) this.packs.get(i).clone();
        Rect bottom = (Rect) this.packs.get(i).clone();
        Rect top = (Rect) this.packs.get(i).clone();

        left.y += this.rects.get(j).height;
        left.width = this.rects.get(j).width;
        left.height -= this.rects.get(j).height;
        right.x += this.rects.get(j).width;
        right.width -= this.rects.get(j).width;

        bottom.x += this.rects.get(j).width;
        bottom.height = this.rects.get(j).height;
        bottom.width -= this.rects.get(j).width;
        top.y += this.rects.get(j).height;
        top.height -= this.rects.get(j).height;

        int maxLeftRightArea = left.getArea();
        if (right.getArea() > maxLeftRightArea) {
            maxLeftRightArea = right.getArea();
        }

        int maxBottomTopArea = bottom.getArea();
        if (top.getArea() > maxBottomTopArea) {
            maxBottomTopArea = top.getArea();
        }

        if (maxLeftRightArea > maxBottomTopArea) {
            if (left.getArea() > right.getArea()) {
                packs.add(left);
                packs.add(right);
            } else {
                packs.add(right);
                packs.add(left);
            }
        } else {
            if (bottom.getArea() > top.getArea()) {
                packs.add(bottom);
                packs.add(top);
            } else {
                packs.add(top);
                packs.add(bottom);
            }
        }

        // This pack area now represents the rect we've just stored, so save the
        // relevant info to it, and assign children.
        packs.get(i).width = rects.get(j).width;
        packs.get(i).height = rects.get(j).height;
        packs.get(i).id = rects.get(j).id;
        packs.get(i).rotated = rects.get(j).rotated;
        packs.get(i).children[0] = packs.size() - 2;
        packs.get(i).children[1] = packs.size() - 1;

        // Done with the rect
        rects.get(j).packed = true;
    }

    /**
     * Check to see if rect1 fits in rect2, and rotate rect1 if that will
     * enable it to fit.
     */
    private boolean fits(Rect rect1, final Rect rect2, boolean allowRotation) {
        if (rect1.width <= rect2.width && rect1.height <= rect2.height) {
            return true;
        } else if (allowRotation && rect1.height <= rect2.width && rect1.width <= rect2.height) {
            rect1.rotate();
            return true;
        } else {
            return false;
        }
    }

    private final void addPackToArray(int pack, List<Rect> array) {
        assert isPackValid(pack);

        int i = pack;
        if (packs.get(i).id != -1) {
            array.add(packs.get(i));

            if (packs.get(i).children[0] != -1) {
                addPackToArray(packs.get(i).children[0], array);
            }
            if (packs.get(i).children[1] != -1) {
                addPackToArray(packs.get(i).children[1], array);
            }
        }
    }

    private final boolean isRectValid(int i) {
        return i >= 0 && i < this.rects.size();
    }

    private final boolean isPackValid(int i) {
        return i >= 0 && i < this.packs.size();
    }
}
