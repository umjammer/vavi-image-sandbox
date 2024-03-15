/*
 * Copyright (C) 1997-2008 Eddie Kohler, ekohler@gmail.com
 *
 * Gifsicle is free software. It is distributed under the GNU Public License,
 * version 2 or later; you can copy, distribute, or alter it at will, as long
 * as this notice is kept intact and this source code is made available. There
 * is no warranty, express or implied.
 */

package vavi.awt.image.gif;

import java.util.Arrays;
import java.util.Comparator;


/**
 * Functions to optimize animated GIFs.
 *
 * @author <a href="mailto:ekohler@gmail.com">Eddie Kohler</a>
 */
public class GifOptimizer {

    int gif_write_flags = 0;

    static class GifColor {

        byte haspixel;
        byte red;
        byte green;
        byte blue;

        int pixel;

        byte getRed() {
            return red;
        }
        byte getGreen() {
            return green;
        }
        byte getBlue() {
            return blue;
        }
        GifColor(int red, int green, int blue) {
            this.red = (byte) red;
            this.green = (byte) green;
            this.blue = (byte) blue;
        }
    }

    class GifColorMap {
        int ncol;
        int getMapSize() {
            return ncol;
        }
        int capacity;
        int userflags;
        int refcount;
        GifColor[] col;

        GifColorMap(int count, int capacity) {
            if (capacity <= 0 || count < 0)
                throw new IllegalArgumentException();
            if (count > capacity)
                capacity = count;
            this.ncol = count;
            this.capacity = capacity;
            this.col = new GifColor[capacity];
            this.refcount = 0;
            this.userflags = 0;
        }
    }

    class GifStream {
        @SuppressWarnings("hiding")
        int screen_width;
        @SuppressWarnings("hiding")
        int screen_height;
        @SuppressWarnings("hiding")
        int background;
        GifColorMap global;
        GifImage[] images;

        void calculateScreenSize(int force) {
            int screen_width = 0;
            int screen_height = 0;

            for (GifImage gfi : this.images) {
                // 17.Dec.1999 - I find this old behavior annoying.
                // if (gfi.left != 0 || gfi.top != 0) continue;
                if (screen_width < gfi.left + gfi.width) {
                    screen_width = gfi.left + gfi.width;
                }
                if (screen_height < gfi.top + gfi.height) {
                    screen_height = gfi.top + gfi.height;
                }
            }

            // Only use the default 640x480 screen size if we are being forced
            // to create a new screen size or there's no screen size currently.
            if (screen_width == 0 && (this.screen_width == 0 || force != 0)) {
                screen_width = 640;
            }
            if (screen_height == 0 && (this.screen_height == 0 || force != 0)) {
                screen_height = 480;
            }

            if (this.screen_width < screen_width || force != 0) {
                this.screen_width = screen_width;
            }
            if (this.screen_height < screen_height || force != 0) {
                this.screen_height = screen_height;
            }
        }
        void fullCompressImage(GifImage gfi, int flags) {
            // TODO
        }
    }

    interface FreeCompressed {
        void exec(byte[] bytes);
    }

    class GifImage {
        int left;
        int top;
        int width;
        int height;
        int transparent;
        int delay;
        int disposal;
        int interlace;
        GifImage[] images;
        GifColorMap local;
        Object user_data;
        byte[] img;
        byte[] compressed;
        byte[] image_data;
        public long compressed_len;
        public FreeCompressed free_compressed;
        static final int GIF_DISPOSAL_NONE = 0;
        static final int GIF_DISPOSAL_BACKGROUND = 1;
        static final int GIF_DISPOSAL_PREVIOUS = 2;
        static final int GIF_DISPOSAL_ASIS = 3;

        int clipImage(int left, int top, int width, int height) {
            int new_width = this.width, new_height = this.height;

            if (this.img == null) {
                return 0;
            }

            if (this.left < left) {
                int shift = left - this.left;
                for (int y = 0; y < this.height; y++) {
                    this.img[y] = (byte) (this.img[y] + shift);
                }
                this.left += shift;
                new_width -= shift;
            }

            if (this.top < top) {
                int shift = top - this.top;
                for (int y = this.height - 1; y >= shift; y++) {
                    this.img[y - shift] = this.img[y];
                }
                this.top += shift;
                new_height -= shift;
            }

            if (this.left + new_width >= width) {
                new_width = width - this.left;
            }

            if (this.top + new_height >= height) {
                new_height = height - this.top;
            }

            if (new_width < 0) {
                new_width = 0;
            }
            if (new_height < 0) {
                new_height = 0;
            }
            this.width = new_width;
            this.height = new_height;
            return 1;
        }
        void uncompressImage() {
            // TODO
        }
        void setUncompressedImage(byte[] data, int flag) {
            // TODO
        }
    }

    static class GifOptBounds {
        int left;
        int top;
        int width;
        int height;
    }

    class GifOptData {
        int left;
        int top;
        int width;
        int height;
        long size;
        byte disposal;
        int transparent;
        byte[] needed_colors;
        int required_color_count;
        int active_penalty;
        int global_penalty;
        int colormap_penalty;
        GifImage new_gfi;
    }

    /* Screen width and height */
    private int screen_width;
    private int screen_height;

    /* IndexColorModel containing all colors in the image. May have >256 colors */
    private GifColorMap all_colormap;
    /* The old global colormap, or a fake one we created if necessary */
    private GifColorMap in_global_map;
    /* The new global colormap */
    private GifColorMap out_global_map;

    static final int TRANSP = 0;

    private int background;

    static final int NOT_IN_OUT_GLOBAL = 256;

    private int[] last_data;
    private int[] this_data;
    private int[] next_data;

    private int image_index;

    private int gif_color_count;

    private static int GIF_COLOREQ(GifColor c1, GifColor c2) {
        return c1.getRed() == c2.getRed() && c1.getGreen() == c2.getGreen() && c1.getBlue() == c2.getBlue() ? -1 : 0;
    }

