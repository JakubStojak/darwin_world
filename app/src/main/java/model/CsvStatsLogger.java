package model;

import util.Statistics;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

public class CsvStatsLogger implements MapChangeListener {
    private final String fileName;

    public CsvStatsLogger(String mapId) {
        this.fileName = "stats_" + mapId + ".csv";
        createHeader();
    }

    private void createHeader() {
        File file = new File(fileName);
        if (!file.exists()) {
            try (FileWriter writer = new FileWriter(fileName, true)) {
                writer.write("Day;AnimalCount;PlantCount;FreeFields;AvgEnergy;AvgLifespan;AvgChildren;TopGenotype");
                writer.write(System.lineSeparator());
            } catch (IOException e) {
                System.err.println("Nie udało się utworzyć nagłówka:" + e.getMessage());
            }
        }
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        if (message.equals("GAME OVER")) return;

        if (worldMap instanceof AbstractWorldMap abstractMap) {
            Statistics stats = abstractMap.getStatistics();
            String day = extractDay(message);

            try (FileWriter writer = new FileWriter(fileName, true)) {
                String line = String.format("%s;%d;%d;%d;%.2f;%.2f;%.2f;%s\n",
                        day, stats.animalCount(), stats.plantCount(), stats.freeFieldsCount(), stats.averageEnergy(), stats.averageLifespan(), stats.averageChildren(), stats.topGenotype()
                );

                writer.write(line);
            } catch (IOException e) {
                System.err.println("Błąd zapisu: " + e.getMessage());
            }
        }
    }

    private String extractDay(String message) {
        return message.replace("Dzień: ", "");
    }
}
