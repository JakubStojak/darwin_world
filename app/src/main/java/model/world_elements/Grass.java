package model.world_elements;

import util.Vector2d;

public record Grass(Vector2d position) implements WorldElement {

    @Override
    public String toString() {
        return "*";
    }
}