    /**
     * SIMPLE HELPERS new and delete optimize data; and colormap_combine; and
     * sorting permutations
     */
    GifOptData new_opt_data() {
        GifOptData od = new GifOptData();
        od.needed_colors = null;
        od.global_penalty = 1;
        return od;
    }

    /**
     * Ensure that each color in 'src' is represented in
     * 'dst'. For each color 'i' in 'src', src.col[i].pixel == some j so that
     * GIF_COLOREQ(&src.col[i], &dst.col[j]). dst.col[0] is reserved for
     * transparency; no source color will be mapped to it.
     */
    void colormap_combine(GifColorMap dst, GifColorMap src) {
        int src_col/*, dst_col */;
        int i, j;

        // expand dst.col if necessary. This might change dst.col
        if (dst.getMapSize() + src.getMapSize() >= dst.capacity) {
            dst.capacity *= 2;
            dst.col = new GifColor[dst.capacity];
        }

        src_col = 0;
//        dst_col = 0;
        for (i = 0; i < src.getMapSize(); i++, src_col++) {
found:
            {
                for (j = 1; j < dst.getMapSize(); j++) {
                    if (GIF_COLOREQ(src.col[src_col], dst.col[j]) != 0) {
                        break found;
                    }
                }
                dst.col[j] = src.col[src_col];
                dst.col[j].pixel = 0;
                dst.ncol++;
            }
            src.col[src_col].pixel = j;
        }
    }

    /*
     * 9.Dec.1998 - Dumb idiot, it's time you stopped using C. The optimizer was
     * broken because I switched to long's for the sorting values without
     * considering the consequences; and the consequences were bad.
     */
    private Integer[] permuting_sort_values;

    private Comparator<Integer> permuting_sorter_up = new Comparator<Integer>() {
        public int compare(Integer n1, Integer n2) {
            return permuting_sort_values[n1] - permuting_sort_values[n2];
        }
    };

    private Comparator<Integer> permuting_sorter_down = new Comparator<Integer>() {
        public int compare(Integer n1, Integer n2) {
            return permuting_sort_values[n2] - permuting_sort_values[n1];
        }
    };

    /**
     * sorts a given permutation 'perm' according to the
     * corresponding values in 'values'. Thus, in the output, the sequence '[
     * values[perm[i]] | i <- 0..size-1 ]' will be monotonic, either up or (if
     * is_down != 0) down.
     */
    private Integer[] sort_permutation(Integer[] perm, int size, Integer[] values, int is_down) {
        permuting_sort_values = values;
        if (is_down != 0) {
            Arrays.sort(perm, permuting_sorter_down);
        } else {
            Arrays.sort(perm, permuting_sorter_up);
        }
        permuting_sort_values = null;
        return perm;
    }

    /*
     * MANIPULATING IMAGE AREAS
     */

    private static int constrain(int low, int x, int high) {
        return x < low ? low : (x < high ? x : high);
    }

    /** Returns bounds constrained to lie within the screen. */
    private GifOptBounds safe_bounds(GifImage area) {
        GifOptBounds b = new GifOptBounds();
        b.left = constrain(0, area.left, screen_width);
        b.top = constrain(0, area.top, screen_height);
        b.width = constrain(0, area.left + area.width, screen_width) - b.left;
        b.height = constrain(0, area.top + area.height, screen_height) - b.top;
        return b;
    }

    private void copy_data_area(int[] dst, int[] src, GifImage area) {
        if (area == null) {
            return;
        }
        GifOptBounds ob = safe_bounds(area);
        int dstP = ob.top * screen_width + ob.left;
        int srcP = ob.top * screen_width + ob.left;
        for (int y = 0; y < ob.height; y++) {
            System.arraycopy(src, srcP, dst, dstP, ob.width);
            dstP += screen_width;
            srcP += screen_width;
        }
    }

    private void copy_data_area_subimage(int[] dst, int[] src, GifOptData area) {
        GifImage img = new GifImage();
        img.left = area.left;
        img.top = area.top;
        img.width = area.width;
        img.height = area.height;
        copy_data_area(dst, src, img);
    }

    private void fill_data_area(int[] dst, int value, GifImage area) {
        GifOptBounds ob = safe_bounds(area);
        int dstP = ob.top * screen_width + ob.left;
        for (int y = 0; y < ob.height; y++) {
            for (int x = 0; x < ob.width; x++) {
                dst[dstP + x] = value;
            }
            dstP += screen_width;
        }
    }

    private void fill_data_area_subimage(int[] dst, int value, GifOptData area) {
        GifImage img = new GifImage();
        img.left = area.left;
        img.top = area.top;
        img.width = area.width;
        img.height = area.height;
        fill_data_area(dst, value, img);
    }

    private void erase_screen(int[] dst) {
        long screen_size = (long) screen_width * screen_height;
        for (int i = 0; i < screen_size; i++) {
            dst[i] = background;
        }
    }

    /*
     * APPLY A GIF FRAME OR DISPOSAL TO AN IMAGE DESTINATION
     */

    private void apply_frame(int[] dst, GifImage gfi, int replace, int save_uncompressed) {
        int i, y/*, was_compressed = 0*/;
        int[] map = new int[256];
        GifColorMap colormap = gfi.local != null ? gfi.local : in_global_map;
        GifOptBounds ob = safe_bounds(gfi);

        if (gfi.img == null) {
//            was_compressed = 1;
            gfi.uncompressImage();
        }

        // make sure transparency maps to TRANSP
        for (i = 0; i < colormap.getMapSize(); i++) {
            map[i] = colormap.col[i].pixel;
        }
        // out-of-bounds colors map to 0, for the sake of argument
        for (i = colormap.getMapSize(); i < 256; i++) {
            map[i] = colormap.col[0].pixel;
        }
        if (gfi.transparent >= 0 && gfi.transparent < 256) {
            map[gfi.transparent] = TRANSP;
        } else {
            replace = 1;
        }

        // map the image
        int dstP = ob.left + ob.top * screen_width;
        for (y = 0; y < ob.height; y++) {
            int gfi_pointer = y; // gfi.img
            int x;

            if (replace != 0) {
                for (x = 0; x < ob.width; x++) {
                    dst[dstP + x] = map[gfi.img[gfi_pointer + x]];
                }
            } else {
                for (x = 0; x < ob.width; x++) {
                    int new_pixel = map[gfi.img[gfi_pointer + x]];
                    if (new_pixel != TRANSP) {
                        dst[dstP + x] = new_pixel;
                    }
                }
            }

            dstP = screen_width;
        }
    }

