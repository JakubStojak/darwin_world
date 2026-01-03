package model;

import util.Parameters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PlantGrower {
    private final WorldMap map;
    private final double[] baseRowSpawnChances;
    private final Random random = new Random();
    private final Parameters parameters;

    public PlantGrower(WorldMap map, Parameters parameters) {
        this.map = map;
        this.parameters = parameters;
        this.baseRowSpawnChances = new double[parameters.mapHeight()];
        initializeGrowthProbabilities();
    }

    private void initializeGrowthProbabilities() {
        int mapHeight = parameters.mapHeight();
        double equatorIndex = mapHeight / 2.0;
        double plantsDispersion = mapHeight / 6.0;

        for (int row = 0; row < mapHeight; row++) {
            double exponent = -Math.pow(row - equatorIndex, 2) / (2 * Math.pow(plantsDispersion, 2));
            baseRowSpawnChances[row] = Math.exp(exponent) + 0.05;
        }
    }

    public void growPlants(int requestedPlantCount) {
        double[] currentTurnChances = baseRowSpawnChances.clone();
        double totalChanceSum = Arrays.stream(currentTurnChances).sum();

        int successfullyPlantedCount = 0;
        int totalEmptyFields = ((parameters.mapWidth() * parameters.mapHeight())  - map.numberOfPlantsOnMap());
        int targetPlantCount = Math.min(requestedPlantCount, totalEmptyFields);

        while (successfullyPlantedCount < targetPlantCount) {

            int targetRowIndex = selectRow(currentTurnChances, totalChanceSum);
            List<Integer> availableSlots = findEmptySlotsInRow(targetRowIndex);

            if (!availableSlots.isEmpty()) {
                int randomSlotIndex = random.nextInt(availableSlots.size());
                int targetColumnIndex = availableSlots.get(randomSlotIndex);

                try {
                    map.plant(new Grass(new Vector2d(targetColumnIndex, targetRowIndex)));
                    successfullyPlantedCount++;
                } catch (IncorrectPositionException e) {
                    System.out.println(e.getMessage());
                }


            } else {
                totalChanceSum -= currentTurnChances[targetRowIndex];
                currentTurnChances[targetRowIndex] = 0.0;
            }
        }
    }


    private int selectRow(double[] chances, double totalSum) {
        double randomThreshold = random.nextDouble() * totalSum;
        double cumulativeSum = 0.0;

        for (int i = 0; i < chances.length; i++) {
            cumulativeSum += chances[i];

            if (cumulativeSum >= randomThreshold) {
                return i;
            }
        }
        return chances.length - 1;
    }


    private List<Integer> findEmptySlotsInRow(int rowIndex) {
        List<Integer> emptySlots = new ArrayList<>();
        int mapWidth = parameters.mapWidth();

        for (int col = 0; col < mapWidth; col++) {
            if (!map.isOccupiedByPlant(new Vector2d(col, rowIndex))) {
                emptySlots.add(col);
            }
        }
        return emptySlots;
    }

}