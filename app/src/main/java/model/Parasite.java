package model;

import util.MapDirection;
import util.Parameters;

import static util.MapDirection.*;

public class Parasite extends Animal {
    private Herbivore host;

    public Parasite(Parameters params, MapDirection direction, Vector2d position, Genome genome) {
        super(params, direction, position, genome);
    }

    @Override
    public void move() {
        if (!hasHost()) {
            MapDirection moveDirection = MapDirection.fromInt(this.genome.getMove());
            this.position = this.position.add(moveDirection.toUnitVector());
            this.genome.next();
            this.setDirection(moveDirection);
        }
    }

    public void setEnergy() {
        this.energy = params.startParasiteEnergy();
    }

    public void consume() {
        this.energy += params.hostEnergyLossPerParasite();
    }

    public void loseEnergyWithoutHost() {
        this.energy -= params.energyLossForParasiteWithoutHost();
    }

    public void loseEnergyWithHost() {
        this.energy -= params.energyLossPerDay();
    }

    public void setHost(Herbivore newHost) {
        if (this.host == null) {
            this.host = newHost;
        }
    }

    public void panic() {
        this.host = null;
    }

    public boolean hasHost() {
        return this.host != null;
    }

    public Herbivore getHost() {
        return host;
    }

    public Parasite reproduce(Animal other) {
        Genome childGenome = this.getGenome().createChildGenome(this, other);
        this.loseEnergyForBreed();
        other.loseEnergyForBreed();
        this.increaseChildrenCount();
        other.increaseChildrenCount();
        Parasite child = new Parasite(params, fromInt(childGenome.getMove()), this.position(), childGenome);
        child.setEnergy();
        return child;
    }
}