    private void apply_frame_disposal(int[] into_data, int[] from_data, int[] previous_data, GifImage gfi) {
        int screen_size = screen_width * screen_height;
        if (gfi.disposal == GifImage.GIF_DISPOSAL_PREVIOUS) {
            System.arraycopy(previous_data, 0, into_data, 0, screen_size);
        } else {
            System.arraycopy(from_data, 0, into_data, 0, screen_size);
            if (gfi.disposal == GifImage.GIF_DISPOSAL_BACKGROUND) {
                fill_data_area(into_data, background, gfi);
            }
        }
    }

    /*
     * FIND THE SMALLEST BOUNDING RECTANGLE ENCLOSING ALL CHANGES
     */

    /**
     * Find the smallest rectangular area containing all
     * the changes and store it in 'bounds'.
     */
    private void find_difference_bounds(GifOptData bounds, GifImage gfi, GifImage last) {
        int lf, rt, lf_min, rt_max, tp, bt, x, y;
        GifOptBounds ob;

        // 1.Aug.99 - use current bounds if possible, since this function is a
        // speed bottleneck
        if (last == null || last.disposal == GifImage.GIF_DISPOSAL_NONE || last.disposal == GifImage.GIF_DISPOSAL_ASIS) {
            ob = safe_bounds(gfi);
            lf_min = ob.left;
            rt_max = ob.left + ob.width - 1;
            tp = ob.top;
            bt = ob.top + ob.height - 1;
        } else {
            lf_min = 0;
            rt_max = screen_width - 1;
            tp = 0;
            bt = screen_height - 1;
        }

        for (; tp < screen_height; tp++) {
            for (int j = 0; j < screen_width; j++) {
                if (last_data[screen_width * tp + j] != this_data[screen_width * tp + j]) {
                    break;
                }
            }
        }
        for (; bt >= tp; bt--) {
            for (int j = 0; j < screen_width; j++) {
                if (last_data[screen_width * bt + j] != this_data[screen_width * bt + j]) {
                    break;
                }
            }
        }

        lf = screen_width;
        rt = 0;
        for (y = tp; y <= bt; y++) {
            int ld = screen_width * y;
            int td = screen_width * y;
            for (x = lf_min; x < lf; x++) {
                if (last_data[ld + x] != this_data[td + x]) {
                    break;
                }
            }
            lf = x;

            for (x = rt_max; x > rt; x--) {
                if (last_data[ld + x] != this_data[td + x]) {
                    break;
                }
            }
            rt = x;
        }

        // 19.Aug.1999 - handle case when there's no difference between frames
        if (tp > bt)
            tp = bt = lf = rt = 0;

        bounds.left = lf;
        bounds.top = tp;
        bounds.width = rt + 1 - lf;
        bounds.height = bt + 1 - tp;
    }

    /**
     * If the current image has background disposal
     * and the background is transparent, we must expand the difference bounds
     * to include any blanked (newly transparent) pixels that are still
     * transparent in the next image. This function does that by comparing
     * this_data and next_data. The new bounds are passed and stored in
     * 'bounds'; the image's old bounds, which are also the maximum bounds, are
     * passed in 'this_bounds'.
     */
    private int expand_difference_bounds(GifOptData bounds, GifImage this_bounds) {
        int x, y, expanded = 0;

        int lf = bounds.left, tp = bounds.top;
        int rt = lf + bounds.width - 1, bt = tp + bounds.height - 1;

        GifOptBounds ob = safe_bounds(this_bounds);
        int tlf = ob.left, ttp = ob.top, trt = ob.left + ob.width - 1, tbt = ob.top + ob.height - 1;

        if (lf > rt || tp > bt) {
            lf = 0;
            tp = 0;
            rt = screen_width - 1;
            bt = screen_height - 1;
        }

found_top:
        for (y = ttp; y < tp; y++) {
            int now = screen_width * y;
            int next = screen_width * y;
            for (x = tlf; x <= trt; x++) {
                if (this_data[now + x] != TRANSP && next_data[next + x] == TRANSP) {
                    expanded = 1;
                    break found_top;
                }
            }
        }
        tp = y;

found_bottom:
        for (y = tbt; y > bt; y--) {
            int now = screen_width * y;
            int next = screen_width * y;
            for (x = tlf; x <= trt; x++) {
                if (this_data[now + x] != TRANSP && next_data[next + x] == TRANSP) {
                    expanded = 1;
                    break found_bottom;
                }
            }
        }
        bt = y;

found_left:
        for (x = tlf; x < lf; x++) {
            int now = x;
            int next = x;
            for (y = tp; y <= bt; y++) {
                if (this_data[now + y * screen_width] != TRANSP && next_data[next + y * screen_width] == TRANSP) {
                    expanded = 1;
                    break found_left;
                }
            }
        }
        lf = x;

found_right:
        for (x = trt; x > rt; x--) {
            int now = x;
            int next = x;
            for (y = tp; y <= bt; y++) {
                if (this_data[now + y * screen_width] != TRANSP && next_data[next + y * screen_width] == TRANSP) {
                    expanded = 1;
                    break found_right;
                }
            }
        }
        rt = x;

found_expanded:
        if (expanded == 0) {
            for (y = tp; y <= bt; ++y) {
                int now = y * screen_width;
                int next = y * screen_width;
                for (x = lf; x <= rt; ++x) {
                    if (this_data[now + x] != TRANSP && next_data[next + x] == TRANSP) {
                        expanded = 1;
                        break found_expanded;
                    }
                }
            }
        }

        bounds.left = lf;
        bounds.top = tp;
        bounds.width = rt + 1 - lf;
        bounds.height = bt + 1 - tp;
        return expanded;
    }

