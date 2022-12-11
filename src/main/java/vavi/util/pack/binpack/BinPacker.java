package vavi.util.pack.binpack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BinPacker {

    private int packSize;
    private int numPacked;
    private List<Rect> rects = new ArrayList<>();
    private List<Rect> packs = new ArrayList<>();
    private List<Integer> roots = new ArrayList<>();
    private List<List<Rect>> results = new ArrayList<>();

    /**
     * @param rects An array containing the width and height of each input rect
     *            in
     *            sequence. The IDs for the rects are
     *            derived from the order in which they appear in the array.
     * @param allowRotation when true (the default value), the packer is allowed
     *            the option of rotating the rects in the process of trying to
     *            fit them
     *            into the current working area.
     * @return the outer array contains the packs (therefore
     *         the number of packs is packs.size()).
     */
    public List<List<Rect>> pack(List<Dim> rects, int packSize, boolean allowRotation) {
        this.packSize = packSize;
        // Add rects to member array, and check to make sure none is too big
        for (int i = 0; i < rects.size(); i ++) {
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

    public static class Dim {
        public Dim(int width, int height) {
            this.width = width;
            this.height = height;
        }
        public int width;
        public int height;
    }

    public static class Rect implements Comparable<Rect> {
        public int x;
        public int y;
        public int w;
        public int h;
        public int id;
        private int[] children = new int[2];
        public boolean rotated;
        private boolean packed;

        private Rect(int size) {
            x = 0;
            y = 0;
            w = size;
            h = size;
            id = -1;
            rotated = false;
            packed = false;
            children[0] = -1;
            children[1] = -1;
        }

        private Rect(int x, int y, int w, int h, int id) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.id = id;
            rotated = false;
            packed = false;
            children[0] = -1;
            children[1] = -1;
        }

        private int getArea() {
            return w * h;
        }

        private void rotate() {
            int t = w;
            w = h;
            h = t;
            rotated = !rotated;
        }

        public int compareTo(Rect rect) {
            return rect.getArea() - getArea();
        }

        public Object clone() {
            Rect rect = new Rect(x, y, w, h, id);
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
        for (int j = 0; j < rects.size(); ++j) {
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

    private void split(int pack, int rect) {
        assert isPackValid(pack);
        assert isRectValid(rect);

        int i = pack;
        int j = rect;
        // Split the working area either horizontally or vertically with respect
        // to the rect we're storing, such that we get the largest possible child
        // area.
        Rect left = (Rect) packs.get(i).clone();
        Rect right = (Rect) packs.get(i).clone();
        Rect bottom = (Rect) packs.get(i).clone();
        Rect top = (Rect) packs.get(i).clone();
        left.y += rects.get(j).h;
        left.w = rects.get(j).w;
        left.h -= rects.get(j).h;
        right.x += rects.get(j).w;
        right.w -= rects.get(j).w;

        bottom.x += rects.get(j).w;
        bottom.h = rects.get(j).h;
        bottom.w -= rects.get(j).w;
        top.y += rects.get(j).h;
        top.h -= rects.get(j).h;

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
        packs.get(i).w = rects.get(j).w;
        packs.get(i).h = rects.get(j).h;
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
    private boolean fits(Rect rect1, Rect rect2, boolean allowRotation) {
        if (rect1.w <= rect2.w && rect1.h <= rect2.h) {
            return true;
        } else if (allowRotation && rect1.h <= rect2.w && rect1.w <= rect2.h) {
            rect1.rotate();
            return true;
        } else {
            return false;
        }
    }

    private void addPackToArray(int pack, List<Rect> array) {
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

    private boolean isRectValid(int i) {
        return i >= 0 && i < rects.size();
    }

    private boolean isPackValid(int i) {
        return i >= 0 && i < packs.size();
    }
}
