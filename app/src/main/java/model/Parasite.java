package model;

public class Parasite implements WorldElement {
    private Vector2d position;

    @Override
    public Vector2d getPosition() {
        return null;
    }

    @Override
    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }
}
