package model.world_elements;

import java.util.Comparator;

public class AnimalComparator implements Comparator<Animal> {
    @Override
    public int compare(Animal animal1, Animal animal2) {
        return Comparator.comparingInt(Animal::getEnergy)
                .thenComparingInt(Animal::getAge)
                .thenComparingInt(Animal::getChildrenCount)
                .reversed()
                .compare(animal1, animal2);
    }
}
