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
    Vector2d position();

}