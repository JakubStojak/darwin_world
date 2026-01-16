package model;

import util.Boundary;
import util.Parameters;
import util.Statistics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractWorldMap implements WorldMap {
    protected final Vector2d lowerLeft = new Vector2d(0, 0);
    protected final Vector2d upperRight;
    private final UUID id = UUID.randomUUID();
    protected final Parameters parameters;
    private final List<MapChangeListener> observers = new CopyOnWriteArrayList<>();
    protected final Map<Vector2d, List<Animal>> animals = new ConcurrentHashMap<>();
    protected final Map<Vector2d, Grass> plants = new ConcurrentHashMap<>();
    protected double totalDeadAnimalsLifespan = 0;
    protected int deadAnimalsCount = 0;
    private final Map<Vector2d, Integer> grassPositions = new ConcurrentHashMap<>();

    public AbstractWorldMap(Parameters parameters) {
        this.upperRight = new Vector2d(parameters.mapWidth() - 1, parameters.mapHeight() - 1);
        this.parameters = parameters;
    }

    public void registerObserver(MapChangeListener observer) {
        observers.add(observer);
    }

    public void deregisterObserver(MapChangeListener observer) {
        observers.remove(observer);
    }

    protected void mapChanged(String message) {
        for (MapChangeListener observer : observers) {
            observer.mapChanged(this, message);
        }
    }

    @Override
    public void place(Animal animal) throws IncorrectPositionException {
        if (animal != null) {
            Vector2d position = animal.getPosition();
            if (canMoveTo(position)) {
                animals.computeIfAbsent(position, pos -> Collections.synchronizedList(new ArrayList<>()))
                        .add(animal);
            } else {
                throw new IncorrectPositionException(animal.getPosition());
            }
        }
    }

    @Override
    public int getAnimalCount() {
        return animals.values().stream()
                .mapToInt(List::size)
                .sum();
    }

    @Override
    public void plant(Grass plant) throws IncorrectPositionException {
        if (plant != null) {
            Vector2d position = plant.getPosition();
            grassPositions.merge(position, 1, Integer::sum);

            if (canMoveTo(position)) {
                plants.put(position, plant);
            } else {
                throw new IncorrectPositionException(plant.getPosition());
            }
        }
    }

    @Override
    public boolean isOccupiedByPlant(Vector2d position) {
        return plants.containsKey(position);
    }

    public boolean isOccupiedByAnimal(Vector2d position) {
        return animals.containsKey(position);
    }

    @Override
    public void removeAnimal(Animal animal) {
        if (animal == null) return;
        Vector2d position = animal.getPosition();

        animals.compute(position, (key, list) -> {
            if (list != null) {
                list.remove(animal);
                return list.isEmpty() ? null : list;
            }
            return null;
        });
    }

    public void removePlant(Grass plant) {
        plants.remove(plant.getPosition());
    }

    @Override
    public void removeDeadAnimals(int currentDate) {
        List<Animal> allAnimals = new ArrayList<>();
        animals.values().forEach(allAnimals::addAll);

        for (Animal animal : allAnimals) {
            if (animal.isDead()) {
                animal.setDeathDate(currentDate);
                deadAnimalsCount++;
                totalDeadAnimalsLifespan += animal.getAge();
                removeAnimal(animal);
            }
        }
    }

    @Override
    public void moveAllAnimals() {
        List<Animal> allAnimals = new ArrayList<>();
        for (List<Animal> animalList : animals.values()) {
            synchronized (animalList) {
                allAnimals.addAll(animalList);
            }
        }

        for (Animal animal : allAnimals) {
            move(animal);
        }
    }

    public List<Animal> getAnimalsAt(Vector2d position) {
        return new ArrayList<>(Optional.ofNullable(animals.get(position))
                .orElse(List.of()));
    }

    @Override
    public int numberOfPlantsOnMap() {
        return plants.size();
    }

    @Override
    public Boundary getCurrentBounds() {
        return new Boundary(lowerLeft, upperRight);
    }

    @Override
    public UUID getId() {
        return id;
    }

    public Statistics getStatistics() {
        List<Animal> allAnimals = new ArrayList<>();

        for (List<Animal> animalList : animals.values()) {
            synchronized (animalList) {
                allAnimals.addAll(animalList);
            }
        }

        int animalCount = allAnimals.size();
        int plantCount = plants.size();

        Set<Vector2d> occupiedPositions = new HashSet<>();
        occupiedPositions.addAll(animals.keySet());
        occupiedPositions.addAll(plants.keySet());

        int freeFields = (parameters.mapWidth() * parameters.mapHeight()) - occupiedPositions.size();

        double avgEnergy = allAnimals.stream().mapToInt(Animal::getEnergy).average().orElse(0);
        double avgLifespan = deadAnimalsCount > 0 ? totalDeadAnimalsLifespan / deadAnimalsCount : 0;
        double avgChildren = allAnimals.stream().mapToInt(Animal::getChildrenCount).average().orElse(0);

        return new Statistics(animalCount, plantCount, freeFields, avgEnergy, avgLifespan, avgChildren);
    }

    public List<Animal> getDominantFamilyGroup(double percentage) {
        List<Animal> allAnimals = new ArrayList<>();
        for (List<Animal> animalList : animals.values()) {
            synchronized (animalList) {
                allAnimals.addAll(animalList);
            }
        }

        int threshold = (int) Math.ceil(parameters.genomeLength() * percentage);

        if (allAnimals.isEmpty()) return Collections.emptyList();

        Map<Genome, List<Animal>> families = new HashMap<>();

        for (Animal animal : allAnimals) {
            Genome currentGenome = animal.getGenome();

            Map.Entry<Genome, List<Animal>> closestEntry = null;
            double minDistance = Double.MAX_VALUE;

            for (Map.Entry<Genome, List<Animal>> entry : families.entrySet()) {
                double dist = currentGenome.calculateDistance(entry.getKey());
                if (dist <= threshold && dist < minDistance) {
                    minDistance = dist;
                    closestEntry = entry;
                }
            }

            if (closestEntry != null) {
                closestEntry.getValue().add(animal);
            } else {
                List<Animal> newFamily = new ArrayList<>();
                newFamily.add(animal);
                families.put(currentGenome, newFamily);
            }
        }

        return families.values().stream()
                .max(Comparator.comparingInt(List::size))
                .orElse(Collections.emptyList());
    }

    public List<Vector2d> getDominantPositions() {
        List<Vector2d> sortedPositions = grassPositions.keySet().stream()
                .sorted(Comparator.comparingInt(grassPositions::get))
                .toList();

        int actualGrassFields = sortedPositions.size();
        if (actualGrassFields == 0) return Collections.emptyList();

        int countToTake = Math.max(1, (int) (actualGrassFields * 0.1));

        return sortedPositions.subList(actualGrassFields - countToTake, actualGrassFields);


    }
}