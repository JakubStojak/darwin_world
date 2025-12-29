package model;

import util.MapDirection;
import util.RandomPositionGenerator;
import java.util.*;


public class GlobeMap extends AbstractWorldMap {
    PlantGrower plantGrower = new PlantGrower(this, this.parameters);

    public GlobeMap(Parameters parameters) {
        super(parameters);
    }

    @Override
    public void move(Animal animal) {
        if (animal == null) return;

        Vector2d oldPosition = animal.getPosition();
        removeAnimal(animal);

        animal.move();

        if (animal instanceof Parasite parasite && parasite.hasHost()) {
            Vector2d hostPos = parasite.getHost().getPosition();
            Vector2d targetPos = hostPos;

            for (int i = 0; i < 8; i++) {
                Vector2d neighbor = hostPos.add(MapDirection.fromInt(i).toUnitVector());

                neighbor = validatePosition(neighbor, animal);

                if (!isOccupiedByAnimal(neighbor)) {
                    targetPos = neighbor;
                    break;
                }
            }
            animal.setPosition(targetPos);
        } else {
            animal.setPosition(validatePosition(animal.getPosition(), animal));
        }

        try {
            place(animal);
        } catch (IncorrectPositionException e) {
            System.out.println(e.getMessage());
        }

        mapChanged("Animal was moved from position: " + oldPosition + " to position: " + animal.getPosition());
    }

    @Override
    public void growPlants(int numberOfPlants) {
        plantGrower.growPlants(numberOfPlants);
    }

