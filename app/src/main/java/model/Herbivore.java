package model;

import util.MapDirection;
import util.Parameters;

import java.util.*;

public class Herbivore extends Animal {
    private final List<Parasite> parasites = new ArrayList<>();

    public Herbivore(Parameters params, MapDirection direction, Vector2d position, Genome genome) {
        super(params, direction, position, genome);
    }

    public Herbivore reproduce(Animal other) {
        Genome childGenome = this.getGenome().createChildGenome(this, other);
        this.loseEnergyForBreed();
        other.loseEnergyForBreed();
        this.increaseChildrenCount();
        other.increaseChildrenCount();
        Herbivore child = new Herbivore(params, MapDirection.fromInt(childGenome.getMove()), this.position(), childGenome);
        child.setEnergy();
        return child;
    }

    public void setEnergy() {
        this.energy = params.startAnimalEnergy();
    }

    @Override
    public void move() {
        if (isInfected()) {
            Random random = new Random();
            int chanceToMove = random.nextInt(parasites.size());
            if (chanceToMove == 0) {
                MapDirection moveDirection = MapDirection.fromInt(this.getGenome().getMove());
                this.genome.next();
                Vector2d newPosition = this.position.add(moveDirection.toUnitVector());
                this.setPosition(newPosition);
            }
        }
        else {
            MapDirection moveDirection = MapDirection.fromInt(this.getGenome().getMove());
            this.genome.next();
            Vector2d newPosition = this.position.add(moveDirection.toUnitVector());
            this.setPosition(newPosition);
        }
    }

    public void addParasite(Parasite parasite) {
        if (!parasites.contains(parasite)) {
            this.parasites.add(parasite);
        }
    }

    public boolean isInfected() {
        return !parasites.isEmpty();
    }

    public void removeParasite(Parasite parasite) {
        this.parasites.remove(parasite);
    }

    public List<Parasite> getParasites() {
        return Collections.unmodifiableList(parasites);
    }

    public void eat() {
        this.energy += params.energyPerGrass();
    }

    public void loseEnergyForDay() {
        this.energy -= params.energyLossPerDay();
        this.energy -= (parasites.size() * params.hostEnergyLossPerParasite());
    }
}