package model.map;

import model.world_elements.Animal;
import model.world_elements.Grass;
import util.Boundary;
import util.Vector2d;

import java.util.UUID;

public interface WorldMap extends MoveValidator {
    /**
     * Place a new animal on the map.
     * @param animal The animal to be placed on the map.
     * @throws IncorrectPositionException if the animal position is invalid
     */
    void place(Animal animal) throws IncorrectPositionException;


    /**
     * Place a new plant on the map.
     * @param plant The plant to be placed on the map.
     * @throws IncorrectPositionException if the plant position is invalid
     */
    void plant(Grass plant) throws IncorrectPositionException;

    /**
     * Check if plant occupies the position.
     * @return if the position is occupied
     */
    boolean isOccupiedByPlant(Vector2d position);

    /**
     * Remove animal from the map.
     * @param animal The animal to be removed from the map.
     */
    void removeAnimal(Animal animal);

    /**
     * Remove plant from the map
     * @param plant The animal to be removed from the map.
     */
    void removePlant(Grass plant);


    /**
     * Moves an animal (if it is present on the map).
     * If the move is not possible, this method has no effect.
     */

    void move(Animal animal);

    /**
     * Moves all animals on the map.
     */

    void moveAllAnimals();

    /**
     * Remove animals with energy lower than 0.
     * @param date The date of the removal.
     */
    void removeDeadAnimals(int date);

    /**
     * Handle plant growth.
     * @param numberOfPlants Number of plants to grow
     */
    void growPlants(int numberOfPlants);

    /**
     * Put initial herbivores on the map.
     */
    void initializeHerbivores();

    /**
     * Put initial parasites on the map.
     */
    void initializeParasites();

    /**
     * Count plants on the map.
     * @return Number of plants on the map.
     */
    int numberOfPlantsOnMap();

    /**
     * Get the grid's height and width.
     * @return Boundary(width, height).
     */
    Boundary getCurrentBounds();

    /**
     * Get the count of all animals.
     * @return number of all animals on the map.
     */
    int getAnimalCount();

    /**
     * Get the map's UUID.
     * @return map's UUID.
     */
    UUID getId();

}