    public void initializeHerbivores() {
        RandomPositionGenerator randomPositionGenerator = new RandomPositionGenerator(parameters.mapWidth(), parameters.mapHeight(), parameters.startAnimalNumber());
        Random random = new Random();
        for (Vector2d animalPosition : randomPositionGenerator) {
            MapDirection randomDirection = MapDirection.fromInt(random.nextInt());
            List<Integer> randomGenes = new ArrayList<>();
            for (int i = 0; i < parameters.genomeLength(); i++) {
                randomGenes.add(random.nextInt(8));
            }

            Genome genome = new Genome(parameters, randomGenes);

            try {
                place(new Herbivore(parameters, randomDirection, animalPosition, genome));
            } catch (IncorrectPositionException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void initializeParasites() {
        RandomPositionGenerator randomPositionGenerator = new RandomPositionGenerator(parameters.mapWidth(), parameters.mapHeight(), parameters.startParasiteNumber());
        Random random = new Random();
        for (Vector2d parasitePosition : randomPositionGenerator) {
            MapDirection randomDirection = MapDirection.fromInt(random.nextInt());
            List<Integer> randomGenes = new ArrayList<>();
            for (int i = 0; i < parameters.genomeLength(); i++) {
                randomGenes.add(random.nextInt(8));
            }

            Genome genome = new Genome(parameters, randomGenes);

            try {
                place(new Parasite(parameters, randomDirection, parasitePosition, genome));
            } catch (IncorrectPositionException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public Vector2d validatePosition(Vector2d position, Animal animal) {
        if (canMoveTo(position)) {
            return position;
        } else {
            int width = parameters.mapWidth();
            int height = parameters.mapHeight();
            int x = (position.getX() + width) % width;
            int y = position.getY();
            if (position.getY() >= height || position.getY() < 0) {
                y = (y >= height) ? height - 1 : 0;
                x = (x + width / 2) % width;
                animal.oppositeDirection();

            }
            return new Vector2d(x, y);
        }

    }

    public void animalInteractions() {
        List<Animal> newborns = new ArrayList<>();

        for (Vector2d position : getAllOccupiedPositions()) {
            List<Animal> ordered = getOrderedAnimalsAt(position);
            if (ordered.isEmpty()) continue;

            handleEnergyLoss(ordered);

            handlePlantConsumption(ordered, position);

            handleParasiteFeeding(ordered);

            handleParasiteInfection(ordered);

            newborns.addAll(handleBreeding(ordered));
        }

        newborns.forEach(this::tryPlaceAnimal);
    }

    private void handleEnergyLoss(List<Animal> ordered) {
        for (Animal animal : ordered) {
            if (animal instanceof Herbivore) {
                ((Herbivore) animal).loseEnergyForDay();
            } else if (animal instanceof Parasite) {
                if (((Parasite) animal).hasHost()) {
                    ((Parasite) animal).loseEnergyWithHost();
                } else {
                    ((Parasite) animal).loseEnergyWithoutHost();
                }
            }
        }
    }

    private void handlePlantConsumption(List<Animal> animals, Vector2d position) {
        if (isOccupiedByPlant(position)) {
            Optional<Herbivore> strongestHerbivore = animals.stream()
                    .filter(a -> a instanceof Herbivore)
                    .map(a -> (Herbivore) a)
                    .findFirst();

            if (strongestHerbivore.isPresent()) {
                strongestHerbivore.get().eat();
                this.removePlant(this.plants.get(position));
            }
        }
    }

    private void handleParasiteFeeding(List<Animal> animals) {
        animals.stream()
                .filter(a -> a instanceof Parasite p && p.hasHost())
                .map(a -> (Parasite) a)
                .forEach(Parasite::consume);
    }

    private void handleParasiteInfection(List<Animal> animals) {
        List<Parasite> hungryParasites = animals.stream()
                .filter(a -> a instanceof Parasite p && !p.hasHost())
                .map(a -> (Parasite) a)
                .toList();

        List<Herbivore> potentialHosts = animals.stream()
                .filter(a -> a instanceof Herbivore)
                .map(a -> (Herbivore) a)
                .toList();

        if (!potentialHosts.isEmpty()) {
            for (Parasite p : hungryParasites) {
                p.setHost(potentialHosts.getFirst());
                p.getHost().addParasite(p);
            }
        }
    }

    private List<Animal> handleBreeding(List<Animal> animals) {
        List<Animal> newbornsAtPos = new ArrayList<>();

        List<Herbivore> herbivores = animals.stream().
                filter(a -> a instanceof Herbivore).
                map(a -> (Herbivore) a).toList();
        List<Parasite> parasites = animals.stream().
                filter(a -> a instanceof Parasite).
                map(a -> (Parasite) a).toList();

        for (int i = 0; i < herbivores.size() - 1; i += 2) {
            Herbivore h1 = herbivores.get(i);
            Herbivore h2 = herbivores.get(i + 1);
            if (h1.canReproduce(h2)) {
                try {
                    newbornsAtPos.add(h1.reproduce(h2));
                } catch (Exception ignored) {
                }
            }
        }

        for (int i = 0; i < parasites.size() - 1; i += 2) {
            Parasite p1 = parasites.get(i);
            Parasite p2 = parasites.get(i + 1);
            if (p1.canReproduce(p2)) {
                try {
                    newbornsAtPos.add(p1.reproduce(p2));
                } catch (Exception ignored) {
                }
            }
        }
        return newbornsAtPos;
    }


    private void tryPlaceAnimal(Animal animal) {
        try {
            place(animal);
        } catch (IncorrectPositionException e) {
            System.err.println("Failed to place animal: " + e.getMessage());
        }
    }

    private Set<Vector2d> getAllOccupiedPositions() {
        return animals.keySet();
    }

    @Override
    public boolean canMoveTo(Vector2d position) {
        return position.follows(lowerLeft) && position.precedes(upperRight);
    }

    public List<Animal> getOrderedAnimalsAt(Vector2d position) {
        List<Animal> list = new ArrayList<>(getAnimalsAt(position));
        list.sort(new AnimalComparator());
        return list;
    }

    @Override
    public void removeDeadAnimals(int currentDate) {
        List<Animal> allAnimals = new ArrayList<>();
        for (List<Animal> animalsAt : animals.values()) {
            allAnimals.addAll(animalsAt);
        }

        for (Animal animal : allAnimals) {
            if (animal.isDead()) {
                animal.setDeathDate(currentDate);

                if (animal instanceof Herbivore herbivore) {
                    herbivore.getParasites().forEach(Parasite::panic);
                }

                if (animal instanceof Parasite parasite) {
                    if (parasite.hasHost()) {
                        parasite.getHost().removeParasite(parasite);
                    }
                }

                removeAnimal(animal);
            }
        }
    }
}