    /** make sure the image isn't 0x0. */
    private void fix_difference_bounds(GifOptData bounds) {
        if (bounds.width == 0 || bounds.height == 0) {
            bounds.top = 0;
            bounds.left = 0;
            bounds.width = 1;
            bounds.height = 1;
        }
        // assert that image lies completely within screen
        assert (bounds.top < screen_height && bounds.left < screen_width && bounds.top + bounds.height - 1 < screen_height && bounds.left + bounds.width - 1 < screen_width);
    }

    /*
     * DETERMINE WHICH COLORS ARE USED
     */

    private static final int REQUIRED = 2;
    private static final int REPLACE_TRANSP = 1;

    /**
     * mark which colors are needed by a given image. Returns a
     * need array so that need[j] == REQUIRED if the output colormap must
     * include all_color j; REPLACE_TRANSP if it should be replaced by
     * transparency; and 0 if it's not in the image at all.
     *
     * If use_transparency > 0, then a pixel which was the same in the last
     * frame may be replaced with transparency. If use_transparency == 2,
     * transparency MUST be set. (This happens on the first image if the
     * background should be transparent.)
     */
    private void get_used_colors(GifOptData bounds, int use_transparency) {
        int top = bounds.top, width = bounds.width, height = bounds.height;
        int i, x, y;
        int all_ncol = all_colormap.getMapSize();
        byte[] need = new byte[all_ncol];

        for (i = 0; i < all_ncol; i++) {
            need[i] = 0;
        }

        // set elements that are in the image. need == 2 means the color must be
        // in the map; need == 1 means the color may be replaced by
        // transparency.
        for (y = top; y < top + height; y++) {
            int data = screen_width * y + bounds.left;
            int last = screen_width * y + bounds.left;
            for (x = 0; x < width; x++) {
                if (this_data[data + x] != last_data[last + x]) {
                    need[this_data[data + x]] = REQUIRED;
                } else if (need[this_data[data + x]] == 0) {
                    need[this_data[data + x]] = REPLACE_TRANSP;
                }
            }
        }
        if (need[TRANSP] != 0) {
            need[TRANSP] = REQUIRED;
        }

        // check for too many colors; also force transparency if needed
        {
            int[] count = new int[3];
            // Count distinct pixels in each category
            count[0] = count[1] = count[2] = 0;
            for (i = 0; i < all_ncol; i++) {
                count[need[i]]++;
            }
            // If use_transparency is large and there's room, add transparency
            if (use_transparency > 1 && need[TRANSP] == 0 && count[REQUIRED] < 256) {
                need[TRANSP] = REQUIRED;
                count[REQUIRED]++;
            }
            // If too many "potentially transparent" pixels, force transparency
            if (count[REPLACE_TRANSP] + count[REQUIRED] > 256) {
                use_transparency = 1;
            }
            // Make sure transparency is marked necessary if we use it
            if (count[REPLACE_TRANSP] > 0 && use_transparency != 0 && need[TRANSP] == 0) {
                need[TRANSP] = REQUIRED;
                count[REQUIRED]++;
            }
            // If not using transparency, change "potentially transparent"
            // pixels to "actually used" pixels
            if (use_transparency == 0) {
                for (i = 0; i < all_ncol; i++) {
                    if (need[i] == REPLACE_TRANSP) {
                        need[i] = REQUIRED;
                    }
                }
                count[REQUIRED] += count[REPLACE_TRANSP];
            }
            // If too many "actually used" pixels, fail miserably
            if (count[REQUIRED] > 256) {
                throw new IllegalStateException("more than 256 colors required in a frame: " + count[REQUIRED]);
            }
            // If we can afford to have transparency, and we want to use it,
            // then include it
            if (count[REQUIRED] < 256 && use_transparency != 0 && need[TRANSP] == 0) {
                need[TRANSP] = REQUIRED;
                count[REQUIRED]++;
            }
            bounds.required_color_count = count[REQUIRED];
        }

        bounds.needed_colors = need;
    }

    /*
     * FIND SUBIMAGES AND COLORS USED
     */

    private void create_subimages(GifStream gfs, int optimize_level, int save_uncompressed) {
        int screen_size;
        GifImage last_gfi;
        int next_data_valid;
        int[] previous_data = null;

        screen_size = screen_width * screen_height;

        next_data = new int[screen_size];
        next_data_valid = 0;

        // do first image. Remember to uncompress it if necessary
        erase_screen(last_data);
        erase_screen(this_data);
        last_gfi = null;

        // PRECONDITION: last_data, previous_data -- garbage this_data -- equal
        // to image data after disposal of previous image next_data -- equal to
        // image data for next image if next_image_valid
        for (image_index = 0; image_index < gfs.images.length; image_index++) {
            GifImage gfi = gfs.images[image_index];
            GifOptData subimage = new_opt_data();

            // save previous data if necessary
            if (gfi.disposal == GifImage.GIF_DISPOSAL_PREVIOUS) {
                if (previous_data == null) {
                    previous_data = new int[screen_size];
                }
                copy_data_area(previous_data, this_data, gfi);
            }

            // set this_data equal to the current image
            if (next_data_valid != 0) {
                int[] temp = this_data;
                this_data = next_data;
                next_data = temp;
                next_data_valid = 0;
            } else {
                apply_frame(this_data, gfi, 0, save_uncompressed);
            }

            // find minimum area of difference between this image and last image
            subimage.disposal = GifImage.GIF_DISPOSAL_ASIS;
            if (image_index > 0) {
                find_difference_bounds(subimage, gfi, last_gfi);
            } else {
                GifOptBounds ob = safe_bounds(gfi);
                subimage.left = ob.left;
                subimage.top = ob.top;
                subimage.width = ob.width;
                subimage.height = ob.height;
            }

            // might need to expand difference border if transparent background
            // & background disposal
            if ((gfi.disposal == GifImage.GIF_DISPOSAL_BACKGROUND || gfi.disposal == GifImage.GIF_DISPOSAL_PREVIOUS) && background == TRANSP && image_index < gfs.images.length - 1) {
                // set up next_data
                GifImage next_gfi = gfs.images[image_index + 1];
                apply_frame_disposal(next_data, this_data, previous_data, gfi);
                apply_frame(next_data, next_gfi, 0, save_uncompressed);
                next_data_valid = 1;
                // expand border as necessary
                if (expand_difference_bounds(subimage, gfi) != 0) {
                    subimage.disposal = GifImage.GIF_DISPOSAL_BACKGROUND;
                }
            }

            fix_difference_bounds(subimage);

            // set map of used colors
            {
                int use_transparency = optimize_level > 1 && image_index > 0 ? -1 : 0;
                if (image_index == 0 && background == TRANSP) {
                    use_transparency = 2;
                }
                get_used_colors(subimage, use_transparency);
            }

            gfi.user_data = subimage;
            last_gfi = gfi;

            // Apply optimized disposal to last_data and unoptimized disposal to
            // this_data. Before 9.Dec.1998 I applied unoptimized disposal
            // uniformly to both. This led to subtle bugs. After all, to
            // determine bounds, we want to compare the current image (only
            // obtainable through unoptimized disposal) with what WILL be left
            // after the previous OPTIMIZED image's disposal. This fix is
            // repeated in create_new_image_data
            if (subimage.disposal == GifImage.GIF_DISPOSAL_BACKGROUND) {
                fill_data_area_subimage(last_data, background, subimage);
            } else {
                copy_data_area_subimage(last_data, this_data, subimage);
            }

            if (last_gfi.disposal == GifImage.GIF_DISPOSAL_BACKGROUND) {
                fill_data_area(this_data, background, last_gfi);
            } else if (last_gfi.disposal == GifImage.GIF_DISPOSAL_PREVIOUS) {
                int[] d = previous_data;
                previous_data = this_data;
                this_data = d;
            }
        }
    }

