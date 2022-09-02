
package vavix.awt.image.resample.enlarge.noids.image.scaling;


/** l */
public enum Direction {

    NORTH("N"),
    SOUTH("S"),
    WEST("W"),
    EAST("E"),
    ANY("ANY");

    final String text;

    Direction(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }
}
