package vavi.util.steganography;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;


class Model {

    /**
     * Encrypt an image with text, the output file will be of type .png
     * 
     * @param path The path (folder) containing the image to modify
     * @param original The name of the image to modify
     * @param ext1 The extension type of the image to modify (jpg, png)
     * @param stegan The output name of the file
     * @param message The text to hide in the image
     * @param type integer representing either basic or advanced encoding
     */
    public boolean encode(String path, String original, String ext1, String stegan, String message) {
        String file_name = image_path(path, original, ext1);
        BufferedImage image_orig = getImage(file_name);
        BufferedImage image = null;
        try {
            Steganography steganography = new Steganography(image_orig);
            image = steganography.encode(message);
            return setImage(image, new File(image_path(path, stegan, "png")), "png");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Target File cannot hold message!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /**
     * Decrypt assumes the image being used is of type .png, extracts the hidden
     * text from an image
     * 
     * @param path The path (folder) containing the image to extract the message
     *            from
     * @param name The name of the image to extract the message from
     * @param type integer representing either basic or advanced encoding
     */
    public String decode(String path, String name) {
        try {
            Steganography steganography = new Steganography(getImage(image_path(path, name, "png")));
            return steganography.decode();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null,
                                          "There is no hidden message in this image!",
                                          "Error",
                                          JOptionPane.ERROR_MESSAGE);
            return "";
        }
    }

    /**
     * Returns the complete path of a file, in the form: path\name.ext
     * 
     * @param path The path (folder) of the file
     * @param name The name of the file
     * @param ext The extension of the file
     * @return A String representing the complete path of a file
     */
    private String image_path(String path, String name, String ext) {
        return path + "/" + name + "." + ext;
    }

    /**
     * Get method to return an image file
     * 
     * @param f The complete path name of the image.
     * @return A BufferedImage of the supplied file path
     * @see Steganography.image_path
     */
    private BufferedImage getImage(String f) {
        BufferedImage image = null;
        File file = new File(f);

        try {
            image = ImageIO.read(file);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Image could not be read!", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return image;
    }

    /**
     * Set method to save an image file
     * 
     * @param image The image file to save
     * @param file File to save the image to
     * @param ext The extension and thus format of the file to be saved
     * @return Returns true if the save is succesful
     */
    private boolean setImage(BufferedImage image, File file, String ext) {
        try {
            file.delete(); //delete resources used by the File
            ImageIO.write(image, ext, file);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "File could not be saved!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}