    /*
     * CALCULATE OUTPUT GLOBAL COLORMAP
     */

    private void increment_penalties(GifOptData opt, Integer[] penalty, int delta) {
        int all_ncol = all_colormap.getMapSize();
        byte[] need = opt.needed_colors;
        for (int i = 1; i < all_ncol; i++) {
            if (need[i] == REQUIRED) {
                penalty[i] += delta;
            }
        }
    }

    /**
     * create_out_global_map: The interface function to this pass. It creates
     * out_global_map and sets pixel values on all_colormap appropriately.
     * Specifically:
     *
     * all_colormap.col[P].pixel >= 256 ==> P is not in the global colormap.
     *
     * Otherwise, all_colormap.col[P].pixel == the J so that
     * GIF_COLOREQ(&all_colormap.col[P], &out_global_map.col[J]).
     *
     * On return, the 'colormap_penalty' component of an image's Gif_OptData
     * structure is <0 iff that image will need a local colormap.
     *
     * 20.Aug.1999 - updated to new version that arranges the entire colormap,
     * not just the stuff above 256 colors.
     */
    private void create_out_global_map(GifStream gfs) {
        int all_ncol = all_colormap.getMapSize();
        Integer[] penalty = new Integer[all_ncol];
        Integer[] permute = new Integer[all_ncol];
        int[] ordering = new int[all_ncol];
        int cur_ncol, i, imagei;
        int nglobal_all = (all_ncol <= 257 ? all_ncol - 1 : 256);
        int permutation_changed;

        // initial permutation is null
        for (i = 0; i < all_ncol - 1; i++) {
            permute[i] = i + 1;
        }

        // choose appropriate penalties for each image
        for (imagei = 0; imagei < gfs.images.length; imagei++) {
            GifOptData opt = (GifOptData) gfs.images[imagei].user_data;
            opt.global_penalty = opt.colormap_penalty = 1;
            for (i = 2; i < opt.required_color_count; i *= 2) {
                opt.colormap_penalty *= 3;
            }
            opt.active_penalty = (all_ncol > 257 ? opt.colormap_penalty : opt.global_penalty);
        }

        // set initial penalties for each color
        for (i = 1; i < all_ncol; i++) {
            penalty[i] = 0;
        }
        for (imagei = 0; imagei < gfs.images.length; imagei++) {
            GifOptData opt = (GifOptData) gfs.images[imagei].user_data;
            increment_penalties(opt, penalty, opt.active_penalty);
        }
        permutation_changed = 1;

        // Loop, removing one color at a time.
        for (cur_ncol = all_ncol - 1; cur_ncol != 0; cur_ncol--) {
            int removed;

            // sort permutation based on penalty
            if (permutation_changed != 0) {
                sort_permutation(permute, cur_ncol, penalty, 1);
            }
            permutation_changed = 0;

            // update reverse permutation
            removed = permute[cur_ncol - 1];
            ordering[removed] = cur_ncol - 1;

            // decrement penalties for colors that are out of the running
            for (imagei = 0; imagei < gfs.images.length; imagei++) {
                GifOptData opt = (GifOptData) gfs.images[imagei].user_data;
                byte[] need = opt.needed_colors;
                if (opt.global_penalty > 0 && need[removed] == REQUIRED) {
                    increment_penalties(opt, penalty, -opt.active_penalty);
                    opt.global_penalty = 0;
                    opt.colormap_penalty = (cur_ncol > 256 ? -1 : 0);
                    permutation_changed = 1;
                }
            }

            // change colormap penalties if we're no longer working w/globalmap
            if (cur_ncol == 257) {
                for (i = 0; i < all_ncol; i++) {
                    penalty[i] = 0;
                }
                for (imagei = 0; imagei < gfs.images.length; imagei++) {
                    GifOptData opt = (GifOptData) gfs.images[imagei].user_data;
                    opt.active_penalty = opt.global_penalty;
                    increment_penalties(opt, penalty, opt.global_penalty);
                }
                permutation_changed = 1;
            }
        }

        // make sure background is in the global colormap
        if (background != TRANSP && ordering[background] >= 256) {
            int other = permute[255];
            ordering[other] = ordering[background];
            ordering[background] = 255;
        }

        // assign out_global_map based on permutation
        out_global_map = new GifColorMap(nglobal_all, 256);

        for (i = 1; i < all_ncol; i++)
            if (ordering[i] < 256) {
                out_global_map.col[ordering[i]] = all_colormap.col[i];
                all_colormap.col[i].pixel = ordering[i];
            } else {
                all_colormap.col[i].pixel = NOT_IN_OUT_GLOBAL;
            }

        // set the stream's background color
        if (background != TRANSP) {
            gfs.background = ordering[background];
        }
    }

