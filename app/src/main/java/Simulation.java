import model.GlobeMap;
import model.Parameters;

public class Simulation implements Runnable{
    private final Parameters parameters;
    private volatile boolean isPaused = false;
    private volatile boolean isRunning = true;
    private final GlobeMap map;
    private int dayCount = 0;

    public Simulation(Parameters parameters){
        this.parameters = parameters;
        map = new GlobeMap(parameters);
        map.initializeHerbivores();
        map.initializeParasites();
    }

    @Override
    public void run() {
        while(isRunning) {
            if(!isPaused){
                try{
                    dayCount++;
                    simulateDay();
                    Thread.sleep(200);
                } catch(InterruptedException e) {
                    System.out.println("Simulation has been interrupted");
                    break;
                }
            } else {
                try{
                    Thread.sleep(100);
                } catch(InterruptedException e) {
                    break;
                }
            }
        }

    }

    public void simulateDay() {
        map.removeDeadAnimals(dayCount);
        map.moveAllAnimals();
        map.animalInteractions();
        map.growPlants(parameters.newGrassPerDay());
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused=false;
    }

    public void stop() {
        isRunning=false;
    }
}
