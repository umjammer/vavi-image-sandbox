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
import java.util.Random;

import org.junit.jupiter.api.Disabled;


/** Base class for unit testing the rectangle packers */
@Disabled
public abstract class RectanglePackerTest {

    /**
     * Delegate for a Rectangle Packer factory method
     */
    protected RectanglePacker rectanglePackerBuilder;

    /**
     * Determines the efficiency of a packer with a packing area of 70x70
     * 
     * @param packer Packer with a packing area of 70x70 units
     * @return The efficiency factor of the packer
     *         <p>
     *         A perfect packer would achieve an efficiency rating of 1.0. This
     *         is impossible however since the 24 squares cannot all be packed
     *         into the 70x70 square with no overlap (Bitner &amp; Reingold
     *         1975). The closer the efficiency rating is to 1.0, the better,
     *         with 0.99 being the mathematically best rating achievable.
     *         </p>
     */
    protected float calculateEfficiency(RectanglePacker packer) {
        // If we take a 1x1 square, a 2x2 square, etc. up to a 24x24 square,
        // the sum of the areas of these squares is 4900, which is 70 This
        // is the only nontrivial sum of consecutive squares starting with
        // one which is a perfect square (Watson 1918).
        int areaCovered = 0;

        for (int size = 24; size >= 1; --size) {
            Point placement = new Point();

            if (packer.tryPack(size, size, /* out */placement)) {
                areaCovered += size * size;
            }
        }

        return areaCovered / 4900.0f;
    }

    /**
     * Benchmarks the provided rectangle packer using random data
     *
     * @param buildPacker Rectangle packer build method returning new rectangle
     *            packers with an area of 1024 x 1024
     *
     * @return The achieved benchmark score
     */
    protected float benchmark(RectanglePacker buildPacker) {
        // How many runs to perform for getting a stable average
        final int averagingRuns = 1;

        // Generates the random number seeds. This is used so that each run
        // produces
        // the same number sequences and makes the comparison of different
        // algorithms
        // a little bit more stable.
        Random seedGenerator = new Random(12345);
        int rectanglesPacked = 0;

        // Perform a number of runs to get a semi-stable average score
        for (int averagingRun = 0; averagingRun < averagingRuns; ++averagingRun) {
            Random dimensionGenerator = new Random(seedGenerator.nextLong());
            RectanglePacker packer = buildPacker;

            // Try to cramp as many rectangles into the packing area as possible
            for (;; ++rectanglesPacked) {
                Point placement = new Point();

                int width = dimensionGenerator.nextInt(48) + 16;
                int height = dimensionGenerator.nextInt(48) + 16;

                // As soon as the packer rejects the first rectangle, the run is
                // over
                if (!packer.tryPack(width, height, /* out */placement)) {
                    break;
                }
            }
        }

        // Return the average score achieved by the packer
        return (float) rectanglesPacked / (float) averagingRuns;
    }
}