    /*
     * CREATE COLOR MAPPING FOR A PARTICULAR IMAGE
     */

    /**
     * Create and return an array of bytes mapping from
     * global pixel values to pixel values for this image. It may add colormap
     * cells to 'into'; if there isn't enough room in 'into', it will return 0.
     * It sets the 'transparent' field of 'gfi.optdata', but otherwise doesn't
     * change or read it at all.
     */
    private byte[] prepare_colormap_map(GifImage gfi, GifColorMap into, byte[] need) {
        int i;
        int is_global = (into == out_global_map) ? -1 : 0;

        int all_ncol = all_colormap.getMapSize();
        GifColor[] all_col = all_colormap.col;

        int ncol = into.getMapSize();
        GifColor[] col = into.col;

        byte[] map = new byte[all_ncol];
        byte[] into_used = new byte[256];

        // keep track of which pixel indices in 'into' have been used;
        // initially, all unused
        for (i = 0; i < 256; i++) {
            into_used[i] = 0;
        }

        // go over all non-transparent global pixels which MUST appear
        // (need[P]==REQUIRED) and place them in 'into'
error:
        {
            for (i = 1; i < all_ncol; i++) {
                int val;
                if (need[i] != REQUIRED) {
                    continue;
                }

                // fail if a needed pixel isn't in the global map
                if (is_global != 0) {
                    val = all_col[i].pixel;
                    if (val >= ncol) {
                        break error;
                    }
                } else {
                    // always place colors in a local colormap
                    if (ncol == 256) {
                        break error;
                    }
                    val = ncol;
                    col[val] = all_col[i];
                    ncol++;
                }

                map[i] = (byte) val;
                into_used[val] = 1;
            }

            // now check for transparency
            gfi.transparent = -1;
            if (need[TRANSP] != 0) {
                int transparent = -1;

                // first, look for an unused index in 'into'. Pick the lowest one:
                // the lower transparent index we get, the more likely we can shave
                // a bit off min_code_bits later, thus saving space
                for (i = 0; i < ncol; i++)
                    if (into_used[i] == 0) {
                        transparent = i;
                        break;
                    }

                // otherwise, [1.Aug.1999] use a fake slot for the purely
                // transparent color. Don't actually enter the transparent color
                // into the colormap -- we might be able to output a smaller
                // colormap! If there's no room for it, give up
                if (transparent < 0) {
                    if (ncol < 256) {
                        transparent = ncol;
                        // 1.Aug.1999 - don't increase ncol
                        col[ncol] = all_col[TRANSP];
                    } else {
                        break error;
                    }
                }

                // change mapping
                map[TRANSP] = (byte) transparent;
                for (i = 1; i < all_ncol; i++) {
                    if (need[i] == REPLACE_TRANSP) {
                        map[i] = (byte) transparent;
                    }
                }

                gfi.transparent = transparent;
            }

            // If we get here, it worked! Commit state changes (the number of color
            // cells in 'into') and return the map.
            into.ncol = ncol;
            return map;
        }

        // If we get here, it failed! Return 0 and don't change global state.
        return null;
    }

    /**
     * sort_colormap_permutation_rgb: for canonicalizing local colormaps by
     * arranging them in RGB order
     */
    private Comparator<GifColor> colormap_rgb_permutation_sorter = (col1, col2) -> {
        int value1 = (col1.getRed() << 16) | (col1.getGreen() << 8) | col1.getBlue();
        int value2 = (col2.getRed() << 16) | (col2.getGreen() << 8) | col2.getBlue();
        return value1 - value2;
    };

    /**
     * make a colormap up from the image data by fitting any
     * used colors into a colormap. Returns a map from global color index to
     * index in this image's colormap. May set a local colormap on 'gfi'.
     */
    private byte[] prepare_colormap(GifImage gfi, byte[] need) {
        byte[] map;

        // try to map pixel values into the global colormap
        gfi.local = null;
        map = prepare_colormap_map(gfi, out_global_map, need);

        if (map == null) {
            // that didn't work; add a local colormap.
            byte[] permutation = new byte[256];
            GifColor[] local_col;
            int i;

            gfi.local = new GifColorMap(0, 256);
            map = prepare_colormap_map(gfi, gfi.local, need);

            // The global colormap has already been canonicalized; we should
            // canonicalize the local colormaps as well. Do that here
            local_col = gfi.local.col;
            for (i = 0; i < gfi.local.getMapSize(); i++) {
                local_col[i].pixel = i;
            }

            Arrays.sort(local_col, colormap_rgb_permutation_sorter);

            for (i = 0; i < gfi.local.getMapSize(); i++) {
                permutation[local_col[i].pixel] = (byte) i;
            }
            // 1.Aug.1999 - we might not have added space for gfi.transparent
            if (gfi.transparent >= gfi.local.getMapSize()) {
                permutation[gfi.transparent] = (byte) gfi.transparent;
            }

            for (i = 0; i < all_colormap.getMapSize(); i++) {
                map[i] = permutation[map[i]];
            }

            if (gfi.transparent >= 0) {
                gfi.transparent = map[TRANSP];
            }
        }

        return map;
    }

    /*
     * CREATE OUTPUT FRAME DATA
     */

    /**
     * just copy the data from the image into the frame data.
     * No funkiness, no transparency, nothing
     */
    private void simple_frame_data(GifImage gfi, byte[] map) {
        GifOptBounds ob = safe_bounds(gfi);
        int x, y, scan_width = gfi.width;

        for (y = 0; y < ob.height; y++) {
            int from = screen_width * (y + ob.top) + ob.left;
            int into = y * scan_width;
            for (x = 0; x < ob.width; x++) {
                gfi.image_data[into++] = map[this_data[from++]];
            }
        }
    }

