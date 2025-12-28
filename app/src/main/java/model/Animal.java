package model;

import util.MapDirection;

import java.util.UUID;

public class Animal implements WorldElement {
    private final Parameters params;
    private final UUID id = UUID.randomUUID();
    private MapDirection direction;
    private Vector2d position;
    private int energy;
    private final Genome genome;
    private Parasite parasite;
    private int age = 0;
    private int childrenCount = 0;
    private int deathDate;

    public Animal(Parameters params, MapDirection direction, Vector2d position, Genome genome) {
        this.params = params;
        this.direction = direction;
        this.position = position;
        this.energy = params.startAnimalEnergy();
        this.genome = genome;
    }

    public MapDirection getDirection() {
        return direction;
    }

    public void setDirection(MapDirection direction) {
        this.direction = direction;
    }

    public Genome getGenome() {
        return genome;
    }

    public int getEnergy() {
        return energy;
    }

    public int getChildrenCount() {
        return childrenCount;
    }

    public int getAge() {
        return age;
    }

    public int getDeathDate() {
        return deathDate;
    }

    public void setDeathDate(int date) {
        deathDate = date;
    }

    public Vector2d getPosition() {
        return position;
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }

    public UUID getId() {
        return id;
    }

    public Boolean isDead() {
        return energy <= 0;
    }

    @Override
    public String toString() {
        return direction.toString();
    }

    public boolean isAt(Vector2d position) {
        return this.position.equals(position);
    }

    public void turn() {
        this.direction = this.direction.rotate(this.genome.getMove());
        this.energy -= params.energyLossPerDay();
    }

    public boolean canReproduce(Animal other) {
        if (this.equals(other)) {
            return false;
        }
        if (this.position != other.position) {
            return false;
        }
        return this.energy > params.saturationEnergy()
                && other.energy > params.saturationEnergy();
    }

    public Animal reproduce(Animal other) throws UnableToBreedException {
        if (!canReproduce(other)) {
            throw new UnableToBreedException("Animal:  " + this.getId()
                    + "and Animal: " + other.getId() + "unable to breed.");
        }
        Genome childGenome = this.getGenome().createChildGenome(this, other);
        return new Animal(params, MapDirection.fromInt(childGenome.getMove()), this.getPosition(), childGenome);

    }

    public void oppositeDirection() {
        direction = direction.rotate(4);
    }

    public void move(){

    }

    public void eat() {

    }
}

