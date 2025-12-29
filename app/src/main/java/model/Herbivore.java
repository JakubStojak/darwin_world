package model;

import util.MapDirection;
import java.util.*;

public class Herbivore extends Animal {
    private final List<Parasite> parasites = new ArrayList<>();

    public Herbivore(Parameters params, MapDirection direction, Vector2d position, Genome genome) {
        super(params, direction, position, genome);
    }

    public Herbivore reproduce(Animal other) throws UnableToBreedException {
        if (!canReproduce(other)) {
            throw new UnableToBreedException("Animals " + this.getId() + " and " + other.getId() + " unable to breed.");
        }
        Genome childGenome = this.getGenome().createChildGenome(this, other);
        this.loseEnergyForBreed();
        other.loseEnergyForBreed();
        return new Herbivore(params, MapDirection.fromInt(childGenome.getMove()), this.getPosition(), childGenome);
    }

    @Override
    public void move() {
        MapDirection moveDirection = MapDirection.fromInt(this.getGenome().getMove());
        Vector2d newPosition = this.position.add(moveDirection.toUnitVector());
        this.setPosition(newPosition);
    }

    public void addParasite(Parasite parasite) {
        if (!parasites.contains(parasite)) {
            this.parasites.add(parasite);
        }
    }

    public void removeParasite(Parasite parasite) {
        this.parasites.remove(parasite);
    }

    public List<Parasite> getParasites() {
        return Collections.unmodifiableList(parasites);
    }

    public boolean hasParasites() {
        return !parasites.isEmpty();
    }

    public void eat() {
        this.energy += params.energyPerGrass();
    }

    public void loseEnergyForDay() {
        this.energy -= params.energyLossPerDay();
        this.energy -= (parasites.size() * params.hostEnergyLossPerParasite());
    }
}