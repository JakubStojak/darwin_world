package presenter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import util.Parameters;

import java.io.IOException;

public class StartPresenter {

    @FXML private TextField mapHeightField;
    @FXML private TextField mapWidthField;
    @FXML private TextField startGrassNumberField;
    @FXML private TextField energyPerGrassField;
    @FXML private TextField newGrassPerDayField;
    @FXML private TextField startAnimalEnergyField;
    @FXML private TextField startAnimalNumberField;
    @FXML private TextField energyLossPerDayField;
    @FXML private TextField saturationEnergyField;
    @FXML private TextField minimumMutationsField;
    @FXML private TextField maximumMutationsField;
    @FXML private TextField genomeLengthField;
    @FXML private TextField startParasiteNumberField;
    @FXML private TextField hostEnergyLossPerParasiteField;
    @FXML private TextField energyLossForParasiteWithoutHostField;

    @FXML private Label errorLabel;

    @FXML
    public void initialize() {
        mapHeightField.setText("15");
        mapWidthField.setText("15");
        startGrassNumberField.setText("7");
        energyPerGrassField.setText("5");
        newGrassPerDayField.setText("2");
        startAnimalEnergyField.setText("15");
        startAnimalNumberField.setText("4");
        energyLossPerDayField.setText("1");
        saturationEnergyField.setText("5");
        minimumMutationsField.setText("0");
        maximumMutationsField.setText("2");
        genomeLengthField.setText("8");
        startParasiteNumberField.setText("4");
        hostEnergyLossPerParasiteField.setText("1");
        energyLossForParasiteWithoutHostField.setText("1");
    }

    @FXML
    public void onSimulationStartClicked() {
        try {
            errorLabel.setText("");
            Parameters params = getParametersFromFields();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
            BorderPane viewRoot = loader.load();

            SimulationPresenter presenter = loader.getController();

            presenter.startSimulation(params);

            Stage stage = new Stage();
            stage.setTitle("Symulacja " + params.mapWidth() + "x" + params.mapHeight());
            stage.setScene(new Scene(viewRoot));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Błąd ładowania widoku symulacji.");
        } catch (IllegalArgumentException e) {
            errorLabel.setText("Błąd danych: " + e.getMessage());
        }
    }

    private Parameters getParametersFromFields() {
        try {
            return new Parameters(
                    Integer.parseInt(mapHeightField.getText()),
                    Integer.parseInt(mapWidthField.getText()),
                    Integer.parseInt(startGrassNumberField.getText()),
                    Integer.parseInt(energyPerGrassField.getText()),
                    Integer.parseInt(newGrassPerDayField.getText()),
                    Integer.parseInt(startAnimalEnergyField.getText()),
                    Integer.parseInt(startAnimalNumberField.getText()),
                    Integer.parseInt(energyLossPerDayField.getText()),
                    Integer.parseInt(saturationEnergyField.getText()),
                    Integer.parseInt(minimumMutationsField.getText()),
                    Integer.parseInt(maximumMutationsField.getText()),
                    Integer.parseInt(genomeLengthField.getText()),
                    Integer.parseInt(startParasiteNumberField.getText()),
                    Integer.parseInt(hostEnergyLossPerParasiteField.getText()),
                    Integer.parseInt(energyLossForParasiteWithoutHostField.getText())
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Wszystkie pola muszą być liczbami!");
        }
    }
}