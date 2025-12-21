package model;

public class Grass implements WorldElement{
    private final Vector2d position;

    public Grass(Vector2d position) {
        this.position = position;
    }

    public Vector2d getPosition() {
        return position;
    }

    public boolean isAt(Vector2d position) {
        return position.getX() == this.position.getX() && position.getY() == this.position.getY();
    }

    @Override
    public String toString() {
        return "*";
    }
}