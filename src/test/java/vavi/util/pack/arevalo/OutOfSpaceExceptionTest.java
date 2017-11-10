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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

import vavi.util.pack.arevalo.OutOfSpaceException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/** Unit Test for the OutOfSpaceException class */
public class OutOfSpaceExceptionTest {

    /**
     * Verifies that the exception's default constructor is working
     */
    @Test
    public void testDefaultConstructor() {
        OutOfSpaceException testException = new OutOfSpaceException();

        String testExceptionString = testException.toString();
        assertNotNull(testExceptionString);
    }

    /**
     * Checks whether the exception correctly stores its inner exception
     */
    @Test
    public void testInnerException() {
        Exception inner = new Exception("This is a test");
        OutOfSpaceException testException = new OutOfSpaceException("Hello World", inner);

        assertEquals(inner, testException.getCause());
    }

    /**
     * Test whether the exception can be serialized
     */
    @Test
    public void testSerialization() throws Exception {

        ByteArrayOutputStream memory = new ByteArrayOutputStream();
        ObjectOutputStream formatter = new ObjectOutputStream(memory);
        OutOfSpaceException exception1 = new OutOfSpaceException("Hello World");

        formatter.writeObject(exception1);
        ByteArrayInputStream memoryIn = new ByteArrayInputStream(memory.toByteArray());
        ObjectInputStream formatterIn = new ObjectInputStream(memoryIn);
        Object exception2 = formatterIn.readObject();

        assertTrue(OutOfSpaceException.class.isInstance(exception2));
        assertEquals(exception1.getMessage(), ((OutOfSpaceException) exception2).getMessage());
    }
}
