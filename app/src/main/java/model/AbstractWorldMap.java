package model;

import util.Boundary;

import java.util.*;

public abstract class AbstractWorldMap implements WorldMap{
    protected final Vector2d lowerLeft = new Vector2d(0,0);
    protected final Vector2d upperRight;
    private final UUID id = UUID.randomUUID();
    protected final Parameters parameters;
    private final List<MapChangeListener> observers = new ArrayList<>();
    protected Map<Vector2d, List<Animal>> animals = new HashMap<>();
    protected Map<Vector2d, Grass> plants = new HashMap<>();


    public AbstractWorldMap(Parameters parameters) {
        this.upperRight = new Vector2d (parameters.mapWidth() - 1, parameters.mapHeight() - 1);
        this.parameters = parameters;
    }

    public void registerObserver(MapChangeListener observer){
        observers.add(observer);
    }

    public void deregisterObserver(MapChangeListener observer){
        observers.remove(observer);
    }

    protected void mapChanged(String message){
        for(MapChangeListener observer : observers){
            observer.mapChanged(this, message);
        }
    }

    @Override
    public void place(Animal animal) throws IncorrectPositionException {
        if (animal != null) {
            Vector2d position = animal.getPosition();
            if (canMoveTo(position)) {
                animals.computeIfAbsent(position, pos -> new ArrayList<Animal>())
                                .add(animal);
                mapChanged("Animal was placed at position: " + animal.getPosition());
            } else {
                throw new IncorrectPositionException(animal.getPosition());
            }
        }
    }

    @Override
    public void plant(Grass plant) throws IncorrectPositionException {
        if (plant != null) {
            Vector2d position = plant.getPosition();
            if (canMoveTo(position)) {
                plants.put(position, plant);
                mapChanged("Plant was placed at position: " + plant.getPosition());
            } else {
                throw new IncorrectPositionException(plant.getPosition());
            }
        }
    }

    @Override
    public boolean isOccupiedByPlant(Vector2d position) {
        return plants.containsKey(position);
    }

    public boolean isOccupiedByAnimal(Vector2d position) { return animals.containsKey(position); }

    @Override
    public void removeAnimal(Animal animal) {
        if(animal != null){
            Vector2d position = animal.getPosition();
            List<Animal> animalsAt = animals.get(position);

            if(animalsAt != null) {
                animalsAt.remove(animal);

                if(animalsAt.isEmpty()){
                    animals.remove(position);
                }
            }
        }
    }

    public void removePlant(Grass plant) {
        plants.remove(plant.getPosition());
        mapChanged("Plant eaten at " + plant.getPosition());
    }

    @Override
    public void removeDeadAnimals(int currentDate){
        List<Animal> allAnimals = new ArrayList<>();
        animals.values().forEach(allAnimals::addAll);

        for (Animal animal : allAnimals) {
            if (animal.isDead()) {
                animal.setDeathDate(currentDate);
                removeAnimal(animal);
            }
        }
    }

    @Override
    public void moveAllAnimals(){
        List<Animal> allAnimals = new ArrayList<>();
        for (List<Animal> animalList : animals.values()) {
            allAnimals.addAll(animalList);
        }

        for (Animal animal : allAnimals) {
            move(animal);
        }
    }


    public List<Animal> getAnimalsAt(Vector2d position) {
        List<Animal> animalsAt = new ArrayList<>(Optional.ofNullable(animals.get(position))
                .orElse(List.of()));

        Collections.shuffle(animalsAt);
        return animalsAt;
    }

    @Override
    public int numberOfPlantsOnMap(){
        return plants.size();
    }

    @Override
    public Boundary getCurrentBounds(){
        return new Boundary(lowerLeft, upperRight);
    }

    @Override
    public UUID getId() {
        return id;
    }


}
