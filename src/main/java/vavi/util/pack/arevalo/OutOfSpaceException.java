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

import java.io.Serializable;


/**
 * Insufficient space left in packing area to contain a given object
 * <p>
 * An exception being sent to you from deep space. Erm, no, wait, it's an
 * exception that occurs when a packing algorithm runs out of space and is
 * unable to fit the object you tried to pack into the remaining packing area.
 * </p>
 */
public class OutOfSpaceException extends Exception implements Serializable {

    /** Initializes the exception */
    public OutOfSpaceException() {
    }

    /**
     * Initializes the exception with an error message
     *
     * @param message Error message describing the cause of the exception
     */
    public OutOfSpaceException(String message) {
        super(message);
    }

    /**
     * Initializes the exception as a followup exception
     *
     * @param message Error message describing the cause of the exception
     * @param inner Preceding exception that has caused this exception
     */
    public OutOfSpaceException(String message, Throwable inner) {
        super(message, inner);
    }
}
