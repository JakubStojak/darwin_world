package model;

import util.MapDirection;
import util.RandomPositionGenerator;

import java.util.*;
import java.util.stream.Collectors;


public class GlobeMap extends AbstractWorldMap{
    PlantGrower plantGrower = new PlantGrower(this,this.parameters);


    public GlobeMap(Parameters parameters) {
        super(parameters);
    }

    @Override
    public void move(Animal animal) {
        if (animal != null) {
            Vector2d oldPosition = animal.getPosition();
            remove(animal);
            animal.move();
            try {
                place(animal);
            } catch (IncorrectPositionException e) {
                System.out.println(e.getMessage());
            }
            mapChanged("Animal was moved from position: " + oldPosition + " to position: " + animal.getPosition());
        }

    }

    @Override
    public void growPlants(int numberOfPlants) {
        plantGrower.growPlants(numberOfPlants);
    }

    @Override
    public void initializeAnimals(){
        RandomPositionGenerator randomPositionGenerator = new RandomPositionGenerator(parameters.mapWidth(), parameters.mapHeight(), parameters.startAnimalNumber());
        Random random = new Random();
        for (Vector2d animalPosition : randomPositionGenerator) {
            MapDirection randomDirection = MapDirection.fromInt(random.nextInt());
            List<Integer> randomGenes = new ArrayList<>();
            for (int i = 0; i< parameters.genomeLength(); i++) {
                randomGenes.add(random.nextInt(8));
            }

            Genome genome = new Genome(parameters, randomGenes);

            try {
                place(new Animal(parameters, randomDirection, animalPosition, genome));
            } catch (IncorrectPositionException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public Vector2d validatePosition(Vector2d position, Animal animal) {
        if (canMoveTo(position)){
            return position;
        }else{
            int width = parameters.mapWidth();
            int height = parameters.mapHeight();
            int x = (position.getX() + width) % width;
            int y = position.getY();
            if (position.getY()>=height || position.getY()<0) {
                y = (y >= height) ? height - 1 : 0;
                x = (x + width / 2) % width;
                animal.oppositeDirection();

            }
            return new Vector2d(x,y);
        }

    }

    public void animalsInteractions() {
        ArrayList<Animal> newborns = new ArrayList<>();
        for (int x = 0; x<parameters.mapWidth(); x++){
            for (int y = 0; y<parameters.mapHeight(); y++){
                List<Animal> orderedAnimals = getOrderedAnimalsAt(new Vector2d(x,y));
                if (!orderedAnimals.isEmpty()) {
                    Vector2d currentPosition = new Vector2d(x,y);
                    Animal firstAnimal = orderedAnimals.getFirst();
                    if (isOccupiedByPlant(currentPosition)) {
                        firstAnimal.eat();
                        plants.remove(currentPosition);
                        mapChanged("Plant eaten at " + currentPosition);
                    }

                    for (int i = 0; i<orderedAnimals.size() - 1; i++){
                        Animal parent1 = orderedAnimals.get(i);
                        Animal parent2 = orderedAnimals.get(i+1);

                        if(!parent1.canReproduce(parent2)){
                            break;
                        } else{
                            try {
                                newborns.add(parent1.reproduce(parent2));
                            } catch (UnableToBreedException e) {
                                System.out.println(e.getMessage());
                            }
                        }
                    }
                }
            }
        }
        for (Animal newborn : newborns){
            try {
                place(newborn);
            } catch (IncorrectPositionException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.follows(lowerLeft) && position.precedes(upperRight);
    }

    public List<Animal> getOrderedAnimalsAt(Vector2d position){
        return getAnimalsAt(position).stream()
                .sorted(new AnimalComparator())
                .toList();
    }
}
