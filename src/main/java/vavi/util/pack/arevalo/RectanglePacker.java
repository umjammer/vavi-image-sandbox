/*
Nuclex Framework
Copyright (C) 2002-2009 Nuclex Development Labs

This library is free software; you can redistribute it and/or
modify it under the terms of the IBM Common Public License as
published by the IBM Corporation; either version 1.0 of the
License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
IBM Common Public License for more details.

You should have received a copy of the IBM Common Public
License along with this library
 */

package vavi.util.pack.arevalo;

import java.awt.Point;


/**
 * Base class for rectangle packing algorithms</summary>
 * <p>
 * By uniting all rectangle packers under this common base class, you can easily
 * switch between different algorithms to find the most efficient or performant
 * one for a given job.
 * </p>
 * <p>
 * An almost exhaustive list of packing algorithms can be found here:
 * http://www.csc.liv.ac.uk/~epa/surveyhtml.html
 * </p>
 */
public abstract class RectanglePacker {

    /**
     * Initializes a new rectangle packer
     *
     * @param packingAreaWidth Width of the packing area
     * @param packingAreaHeight Height of the packing area
     */
    protected RectanglePacker(int packingAreaWidth, int packingAreaHeight) {
        this.packingAreaWidth = packingAreaWidth;
        this.packingAreaHeight = packingAreaHeight;
    }

    /**
     * Allocates space for a rectangle in the packing area
     *
     * @param rectangleWidth Width of the rectangle to allocate
     * @param rectangleHeight Height of the rectangle to allocate
     * @return The location at which the rectangle has been placed
     */
    public Point pack(int rectangleWidth, int rectangleHeight) throws OutOfSpaceException {
        Point point = new Point();

        if (!tryPack(rectangleWidth, rectangleHeight, /* out */point)) {
            throw new OutOfSpaceException("Rectangle does not fit in packing area");
        }

        return point;
    }

    /**
     * Tries to allocate space for a rectangle in the packing area
     *
     * @param rectangleWidth Width of the rectangle to allocate
     * @param rectangleHeight Height of the rectangle to allocate
     * @param placement Output parameter receiving the rectangle's placement
     * @return True if space for the rectangle could be allocated
     */
    public abstract boolean tryPack(int rectangleWidth, int rectangleHeight, /* out */Point placement);

    /** Maximum width the packing area is allowed to have */
    public int getPackingAreaWidth() {
        return packingAreaWidth;
    }

    /** Maximum height the packing area is allowed to have */
    public int getPackingAreaHeight() {
        return packingAreaHeight;
    }

    /** Maximum allowed width of the packing area */
    protected int packingAreaWidth;

    /** Maximum allowed height of the packing area */
    protected int packingAreaHeight;
}
