package model.file_management;

import model.map.AbstractWorldMap;
import model.map.MapChangeListener;
import model.map.WorldMap;
import util.Statistics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CsvStatsLogger implements MapChangeListener {
    private static final String STATS_DIRECTORY = "stats";

    private final Path statsFolderPath;
    private final String currentFileName;

    public CsvStatsLogger(String mapId) {
        Path localPath = Paths.get(STATS_DIRECTORY);
        Path modulePath = Paths.get("app", STATS_DIRECTORY);

        if (Files.exists(Paths.get("app"))) {
            this.statsFolderPath = modulePath;
        } else {
            this.statsFolderPath = localPath;
        }

        try {
            if (!Files.exists(statsFolderPath)) {
                Files.createDirectories(statsFolderPath);
            }
        } catch (IOException e) {
            System.err.println("Nie udało się utworzyć katologu statystyk: " + e.getMessage());
        }

        this.currentFileName = "stats_" + mapId + ".csv";
        createHeader();
    }

    private void createHeader() {
        Path fullPath = statsFolderPath.resolve(currentFileName);
        if (!Files.exists(fullPath)) {
            try (BufferedWriter writer = Files.newBufferedWriter(fullPath, StandardOpenOption.CREATE)) {
                writer.write("Day;AnimalCount;PlantCount;FreeFields;AvgEnergy;AvgLifespan;AvgChildren;TopGenotype");
                writer.newLine();
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

            Path fullPath = statsFolderPath.resolve(currentFileName);

            try (BufferedWriter writer = Files.newBufferedWriter(fullPath, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                String line = String.format("%s;%d;%d;%d;%.2f;%.2f;%.2f;%s",
                        day, stats.animalCount(), stats.plantCount(), stats.freeFieldsCount(), stats.averageEnergy(), stats.averageLifespan(), stats.averageChildren(), stats.topGenotype()
                );

                writer.write(line);
                writer.newLine();
            } catch (IOException e) {
                System.err.println("Błąd zapisu: " + e.getMessage());
            }
        }
    }

    private String extractDay(String message) {
        return message.replace("Dzień: ", "");
    }
}
