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
import util.Boundary;
import util.Parameters;
import util.StatType;
import util.Statistics;

import java.util.*;

public class SimulationPresenter implements MapChangeListener {

    @FXML private Canvas mapGrid;
    @FXML private Label infoLabel;
    @FXML private Label statsLabel;
    @FXML private LineChart<Number, Number> statChart;

    private Simulation simulation;
    private Parameters parameters;
    private static final double CELL_SIZE = 30.0;

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
        WorldMap map = simulation.getMap();

        AbstractWorldMap abstractMap = (AbstractWorldMap) map;
        abstractMap.registerObserver(this);

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
        alert.setHeaderText("Koniec gry");
        alert.setContentText("Brak zwierząt na mapie.");
        alert.showAndWait();
    }

    private void drawMap(WorldMap map) {
        GraphicsContext gc = mapGrid.getGraphicsContext2D();
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, mapGrid.getWidth(), mapGrid.getHeight());

        if (map == null) return;

        Boundary bounds = map.getCurrentBounds();
        double width = (bounds.upperRight().getX() - bounds.lowerLeft().getX() + 1) * CELL_SIZE;
        double height = (bounds.upperRight().getY() - bounds.lowerLeft().getY() + 1) * CELL_SIZE;
        mapGrid.setWidth(width);
        mapGrid.setHeight(height);

        gc.setFill(Color.WHITESMOKE);
        gc.fillRect(0, 0, width, height);

        int minX = bounds.lowerLeft().getX();
        int maxX = bounds.upperRight().getX();
        int minY = bounds.lowerLeft().getY();
        int maxY = bounds.upperRight().getY();

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                Vector2d position = new Vector2d(x, y);
                double drawX = (x - minX) * CELL_SIZE;
                double drawY = (maxY - y) * CELL_SIZE;

                if (map.isOccupiedByPlant(position)) {
                    gc.setFill(Color.FORESTGREEN);
                    gc.fillRect(drawX, drawY, CELL_SIZE, CELL_SIZE);
                    if (dominantPositions.contains(position)) {
                        gc.setStroke(Color.GOLD);
                        gc.setLineWidth(3.0);
                        gc.strokeRect(drawX + 1.5, drawY + 1.5, CELL_SIZE - 3, CELL_SIZE - 3);
                    }
                }

                if (map instanceof AbstractWorldMap abstractMap) {
                    List<Animal> animalsAtPos = abstractMap.getAnimalsAt(position);
                    if (!animalsAtPos.isEmpty()) {
                        Animal animalToDraw = animalsAtPos.get(0);

                        boolean isDominant = dominantAnimals.contains(animalToDraw);

                        drawAnimalWithEnergyBar(gc, animalToDraw, drawX, drawY, isDominant);
                    }
                }
            }
        }

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(0.5);
        for (int x = 0; x <= (maxX - minX + 1); x++) {
            gc.strokeLine(x * CELL_SIZE, 0, x * CELL_SIZE, height);
        }
        for (int y = 0; y <= (maxY - minY + 1); y++) {
            gc.strokeLine(0, y * CELL_SIZE, width, y * CELL_SIZE);
        }
    }

    private void drawAnimalWithEnergyBar(GraphicsContext gc, Animal animal, double x, double y, boolean isDominant) {
        double animalSize = CELL_SIZE * 0.6;
        double barWidth = CELL_SIZE * 0.8;
        double barHeight = 4.0;

        double dotOffset = (CELL_SIZE - animalSize) / 2;
        double barOffsetX = (CELL_SIZE - barWidth) /2;

        double dotDrawY = y + dotOffset - 3;

        if (isDominant) {
            gc.setStroke(Color.PURPLE);
            gc.setLineWidth(3.0);
            gc.strokeOval(x + dotOffset - 2, dotDrawY - 2, animalSize + 4, animalSize + 4);
        }

        if (animal instanceof Herbivore) gc.setFill(Color.BLUE);
        else if (animal instanceof Parasite) gc.setFill(Color.RED);
        else gc.setFill(Color.ORANGE);

        gc.fillOval(x + dotOffset, dotDrawY, animalSize, animalSize);

        double maxEnergy = parameters.startAnimalEnergy();
        double energyPercentage = (double) animal.getEnergy() / maxEnergy;
        energyPercentage = Math.min(1.0, Math.max(0.0, energyPercentage));

        double filledWidth = barWidth * energyPercentage;

        double barDrawY = y + CELL_SIZE - 6;

        gc.setFill(Color.DARKGRAY);
        gc.fillRect(x + barOffsetX, barDrawY, barWidth, barHeight);

        if (energyPercentage > 0.7) gc.setFill(Color.LIMEGREEN);
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
                Śr. energia: %.2f
                Śr. dł. życia (martwe): %.2f
                Śr. dzieci: %.2f
                """,
                    stats.animalCount(),
                    stats.plantCount(),
                    stats.freeFieldsCount(),
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