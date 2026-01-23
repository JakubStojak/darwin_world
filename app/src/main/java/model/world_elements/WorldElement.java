package model.world_elements;

import util.Vector2d;

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