    /**
     * copy the frame data into the actual image, using
     * transparency occasionally according to a heuristic described below
     */
    private void transp_frame_data(GifStream gfs, GifImage gfi, byte[] map) {
        GifOptBounds ob = safe_bounds(gfi);
//        int scan_width = gfi.width;
        int x, y, transparent = gfi.transparent;
        int last = 0;
        int cur = 0;
        int data, swit;
        int transparentizing;

        // First, try w/o transparency. Compare this to the result using
        // transparency and pick the better of the two.
        simple_frame_data(gfi, map);
        gfs.fullCompressImage(gfi, gif_write_flags);

        /*
         * Actually copy data to frame.
         *
         * Use transparency if possible to shrink the size of the written GIF.
         *
         * The written GIF will be small if patterns (sequences of pixel values)
         * recur in the image. We could conceivably use transparency to produce
         * THE OPTIMAL image, with the most recurring patterns of the best
         * kinds; but this would be very hard (wouldn't it?). Instead, we settle
         * for a heuristic: we try and create RUNS. (Since we *try* to create
         * them, they will presumably recur!) A RUN is a series of adjacent
         * pixels all with the same value.
         *
         * By & large, we just use the regular image's values. However, we might
         * create a transparent run *not in* the regular image, if TWO OR MORE
         * adjacent runs OF DIFFERENT COLORS *could* be made transparent.
         *
         * (An area can be made transparent if the corresponding area in the
         * previous frame had the same colors as the area does now.)
         *
         * Why? If only one run (say of color C) could be transparent, we get no
         * large immediate advantage from making it transparent (it'll be a run
         * of the same length regardless). Also, we might LOSE: what if the run
         * was adjacent to some more of color C, which couldn't be made
         * transparent? If we use color C (instead of the transparent color),
         * then we get a longer run.
         *
         * This simple heuristic does a little better than Gifwizard's (6/97) on
         * some images, but does *worse than nothing at all* on others.
         *
         * However, it DOES do better than the complicated, greedy algorithm I
         * commented out above; and now we pick either the
         * transparency-optimized version or the normal version, whichever
         * compresses smaller, for the best of both worlds. (9/98)
         */

        x = y = 0;
        data = swit = 0; // gfi.image_data;
        transparentizing = 0;
//        continue calculate_pointers;
        boolean first = true;

        while (y < ob.height) {
            if (!first) {
                if (transparentizing != 0) {
                    while (x < ob.width && transparentizing != 0) {
                        if (this_data[cur] == last_data[last] || map[this_data[cur]] == transparent) {
                            gfi.image_data[data] = (byte) transparent;
                            data++;
                            last++;
                            cur++;
                            x++;
                        } else {
                            gfi.image_data[data] = map[this_data[cur]];
                            transparentizing = 0;
                            swit = data + 1;
                            data++;
                            last++;
                            cur++;
                            x++;
                        }
                    }
                } else {
                    while (x < ob.width && transparentizing == 0) {
                        gfi.image_data[data] = map[this_data[cur]];
                        if (gfi.image_data[data] == transparent || (this_data[cur] == last_data[last] && gfi.image_data[swit] != gfi.image_data[data])) {
                            transparentizing = 1;
                            for (int j = 0; j < data - swit; j++) {
                                gfi.image_data[swit + j] = (byte) transparent;
                            }
                        } else {
                            if (this_data[cur] != last_data[last]) {
                                swit = data + 1;
                            } else if (gfi.image_data[swit] != gfi.image_data[data]) {
                                swit = data;
                            }
                            data++;
                            last++;
                            cur++;
                            x++;
                        }
                    }
                }
            }

            if (!first && x >= ob.width) {
                if (!first) {
                    x = 0;
                    y++;
                }
//calculate_pointers:
                if (!first) {
                    first = false;
                }
                last = screen_width * (y + ob.top) + ob.left;
                cur = screen_width * (y + ob.top) + ob.left;
            }
        }

        // Now, try compressed transparent version and pick the better of the
        // two.
        {
            byte[] old_compressed = gfi.compressed;
            FreeCompressed old_free_compressed = gfi.free_compressed;
            long old_compressed_len = gfi.compressed_len;
            gfi.compressed = null; /* prevent freeing old_compressed */
            gfs.fullCompressImage(gfi, gif_write_flags);
            if (gfi.compressed_len > old_compressed_len) {
                gfi.compressed = old_compressed;
                gfi.free_compressed = old_free_compressed;
                gfi.compressed_len = old_compressed_len;
            } else {
                old_free_compressed.exec(old_compressed);
            }
        }
    }

    /*
     * CREATE NEW IMAGE DATA
     */

