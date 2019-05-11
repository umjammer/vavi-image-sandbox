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

import org.junit.jupiter.api.Test;

import vavi.util.pack.arevalo.ArevaloRectanglePacker;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;


/** Unit test for the arevalo rectangle packer class */
public class ArevaloRectanglePackerTest extends RectanglePackerTest {

    /** Tests the packer's efficiency using a deterministic benchmark */
    @Test
    public void testSpaceEfficiency() {
        float efficiency = calculateEfficiency(new ArevaloRectanglePacker(70, 70));

        assertTrue(efficiency >= 0.75f); // Packer achieves 75% efficiency
    }

    /**
     * Tests whether the packer throws the appropriate exception if a rectangle
     * is too large to fit in the packing area
     */
    @Test
    public void testTooLargeRectangle() {
        ArevaloRectanglePacker packer = new ArevaloRectanglePacker(128, 128);
        Point placement = new Point();

        boolean result = packer.tryPack(129, 10, /* out */placement);
        assertFalse(result);

        result = packer.tryPack(10, 129, /* out */placement);
        assertFalse(result);
    }

    /** Verifies that the packer rejects a rectangle that is too large */
    @Test
    public void testThrowOnTooLargeRectangle() {
        ArevaloRectanglePacker packer = new ArevaloRectanglePacker(128, 128);
        try {
            packer.pack(129, 129);
            fail();
        } catch (Exception e) {
        }
    }

    /**
     * Verifies that the packer can pack a rectangle that barely fits in the
     * packing area
     */
    @Test
    public void testBarelyFittingRectangle() throws Exception {
        ArevaloRectanglePacker packer = new ArevaloRectanglePacker(128, 128);

        Point placement = packer.pack(128, 128);

        assertEquals(new Point(0, 0), placement);
    }

    /** Tests the packer's stability by running a complete benchmark */
//    @Test
    public void testStability() {
        float score = benchmark(new ArevaloRectanglePacker(1024, 1024));
        System.out.println("score: " + score);
        // This is mainly a stability and performance test. It fails when the
        // packer crashes on its own and is otherwise only there to tell how
        // long
        // it takes to complete the benchmark.
    }
}
