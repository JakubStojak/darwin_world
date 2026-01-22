package model;

import util.Parameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Genome {
    private final Parameters params;
    private final List<Integer> genes;
    private int currentIndex;

    public Genome(Parameters params) {
        this.params = params;
        this.genes = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < params.genomeLength(); i++) {
            this.genes.add(random.nextInt(8));
        }
    }

    public Genome(Parameters params, List<Integer> childGenes) {
        this.params = params;
        this.genes = childGenes;
    }

    public void next() {
        currentIndex = (currentIndex + 1) % genes.size();
    }

    public Integer getMove() {
        return genes.get(currentIndex);
    }

    public int getGeneAtIndex(int i) {
        return genes.get(i);
    }

    public void mutate() {
        Random random = new Random();
        int genesToMutate = random.nextInt(params.minimumMutations(), params.maximumMutations() + 1);

        for (int i = 0; i < genesToMutate; i++) {
            int randomIndex = random.nextInt(params.genomeLength());
            int randomGene = random.nextInt(8);

            genes.set(randomIndex, randomGene);
        }
    }

    public Genome createChildGenome(Animal animal1, Animal animal2) {
        Random random = new Random();

        Animal superior = animal1.getEnergy() >= animal2.getEnergy() ? animal1 : animal2;
        Animal inferior = (superior == animal1) ? animal2 : animal1;

        double ratio = (double) superior.getEnergy() / (superior.getEnergy() + inferior.getEnergy());
        int split = (int) Math.round(params.genomeLength() * ratio);

        List<Integer> childGenes = new ArrayList<>();
        int side = random.nextInt(2);

        for (int i = 0; i < params.genomeLength(); i++) {
            if (side == 0) {
                childGenes.add(i < split ? superior.getGenome().getGeneAtIndex(i) : inferior.getGenome().getGeneAtIndex(i));
            } else {
                childGenes.add(i < (params.genomeLength() - split) ? inferior.getGenome().getGeneAtIndex(i) : superior.getGenome().getGeneAtIndex(i));
            }
        }

        Genome childGenome = new Genome(params, childGenes);
        childGenome.mutate();
        return childGenome;
    }

    public int calculateDistance(Genome other) {
        int distance = 0;
        for (int i = 0; i < this.genes.size(); i++) {
            if (!this.genes.get(i).equals(other.getGeneAtIndex(i))) {
                distance++;
            }
        }
        return distance;
    }

    @Override
    public String toString() {
        return genes.toString();
    }

}
