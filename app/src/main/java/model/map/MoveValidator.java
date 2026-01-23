package model.map;

import util.Vector2d;
import model.world_elements.Animal;

public interface MoveValidator {

    /**
     * Adjust the target position to fit map boundaries
     *
     * @param position The position requested by the object.
     * @return The corrected position within map boundaries.
     */
    Vector2d validatePosition(Vector2d position, Animal animal);

    /**
     * Indicate if any object can move to the given position.
     *
     * @param position
     *            The position checked for the movement possibility.
     * @return True if the object can move to that position.
     */
    boolean canMoveTo(Vector2d position);
}
