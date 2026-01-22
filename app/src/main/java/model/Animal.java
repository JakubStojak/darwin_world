package model;

import util.MapDirection;
import util.Parameters;

import java.util.UUID;

public abstract class Animal implements WorldElement {
    protected final Parameters params;
    protected final UUID id = UUID.randomUUID();
    protected MapDirection direction;
    protected Vector2d position;
    protected int energy;
    protected final Genome genome;
    protected int age = 0;
    protected int childrenCount = 0;
    protected int deathDate;

    public Animal(Parameters params, MapDirection direction, Vector2d position, Genome genome) {
        this.params = params;
        this.direction = direction;
        this.position = position;
        this.genome = genome;
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

    public void setDeathDate(int date) {
        deathDate = date;
    }

    public Vector2d position() {
        return position;
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }


    public Boolean isDead() {
        return energy <= 0;
    }

    @Override
    public String toString() {
        return direction.toString();
    }

    public boolean canReproduce(Animal other) {
        if (this.equals(other)) {
            return false;
        }
        if (this.getClass() != other.getClass()) {
            return false;
        }
        if (!this.position.equals(other.position)) {
            return false;
        }
        return this.energy > params.saturationEnergy()
                && other.energy > params.saturationEnergy();
    }

    public void oppositeDirection() {
        direction = direction.rotate(4);
    }

    public void move() {
    }

    public void loseEnergyForBreed() {
        this.energy -= params.saturationEnergy();
    }

    public void increaseChildrenCount() {
        this.childrenCount++;
    }

    public abstract <T extends Animal> Animal reproduce(T parent2);
}

