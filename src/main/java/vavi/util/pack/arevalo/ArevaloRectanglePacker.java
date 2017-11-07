/*
 * Nuclex Framework
 * Copyright (C) 2002-2009 Nuclex Development Labs
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the IBM Common Public License as
 * published by the IBM Corporation; either version 1.0 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * IBM Common Public License for more details.
 *
 * You should have received a copy of the IBM Common Public
 * License along with this library
 */

package vavi.util.pack.arevalo;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Rectangle packer using an algorithm by Javier Arevalo
 * <p>
 * Original code by Javier Arevalo (jare at iguanademos dot com). Rewritten to
 * C# / .NET by Markus Ewald (cygon at nuclex dot org). The following comments
 * were written by the original author when he published his algorithm.
 * </p>
 * <p>
 * You have a bunch of rectangular pieces. You need to arrange them in a
 * rectangular surface so that they don't overlap, keeping the total area of the
 * rectangle as small as possible. This is fairly common when arranging
 * characters in a bitmapped font, lightmaps for a 3D engine, and I guess other
 * situations as well.
 * </p>
 * <p>
 * The idea of this algorithm is that, as we add rectangles, we can pre-select
 * "interesting" places where we can try to add the next rectangles. For optimal
 * results, the rectangles should be added in order. I initially tried using
 * area as a sorting criteria, but it didn't work well with very tall or very
 * flat rectangles. I then tried using the longest dimension as a selector, and
 * it worked much better. So much for intuition...
 * </p>
 * <p>
 * These "interesting" places are just to the right and just below the currently
 * added rectangle. The first rectangle, obviously, goes at the top left, the
 * next one would go either to the right or below this one, and so on. It is a
 * weird way to do it, but it seems to work very nicely.
 * </p>
 * <p>
 * The way we search here is fairly brute-force, the fact being that for most
 * offline purposes the performance seems more than adequate. I have generated a
 * japanese font with around 8500 characters and all the time was spent
 * generating the bitmaps.
 * </p>
 * <p>
 * Also, for all we care, we could grow the parent rectangle in a different way
 * than power of two. It just happens that power of 2 is very convenient for
 * graphics hardware textures.
 * </p>
 * <p>
 * I'd be interested in hearing of other approaches to this problem. Make sure
 * to post them on http://www.flipcode.com
 * </p>
 */
public class ArevaloRectanglePacker extends RectanglePacker {

    /**
     * Compares the 'rank' of anchoring points
     * <p>
     * Anchoring points are potential locations for the placement of new
     * rectangles. Each time a rectangle is inserted, an anchor point is
     * generated on its upper right end and another one at its lower left end.
     * The anchor points are kept in a list that is ordered by their closeness
     * to the upper left corner of the packing area (their 'rank') so the packer
     * favors positions that are closer to the upper left for new rectangles.
     * </p>
     */
    private static class AnchorRankComparer implements Comparator<Point> {

        /**
         * Provides a default instance for the anchor rank comparer
         */
        public final static AnchorRankComparer Default = new AnchorRankComparer();

        /**
         * Compares the rank of two anchors against each other
         *
         * @param left Left anchor point that will be compared
         * @param right Right anchor point that will be compared
         * @return The relation of the two anchor point's ranks to each other
         */
        public int compare(Point left, Point right) {
            // return Math.min(left.X, left.Y) - Math.min(right.X, right.Y);
            return (left.x + left.y) - (right.x + right.y);
        }
    }

    /**
     * Initializes a new rectangle packer
     *
     * @param packingAreaWidth Maximum width of the packing area
     * @param packingAreaHeight Maximum height of the packing
     */
    public ArevaloRectanglePacker(int packingAreaWidth, int packingAreaHeight) {
        super(packingAreaWidth, packingAreaHeight);

        this.packedRectangles = new ArrayList<>();
        this.anchors = new ArrayList<>();
        this.anchors.add(new Point(0, 0));

        this.actualPackingAreaWidth = 1;
        this.actualPackingAreaHeight = 1;
    }

