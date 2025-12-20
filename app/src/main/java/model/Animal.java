package model;

import util.MapDirection;

import java.util.UUID;

public class Animal implements WorldElement{
    private final Parameters params;
    private final UUID id = UUID.randomUUID();
    private MapDirection direction;
    private Vector2d position;
    private int energy;
    private final Genome genome;
    private Parasite parasite;
    int age = 0;
    int childrenCount = 0;

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

    public boolean canReproduce(Animal animal1, Animal animal2) {
        if (animal1.equals(animal2)) {
            return false;
        }
        if (animal1.position != animal2.position) {
            return false;
        }
        return animal1.energy > params.saturationEnergy()
                && animal2.energy > params.saturationEnergy();
    }

    public Animal reproduce(Animal animal1, Animal animal2) throws UnableToBreedException {
        if (!canReproduce(animal1, animal2)) {
            throw new UnableToBreedException("Animal:  " + animal1.getId()
                    + "and Animal: " + animal2.getId() + "unable to breed.");
        }
        Genome childGenome = animal1.getGenome().createChildGenome(animal1, animal2);
        return new Animal(params, MapDirection.fromInt(childGenome.getMove()), animal1.getPosition(), childGenome);

    }
}

