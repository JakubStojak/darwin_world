package presenter;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import model.*;
import model.file_management.CsvStatsLogger;
import model.map.AbstractWorldMap;
import model.map.MapChangeListener;
import model.map.WorldMap;
import model.world_elements.Animal;
import model.world_elements.Herbivore;
import model.world_elements.Parasite;
import util.*;

import java.util.*;

public class SimulationPresenter implements MapChangeListener {

    @FXML
    private Canvas mapGrid;
    @FXML
    private Label infoLabel;
    @FXML
    private Label statsLabel;
    @FXML
    private LineChart<Number, Number> statChart;

    private Simulation simulation;
    private Parameters parameters;

    private StatType trackedStat;
    private XYChart.Series<Number, Number> series;
    private int dayCounter = 0;
    private Set<Animal> dominantAnimals = new HashSet<>();
    private Set<Vector2d> dominantPositions = new HashSet<>();
    private final float GENOME_PERCENTAGE = 0.7F;

    public void setTrackedStat(StatType statType) {
        this.trackedStat = statType;
    }

    public void startSimulation(Parameters params) {
        this.parameters = params;
        this.simulation = new Simulation(params);
        AbstractWorldMap map = simulation.getMap();

        map.registerObserver(this);

        CsvStatsLogger csvLogger = new CsvStatsLogger(map.getId().toString());
        map.registerObserver(csvLogger);

        series = new XYChart.Series<>();
        series.setName(trackedStat.toString());
        statChart.getData().add(series);
        statChart.setAnimated(false);

        drawMap(map);

        Thread thread = new Thread(simulation);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        Platform.runLater(() -> {
            drawMap(worldMap);
            if (worldMap instanceof AbstractWorldMap abstractMap) {
                this.dominantAnimals = new HashSet<>(abstractMap.getDominantFamilyGroup(GENOME_PERCENTAGE));
                this.dominantPositions = new HashSet<>(abstractMap.getDominantPositions());
            }
            updateStatistics(worldMap);
            if ("GAME OVER".equals(message)) {
                infoLabel.setText("Koniec symulacji.");
                showGameOverAlert();
            } else {
                infoLabel.setText(message);
            }
        });
    }

    @FXML
    public void onPauseClicked() {
        if (simulation != null) simulation.pause();
    }

    @FXML
    public void onResumeClicked() {
        if (simulation != null) simulation.resume();
    }