    /**
     * Tries to allocate space for a rectangle in the packing area
     *
     * @param rectangleWidth Width of the rectangle to allocate
     * @param rectangleHeight Height of the rectangle to allocate
     * @param placement Output parameter receiving the rectangle's placement
     * @return True if space for the rectangle could be allocated
     */
    @Override
    public boolean tryPack(int rectangleWidth, int rectangleHeight, /* out */Point placement) {

        // Try to find an anchor where the rectangle fits in, enlarging the
        // packing
        // area and repeating the search recursively until it fits or the
        // maximum allowed size is exceeded.
        int anchorIndex = selectAnchorRecursive(rectangleWidth, rectangleHeight, actualPackingAreaWidth, actualPackingAreaHeight);

        // No anchor could be found at which the rectangle did fit in
        if (anchorIndex == -1) {
            placement.x = 0;
            placement.y = 0;
            return false;
        }

        Point point = anchors.get(anchorIndex);
        placement.x = point.x;
        placement.y = point.y;

        // Move the rectangle either to the left or to the top until it collides
        // with
        // a neightbouring rectangle. This is done to combat the effect of
        // lining up
        // rectangles with gaps to the left or top of them because the anchor
        // that
        // would allow placement there has been blocked by another rectangle
        optimizePlacement(/* ref */placement, rectangleWidth, rectangleHeight);

        // Remove the used anchor and add new anchors at the upper right and
        // lower left
        // positions of the new rectangle
        {
            // The anchor is only removed if the placement optimization didn't
            // move the rectangle so far that the anchor isn't blocked anymore
            boolean blocksAnchor = ((placement.x + rectangleWidth) > anchors.get(anchorIndex).x) &&
                                   ((placement.y + rectangleHeight) > anchors.get(anchorIndex).y);

            if (blocksAnchor) {
                anchors.remove(anchorIndex);
            }

            // Add new anchors at the upper right and lower left coordinates of
            // the rectangle
            insertAnchor(new Point(placement.x + rectangleWidth, placement.y));
            insertAnchor(new Point(placement.x, placement.y + rectangleHeight));
        }

        // Finally, we can add the rectangle to our packed rectangles list
        packedRectangles.add(new Rectangle(placement.x, placement.y, rectangleWidth, rectangleHeight));

        return true;
    }

    /**
     * Optimizes the rectangle's placement by moving it either left or up to
     * fill any gaps resulting from rectangles blocking the anchors of the most
     * optimal placements.
     *
     * @param placement Placement to be optimized
     * @param rectangleWidth Width of the rectangle to be optimized
     * @param rectangleHeight Height of the rectangle to be optimized
     */
    private void optimizePlacement(
    /* ref */Point placement, int rectangleWidth, int rectangleHeight) {
        Rectangle rectangle = new Rectangle(placement.x, placement.y, rectangleWidth, rectangleHeight);

        // Try to move the rectangle to the left as far as possible
        int leftMost = placement.x;
        while (isFree(/* ref */rectangle, packingAreaWidth, packingAreaHeight)) {
            leftMost = rectangle.x;
            --rectangle.x;
        }

        // Reset rectangle to original position
        rectangle.x = placement.x;

        // Try to move the rectangle upwards as far as possible
        int topMost = placement.y;
        while (isFree(/* ref */rectangle, packingAreaWidth, packingAreaHeight)) {
            topMost = rectangle.y;
            --rectangle.y;
        }

        // Use the dimension in which the rectangle could be moved farther
        if ((placement.x - leftMost) > (placement.y - topMost)) {
            placement.x = leftMost;
        } else {
            placement.y = topMost;
        }
    }

    /**
     * Searches for a free anchor and recursively enlarges the packing area if
     * none can be found.
     *
     * @param rectangleWidth Width of the rectangle to be placed
     * @param rectangleHeight Height of the rectangle to be placed
     * @param testedPackingAreaWidth Width of the tested packing area
     * @param testedPackingAreaHeight Height of the tested packing area
     * @return Index of the anchor the rectangle is to be placed at or -1 if the
     *         rectangle does not fit in the packing area anymore.
     */
    private int selectAnchorRecursive(int rectangleWidth, int rectangleHeight, int testedPackingAreaWidth, int testedPackingAreaHeight) {

        // Try to locate an anchor point where the rectangle fits in
        int freeAnchorIndex = findFirstFreeAnchor(rectangleWidth, rectangleHeight, testedPackingAreaWidth, testedPackingAreaHeight);

        // If a the rectangle fits without resizing packing area (any further in
        // case
        // of a recursive call), take over the new packing area size and return
        // the
        // anchor at which the rectangle can be placed.
        if (freeAnchorIndex != -1) {
            this.actualPackingAreaWidth = testedPackingAreaWidth;
            this.actualPackingAreaHeight = testedPackingAreaHeight;

            return freeAnchorIndex;
        }

        //
        // If we reach this point, the rectangle did not fit in the current
        // packing
        // area and our only choice is to try and enlarge the packing area.
        //

        // For readability, determine whether the packing area can be enlarged
        // any further in its width and in its height
        boolean canEnlargeWidth = (testedPackingAreaWidth < packingAreaWidth);
        boolean canEnlargeHeight = (testedPackingAreaHeight < packingAreaHeight);
        boolean shouldEnlargeHeight = (!canEnlargeWidth) || (testedPackingAreaHeight < testedPackingAreaWidth);

        // Try to enlarge the smaller of the two dimensions first (unless the
        // smaller
        // dimension is already at its maximum size). 'shouldEnlargeHeight' is
        // true
        // when the height was the smaller dimension or when the width is maxed
        // out.
        if (canEnlargeHeight && shouldEnlargeHeight) {
            // Try to double the height of the packing area
            return selectAnchorRecursive(rectangleWidth, rectangleHeight, testedPackingAreaWidth, Math.min(testedPackingAreaHeight * 2, packingAreaHeight));
        } else if (canEnlargeWidth) {
            // Try to double the width of the packing area
            return selectAnchorRecursive(rectangleWidth, rectangleHeight, Math.min(testedPackingAreaWidth * 2, packingAreaWidth), testedPackingAreaHeight);
        } else {
            // Both dimensions are at their maximum sizes and the rectangle
            // still
            // didn't fit. We give up!
            return -1;
        }
    }

