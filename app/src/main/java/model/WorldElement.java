package model;
public interface WorldElement {
    /**
     * Get the string representation
     *
     * @return the String representation of the element
     */
    String toString();

    /**
     * Get the position of the element
     *
     * @return the position of the element
     */
    Vector2d getPosition();

    /**
     * Check whether the element is at a given position
     *
     * @param position which we are checking
     * @return True if the element is at a given position, False otherwise
     */

    boolean isAt(Vector2d position);

}