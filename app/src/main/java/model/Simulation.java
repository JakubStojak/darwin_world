package model;

import model.map.GlobeMap;
import util.Parameters;

public class Simulation implements Runnable {
    private final Parameters parameters;
    private final GlobeMap map;
    private volatile boolean isPaused = false;
    private volatile boolean isRunning = true;

    private int dayCount = 0;
    private static final int SIMULATION_DELAY = 200;

    public Simulation(Parameters parameters) {
        this.parameters = parameters;
        this.map = new GlobeMap(parameters);

        this.map.initializeHerbivores();
        this.map.initializeParasites();

        this.map.growPlants(parameters.startGrassNumber());
    }


    public GlobeMap getMap() {
        return map;
    }


    @Override
    public void run() {
        while (isRunning) {
            if (isPaused) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
                continue;
            }
            try {
                simulateDay();
                if (map.getAnimalCount() == 0) {
                    map.mapChanged("GAME OVER");
                    stop();
                    break;
                }
                Thread.sleep(SIMULATION_DELAY);
            } catch (InterruptedException e) {
                System.out.println("Symulacja przerwana");
                break;
            }
        }
    }

    private void simulateDay() {
        dayCount++;
        map.removeDeadAnimals(dayCount);
        map.moveAllAnimals();
        map.animalInteractions();
        map.growPlants(parameters.newGrassPerDay());
        map.mapChanged("Dzień: " + dayCount);
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public void stop() {
        isRunning = false;
    }
}