    /**
     * last == what last image ended up looking like this == what new image
     * should look like
     *
     * last = apply O1 + dispose O1 + ... + apply On-1 + dispose On-1 this =
     * apply U1 + dispose U1 + ... + apply Un-1 + dispose Un-1 + apply Un
     *
     * invariant: apply O1 + dispose O1 + ... + apply Ok === apply U1 + dispose
     * U1 + ... + apply Uk
     */
    private void create_new_image_data(GifStream gfs, int optimize_level) {
        GifImage cur_unopt_gfi; // placehoder; maintains pre-optimization image
                                // size so we can apply background disposal
        int screen_size = screen_width * screen_height;
        int[] previous_data = null;

        gfs.global = out_global_map;

        // do first image. Remember to uncompress it if necessary
        erase_screen(last_data);
        erase_screen(this_data);

        for (image_index = 0; image_index < gfs.images.length; image_index++) {
            GifImage cur_gfi = gfs.images[image_index];
            GifOptData opt = (GifOptData) cur_gfi.user_data;
            int was_compressed = (cur_gfi.img == null) ? -1 : 0;

            // save previous data if necessary
            if (cur_gfi.disposal == GifImage.GIF_DISPOSAL_PREVIOUS) {
                previous_data = new int[screen_size];
                copy_data_area(previous_data, this_data, cur_gfi);
            }

            // set up this_data to be equal to the current image
            apply_frame(this_data, cur_gfi, 0, 0);

            // save actual bounds and disposal from unoptimized version so we
            // can apply the disposal correctly next time through
            cur_unopt_gfi = cur_gfi;

            // set bounds and disposal from optdata
            cur_gfi.left = opt.left;
            cur_gfi.top = opt.top;
            cur_gfi.width = opt.width;
            cur_gfi.height = opt.height;
            cur_gfi.disposal = opt.disposal;
            if (image_index > 0) {
                cur_gfi.interlace = 0;
            }

            // find the new image's colormap and then make new data
            {
                byte[] map = prepare_colormap(cur_gfi, opt.needed_colors);
                byte[] data = new byte[cur_gfi.width * cur_gfi.height];
                cur_gfi.setUncompressedImage(data, 0);

                // don't use transparency on first frame
                if (optimize_level > 1 && image_index > 0 && cur_gfi.transparent >= 0) {
                    transp_frame_data(gfs, cur_gfi, map);
                } else {
                    simple_frame_data(cur_gfi, map);
                }

                if (cur_gfi.img != null) {
                    if (was_compressed != 0) {
                        gfs.fullCompressImage(cur_gfi, gif_write_flags);
                    } else {
                        // bug fix 22.May.2001
                    }
                }
            }

            cur_gfi.user_data = null;

            // Set up last_data and this_data. last_data must contain this_data
            // + new disposal. this_data must contain this_data + old disposal.
            if (cur_gfi.disposal == GifImage.GIF_DISPOSAL_NONE ||
                cur_gfi.disposal == GifImage.GIF_DISPOSAL_ASIS) {

                copy_data_area(last_data, this_data, cur_gfi);
            } else if (cur_gfi.disposal == GifImage.GIF_DISPOSAL_BACKGROUND) {
                fill_data_area(last_data, background, cur_gfi);
            } else {
                assert false : "optimized frame has strange disposal";
            }

            if (cur_unopt_gfi.disposal == GifImage.GIF_DISPOSAL_BACKGROUND)
                fill_data_area(this_data, background, cur_unopt_gfi);
            else if (cur_unopt_gfi.disposal == GifImage.GIF_DISPOSAL_PREVIOUS) {
                copy_data_area(this_data, previous_data, cur_unopt_gfi);
            }
        }
    }

    /*
     * INITIALIZATION AND FINALIZATION
     */

    private int initialize_optimizer(GifStream gfs) {
        int i, screen_size;

        if (gfs.images.length < 1)
            return 0;

        // combine colormaps
        all_colormap = new GifColorMap(1, 384);
        all_colormap.col[0] = new GifColor(255, 255, 255);

        in_global_map = gfs.global;
        if (in_global_map == null) {
            GifColor[] col;
            in_global_map = new GifColorMap(256, 256);
            col = in_global_map.col;
            for (i = 0; i < 256; i++) {
                col[i] = new GifColor(i, i, i);
            }
        }

        {
            int any_globals = 0;
            int first_transparent = -1;
            for (i = 0; i < gfs.images.length; i++) {
                GifImage gfi = gfs.images[i];
                if (gfi.local != null) {
                    colormap_combine(all_colormap, gfi.local);
                } else {
                    any_globals = 1;
                }
                if (gfi.transparent >= 0 && first_transparent < 0) {
                    first_transparent = i;
                }
            }
            if (any_globals != 0) {
                colormap_combine(all_colormap, in_global_map);
            }

            // try and maintain transparency's pixel value
            if (first_transparent >= 0) {
                GifImage gfi = gfs.images[first_transparent];
                GifColorMap gfcm = gfi.local != null ? gfi.local : gfs.global;
                all_colormap.col[TRANSP] = gfcm.col[gfi.transparent];
            }
        }

        // find screen_width and screen_height, and clip all images to screen
        gfs.calculateScreenSize(0);
        screen_width = gfs.screen_width;
        screen_height = gfs.screen_height;
        for (i = 0; i < gfs.images.length; i++) {
            gfs.images[i].clipImage(0, 0, screen_width, screen_height);
        }

        // create data arrays
        screen_size = screen_width * screen_height;
        last_data = new int[screen_size];
        this_data = new int[screen_size];

        // set up colormaps
        gif_color_count = 2;
        while (gif_color_count < gfs.global.getMapSize() && gif_color_count < 256) {
            gif_color_count *= 2;
        }

        // choose background
        if (gfs.images[0].transparent < 0 && gfs.background < in_global_map.getMapSize()) {
            background = in_global_map.col[gfs.background].pixel;
        } else {
            background = TRANSP;
        }

        return 1;
    }

    private void finalize_optimizer(GifStream gfs) {
        if (background == TRANSP) {
            gfs.background = (byte) gfs.images[0].transparent;
        }

        // 10.Dec.1998 - prefer GIF_DISPOSAL_NONE to GIF_DISPOSAL_ASIS. This is
        // semantically "wrong" -- it's better to set the disposal explicitly
        // than rely on default behavior -- but will result in smaller GIF
        // files, since the graphic control extension can be left off in many
        // cases.
        for (int i = 0; i < gfs.images.length; i++) {
            if (gfs.images[i].disposal == GifImage.GIF_DISPOSAL_ASIS &&
                gfs.images[i].delay == 0 &&
                gfs.images[i].transparent < 0) {

                gfs.images[i].disposal = GifImage.GIF_DISPOSAL_NONE;
            }
        }
    }

    /** the interface function! */
    void optimize_fragments(GifStream gfs, int optimize_level, int huge_stream) {
        if (initialize_optimizer(gfs) == 0) {
            return;
        }

        create_subimages(gfs, optimize_level, ~huge_stream);
        create_out_global_map(gfs);
        create_new_image_data(gfs, optimize_level);

        finalize_optimizer(gfs);
    }
}