    private void showGameOverAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Koniec");
        alert.setHeaderText("Koniec symulacji");
        alert.setContentText("Brak zwierząt na mapie.");
        alert.showAndWait();
    }

    private void drawMap(WorldMap map) {
        GraphicsContext gc = mapGrid.getGraphicsContext2D();
        gc.setFill(Color.GRAY);
        gc.fillRect(0, 0, mapGrid.getWidth(), mapGrid.getHeight());

        if (map == null) return;

        Boundary bounds = map.getCurrentBounds();
        double gridWidth = (bounds.upperRight().getX() - bounds.lowerLeft().getX() + 1);
        double gridHeight = (bounds.upperRight().getY() - bounds.lowerLeft().getY() + 1);
        double canvasWidth = mapGrid.getWidth();
        double canvasHeight = mapGrid.getHeight();

        double cellSize = Math.min(canvasWidth / gridWidth, canvasHeight / gridHeight);

        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, gridWidth * cellSize, gridHeight * cellSize);

        int minX = bounds.lowerLeft().getX();
        int maxX = bounds.upperRight().getX();
        int minY = bounds.lowerLeft().getY();
        int maxY = bounds.upperRight().getY();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Vector2d position = new Vector2d(x, y);
                double drawX = (x - minX) * cellSize;
                double drawY = (maxY - y) * cellSize;

                if (map.isOccupiedByPlant(position)) {
                    gc.setFill(Color.MEDIUMSEAGREEN);
                    gc.fillRect(drawX, drawY, cellSize, cellSize);
                }
                if (dominantPositions.contains(position)) {
                    gc.setStroke(Color.BLACK);
                    gc.setLineWidth(1);
                    gc.strokeRect(drawX + 1.5, drawY + 1.5, cellSize - 3, cellSize - 3);
                }

                if (map instanceof AbstractWorldMap abstractMap) {
                    List<Animal> animalsAtPos = abstractMap.getAnimalsAt(position);
                    if (!animalsAtPos.isEmpty()) {
                        Animal animalToDraw = animalsAtPos.getFirst();
                        boolean isDominant = dominantAnimals.contains(animalToDraw);
                        drawAnimalWithEnergyBar(gc, animalToDraw, drawX, drawY, cellSize, isDominant);
                    }
                }
            }
        }

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.5);
        for (int x = 0; x <= (maxX - minX + 1); x++) {
            gc.strokeLine(x * cellSize, 0, x * cellSize, gridHeight * cellSize);
        }
        for (int y = 0; y <= (maxY - minY + 1); y++) {
            gc.strokeLine(0, y * cellSize, gridWidth * cellSize, y * cellSize);
        }
    }

    private void drawAnimalWithEnergyBar(GraphicsContext gc, Animal animal, double x, double y, double cellSize, boolean isDominant) {
        double animalSize = cellSize * 0.6;
        double barWidth = cellSize * 0.8;
        double barHeight = cellSize * 0.15;

        double dotOffset = (cellSize - animalSize) / 2;
        double barOffsetX = (cellSize - barWidth) / 2;

        double dotDrawY = y + dotOffset - 2;

        if (isDominant) {
            gc.setStroke(Color.BLACK);
            gc.setLineWidth(cellSize * 0.08);
            gc.strokeOval(x + dotOffset - 1, dotDrawY - 1, animalSize + 2, animalSize + 2);
        }

        if (animal instanceof Herbivore) gc.setFill(Color.LIGHTBLUE);
        else if (animal instanceof Parasite) gc.setFill(Color.LIGHTPINK);
        else gc.setFill(Color.ORANGE);

        gc.fillOval(x + dotOffset, dotDrawY, animalSize, animalSize);

        gc.setFill(Color.BLACK);
        double r = animalSize / 2.0;
        double cx = x + dotOffset + r;
        double cy = dotDrawY + r;
        double eyeDist = r * 0.6;
        double eyeSpace = r * 0.3;
        double eyeSize = animalSize * 0.15;

        double e1x = cx, e1y = cy, e2x = cx, e2y = cy;

        switch (animal.getDirection()) {
            case NORTH -> {
                e1x = cx - eyeSpace;
                e1y = cy - eyeDist;
                e2x = cx + eyeSpace;
                e2y = cy - eyeDist;
            }
            case SOUTH -> {
                e1x = cx - eyeSpace;
                e1y = cy + eyeDist;
                e2x = cx + eyeSpace;
                e2y = cy + eyeDist;
            }
            case WEST -> {
                e1x = cx - eyeDist;
                e1y = cy - eyeSpace;
                e2x = cx - eyeDist;
                e2y = cy + eyeSpace;
            }
            case EAST -> {
                e1x = cx + eyeDist;
                e1y = cy - eyeSpace;
                e2x = cx + eyeDist;
                e2y = cy + eyeSpace;
            }
            case NORTHEAST -> {
                e1x = cx + eyeDist * 0.2;
                e1y = cy - eyeDist;
                e2x = cx + eyeDist;
                e2y = cy - eyeDist * 0.2;
            }
            case NORTHWEST -> {
                e1x = cx - eyeDist * 0.2;
                e1y = cy - eyeDist;
                e2x = cx - eyeDist;
                e2y = cy - eyeDist * 0.2;
            }
            case SOUTHEAST -> {
                e1x = cx + eyeDist;
                e1y = cy + eyeDist * 0.2;
                e2x = cx + eyeDist * 0.2;
                e2y = cy + eyeDist;
            }
            case SOUTHWEST -> {
                e1x = cx - eyeDist;
                e1y = cy + eyeDist * 0.2;
                e2x = cx - eyeDist * 0.2;
                e2y = cy + eyeDist;
            }
        }

        gc.fillOval(e1x - eyeSize / 2, e1y - eyeSize / 2, eyeSize, eyeSize);
        gc.fillOval(e2x - eyeSize / 2, e2y - eyeSize / 2, eyeSize, eyeSize);

        double maxEnergy = 1;
        if (animal instanceof Herbivore) {
            maxEnergy = parameters.startAnimalEnergy();
        }
        if (animal instanceof Parasite) {
            maxEnergy = parameters.startParasiteEnergy();
        }
        double energyPercentage = (double) animal.getEnergy() / maxEnergy;
        energyPercentage = Math.min(1.0, Math.max(0.0, energyPercentage));

        double filledWidth = barWidth * energyPercentage;

        double barDrawY = y + cellSize * 0.8;

        gc.setFill(Color.DARKGRAY);
        gc.fillRect(x + barOffsetX, barDrawY, barWidth, barHeight);

        if (energyPercentage > 0.7) gc.setFill(Color.MEDIUMSEAGREEN);
        else if (energyPercentage > 0.3) gc.setFill(Color.YELLOW);
        else gc.setFill(Color.RED);

        gc.fillRect(x + barOffsetX, barDrawY, filledWidth, barHeight);

        gc.setStroke(Color.BLACK);
        gc.setLineWidth(0.5);
        gc.strokeRect(x + barOffsetX, barDrawY, barWidth, barHeight);
    }

    public void stopSimulation() {
        if (simulation != null) {
            simulation.stop();
        }
    }

    private void updateStatistics(WorldMap worldMap) {
        if (worldMap instanceof AbstractWorldMap abstractMap) {
            Statistics stats = abstractMap.getStatistics();
            dayCounter++;


            String statsText = String.format("""
                            Zwierzaki: %d
                            Rośliny: %d
                            Wolne pola: %d
                            Genotyp: %s
                            Śr. energia: %.2f
                            Śr. dł. życia (martwe): %.2f
                            Śr. dzieci: %.2f
                            """,
                    stats.animalCount(),
                    stats.plantCount(),
                    stats.freeFieldsCount(),
                    stats.topGenotype(),
                    stats.averageEnergy(),
                    stats.averageLifespan(),
                    stats.averageChildren()
            );
            statsLabel.setText(statsText);


            double valueToShow = switch (trackedStat) {
                case ANIMAL_COUNT -> stats.animalCount();
                case PLANT_COUNT -> stats.plantCount();
                case FREE_FIELDS -> stats.freeFieldsCount();
                case AVG_ENERGY -> stats.averageEnergy();
                case AVG_LIFESPAN -> stats.averageLifespan();
                case AVG_CHILDREN -> stats.averageChildren();
            };

            series.getData().add(new XYChart.Data<>(dayCounter, valueToShow));
        }
    }
}