    /**
     * Locates the first free anchor at which the rectangle fits
     *
     * @param rectangleWidth Width of the rectangle to be placed
     * @param rectangleHeight Height of the rectangle to be placed
     * @param testedPackingAreaWidth Total width of the packing area
     * @param testedPackingAreaHeight Total height of the packing area
     * @return The index of the first free anchor or -1 if none is found
     */
    private int findFirstFreeAnchor(int rectangleWidth, int rectangleHeight, int testedPackingAreaWidth, int testedPackingAreaHeight) {
        Rectangle potentialLocation = new Rectangle(0, 0, rectangleWidth, rectangleHeight);

        // Walk over all anchors (which are ordered by their distance to the
        // upper left corner of the packing area) until one is discovered that
        // can house the new rectangle.
        for (int index = 0; index < anchors.size(); ++index) {
            potentialLocation.x = anchors.get(index).x;
            potentialLocation.y = anchors.get(index).y;

            // See if the rectangle would fit in at this anchor point
            if (isFree(/* ref */potentialLocation, testedPackingAreaWidth, testedPackingAreaHeight)) {
                return index;
            }
        }

        // No anchor points were found where the rectangle would fit in
        return -1;
    }

    /**
     * Determines whether the rectangle can be placed in the packing area at its
     * current location.
     *
     * @param rectangle Rectangle whose position to check
     * @param testedPackingAreaWidth Total width of the packing area
     * @param testedPackingAreaHeight Total height of the packing area
     * @return True if the rectangle can be placed at its current position
     */
    private boolean isFree(
    /* ref */Rectangle rectangle, int testedPackingAreaWidth, int testedPackingAreaHeight) {

        // If the rectangle is partially or completely outside of the packing
        // area, it can't be placed at its current location
        boolean leavesPackingArea = (rectangle.x < 0) || (rectangle.y < 0) ||
                                    (rectangle.width > testedPackingAreaWidth) ||
                                    (rectangle.height > testedPackingAreaHeight);

        if (leavesPackingArea) {
            return false;
        }

        // Brute-force search whether the rectangle touches any of the other
        // rectangles already in the packing area
        for (int index = 0; index < packedRectangles.size(); ++index) {
            if (packedRectangles.get(index).intersects(rectangle)) {
                return false;
            }
        }

        // Success! The rectangle is inside the packing area and doesn't overlap
        // with any other rectangles that have already been packed.
        return true;
    }

    /**
     * Inserts a new anchor point into the anchor list
     *
     * @param anchor Anchor point that will be inserted <remarks> This method
     *            tries to keep the anchor list ordered by ranking the anchors
     *            depending on the distance from the top left corner in the
     *            packing area.
     */
    private void insertAnchor(Point anchor) {

        // Find out where to insert the new anchor based on its rank (which is
        // calculated based on the anchor's distance to the top left corner of
        // the packing area).
        //
        // From MSDN on BinarySearch():
        // "If the List does not contain the specified value, the method returns
        // a negative integer. You can apply the bitwise complement operation
        // (~) to
        // this negative integer to get the index of the first element that is
        // larger than the search value."
        Collections.sort(anchors, AnchorRankComparer.Default);
        int insertIndex = anchors.indexOf(anchor);
        if (insertIndex < 0) {
            insertIndex = ~insertIndex;
        }

        // Insert the anchor at the index matching its rank
        anchors.add(insertIndex, anchor);
    }

    /** Current width of the packing area */
    private int actualPackingAreaWidth;

    /** Current height of the packing area */
    private int actualPackingAreaHeight;

    /** Rectangles contained in the packing area */
    private List<Rectangle> packedRectangles;

    /** Anchoring points where new rectangles can potentially be placed */
    private List<Point> anchors;
}
