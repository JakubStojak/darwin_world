package presenter;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import model.*;
import util.Boundary;
import util.Parameters;

import java.util.List;

public class SimulationPresenter implements MapChangeListener {

    @FXML private Canvas mapGrid;
    @FXML private Label infoLabel;

    private WorldMap map;
    private Simulation simulation;
    private static final double CELL_SIZE = 30.0;

    public void startSimulation(Parameters params) {
        this.simulation = new Simulation(params);
        this.map = simulation.getMap();

        if (this.map instanceof AbstractWorldMap abstractMap) {
            abstractMap.registerObserver(this);
        }

        drawMap(map);

        Thread thread = new Thread(simulation);
        thread.setDaemon(true);
        thread.start();
    }

    @Override
    public void mapChanged(WorldMap worldMap, String message) {
        Platform.runLater(() -> {
            drawMap(worldMap);
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
                }

                if (map instanceof AbstractWorldMap abstractMap) {
                    List<Animal> animals = abstractMap.getAnimalsAt(position);
                    if (!animals.isEmpty()) {
                        Animal animal = animals.get(0);
                        if (animal instanceof Herbivore) gc.setFill(Color.BLUE);
                        else if (animal instanceof Parasite) gc.setFill(Color.RED);
                        else gc.setFill(Color.ORANGE);
                        gc.fillOval(drawX, drawY, CELL_SIZE, CELL_SIZE);
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
}