
package jp.noids.image.scaling;


/** l */
public final class Direction {

    String text;

    public static final Direction NORTH = new Direction("N");
    public static final Direction SOUTH = new Direction("S");
    public static final Direction WEST = new Direction("W");
    public static final Direction EAST = new Direction("E");
    public static final Direction ANY = new Direction("ANY");

    private Direction(String text) {
        this.text = text;
    }

    public String toString() {
        return text;
    }
}
