/*
 * http://www.dreamincode.net/forums/topic/27950-steganography/
 */

package vavi.util.steganography;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;


/**
 * Class Steganography
 *
 * @author William_Wilson
 * @version 1.6 May 8, 2007
 */
public class Steganography {

    /** */
    private BufferedImage userSpace;

    /** */
    public Steganography(BufferedImage image) {
        createUserSpace(image);
    }

    /**
     * Encrypt an image with text, the output file will be of type .png
     *
     * @param message The text to hide in the image
     */
    public BufferedImage encode(String message) {
        //user space is not necessary for Encrypting
        addText(message);
        return userSpace;
    }

    /**
     * Decrypt assumes the image being used is of type .png, extracts the hidden
     * text from an image
     */
    public String decode() {
        //user space is necessary for decrypting
        return new String(decodeText(getByteData()));
    }

    /**
     * Handles the addition of text into an image
     *
     * @param text The text to hide in the image
     */
    private void addText(String text) {
        // convert all items to byte arrays: image, message, message length
        byte[] img = getByteData();
        byte[] msg = text.getBytes();
        byte[] len = convertBit(msg.length);
        encodeText(img, len, 0); // 0 first positiong
        encodeText(img, msg, 32); // 4 bytes of space for length: 4bytes * 8bit = 32 bits
    }

    /**
     * Creates a user space version of a Buffered Image, for editing and saving
     * bytes
     *
     * @param image The image to put into user space, removes compression
     *            interferences
     */
    private void createUserSpace(BufferedImage image) {
        // create new_img with the attributes of image
        userSpace = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        Graphics2D graphics = userSpace.createGraphics();
        graphics.drawRenderedImage(image, null);
        graphics.dispose(); // release all allocated memory for this image
    }

    /**
     * Gets the byte array of an image
     *
     * @return Returns the byte array of the image supplied
     * @see Raster
     * @see WritableRaster
     * @see DataBufferByte
     */
    private byte[] getByteData() {
        WritableRaster raster = userSpace.getRaster();
        DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
        return buffer.getData();
    }

    /**
     * Gernerates proper byte format of an integer
     *
     * @param i The integer to convert
     * @return Returns a byte[4] array converting the supplied integer into
     *         bytes
     */
    private byte[] convertBit(int i) {
        // originally integers (ints) cast into bytes
        // byte byte7 = (byte)((i & 0xff00000000000000l) >>> 56);
        // byte byte6 = (byte)((i & 0x00ff000000000000l) >>> 48);
        // byte byte5 = (byte)((i & 0x0000ff0000000000l) >>> 40);
        // byte byte4 = (byte)((i & 0x000000ff00000000l) >>> 32);

        // only using 4 bytes
        byte byte3 = (byte) ((i & 0xff000000) >>> 24); // 0
        byte byte2 = (byte) ((i & 0x00ff0000) >>> 16); // 0
        byte byte1 = (byte) ((i & 0x0000ff00) >>> 8); // 0
        byte byte0 = (byte) ((i & 0x000000ff));
        // { 0, 0, 0, byte0 } is equivalent, since all shifts >=8 will be 0
        return new byte[] {
            byte3, byte2, byte1, byte0
        };
    }

    /**
     * Encode an array of bytes into another array of bytes at a supplied offset
     *
     * @param image Array of data representing an image
     * @param addition Array of data to add to the supplied image data array
     * @param offset The offset into the image array to add the addition data
     * @return Returns data Array of merged image and addition data
     */
    private byte[] encodeText(byte[] image, byte[] addition, int offset) {
        // check that the data + offset will fit in the image
        if (addition.length + offset > image.length) {
            throw new IllegalArgumentException("File not long enough!");
        }
        // loop through each addition byte
        for (int i = 0; i < addition.length; ++i) {
            // loop through the 8 bits of each byte
            int add = addition[i];
            for (int bit = 7; bit >= 0; --bit, ++offset) { // ensure the new offset value carries on through both loops
                // assign an integer to b, shifted by bit spaces AND 1
                // a single bit of the current byte
                int b = (add >>> bit) & 1;
                // assign the bit by taking: [(previous byte value) AND 0xfe] OR bit to add
                // changes the last bit of the byte in the image to be the bit of addition
                image[offset] = (byte) ((image[offset] & 0xFE) | b);
            }
        }
        return image;
    }

    /**
     * Retrieves hidden text from an image
     * 
     * @param image Array of data, representing an image
     * @return Array of data which contains the hidden text
     */
    private byte[] decodeText(byte[] image) {
        int length = 0;
        int offset = 32;
        // loop through 32 bytes of data to determine text length
        for (int i = 0; i < 32; ++i) { // i = 24 will also work, as only the 4th byte contains real data
            length = (length << 1) | (image[i] & 1);
        }

        byte[] result = new byte[length];

        // loop through each byte of text
        for (int b = 0; b < result.length; ++b) {
            // loop through each bit within a byte of text
            for (int i = 0; i < 8; ++i, ++offset) {
                // assign bit: [(new byte value) << 1] OR [(text byte) AND 1]
                result[b] = (byte) ((result[b] << 1) | (image[offset] & 1));
            }
        }
        return result;
    }
}
