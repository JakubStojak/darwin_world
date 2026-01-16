package presenter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import util.Parameters;
import util.StatType;

import java.io.IOException;

public class StartPresenter {

    @FXML
    private TextField mapHeightField;
    @FXML
    private TextField mapWidthField;
    @FXML
    private TextField startGrassNumberField;
    @FXML
    private TextField energyPerGrassField;
    @FXML
    private TextField newGrassPerDayField;
    @FXML
    private TextField startAnimalEnergyField;
    @FXML
    private TextField startAnimalNumberField;
    @FXML
    private TextField energyLossPerDayField;
    @FXML
    private TextField saturationEnergyField;
    @FXML
    private TextField minimumMutationsField;
    @FXML
    private TextField maximumMutationsField;
    @FXML
    private TextField genomeLengthField;
    @FXML
    private TextField startParasiteNumberField;
    @FXML
    private TextField startParasiteEnergyField;
    @FXML
    private TextField hostEnergyLossPerParasiteField;
    @FXML
    private TextField energyLossForParasiteWithoutHostField;
    @FXML
    private CheckBox parasitesCheckBox;
    @FXML
    private ComboBox<StatType> statTypeComboBox;

    @FXML
    private Label errorLabel;

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
        startParasiteEnergyField.setText("15");
        hostEnergyLossPerParasiteField.setText("1");
        energyLossForParasiteWithoutHostField.setText("1");

        parasitesCheckBox.setSelected(true);
        parasitesCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            toggleParasiteFields(newValue);
        });

        statTypeComboBox.getItems().setAll(StatType.values());
        statTypeComboBox.getSelectionModel().selectFirst();
    }

    private void toggleParasiteFields(boolean isEnabled) {
        startParasiteNumberField.setDisable(!isEnabled);
        startParasiteEnergyField.setDisable(!isEnabled);
        hostEnergyLossPerParasiteField.setDisable(!isEnabled);
        energyLossForParasiteWithoutHostField.setDisable(!isEnabled);
    }

    @FXML
    public void onSimulationStartClicked() {
        try {
            errorLabel.setText("");
            Parameters params = getParametersFromFields();
            StatType selectedStat = statTypeComboBox.getValue();

            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getClassLoader().getResource("simulation.fxml"));
            BorderPane viewRoot = loader.load();

            SimulationPresenter presenter = loader.getController();

            presenter.setTrackedStat(selectedStat);
            presenter.startSimulation(params);

            Stage stage = new Stage();
            stage.setTitle("Symulacja " + params.mapWidth() + "x" + params.mapHeight());
            stage.setScene(new Scene(viewRoot));
            stage.setOnCloseRequest(event -> {
                presenter.stopSimulation();
            });
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
            int height = Integer.parseInt(mapHeightField.getText());
            int width = Integer.parseInt(mapWidthField.getText());
            int startGrass = Integer.parseInt(startGrassNumberField.getText());
            int energyGrass = Integer.parseInt(energyPerGrassField.getText());
            int newGrass = Integer.parseInt(newGrassPerDayField.getText());
            int startEnergy = Integer.parseInt(startAnimalEnergyField.getText());
            int startAnimals = Integer.parseInt(startAnimalNumberField.getText());
            int lossDay = Integer.parseInt(energyLossPerDayField.getText());
            int saturation = Integer.parseInt(saturationEnergyField.getText());
            int minMut = Integer.parseInt(minimumMutationsField.getText());
            int maxMut = Integer.parseInt(maximumMutationsField.getText());
            int genomeLen = Integer.parseInt(genomeLengthField.getText());

            int startParasiteEnergy = 0;
            int startParasites = 0;
            int hostLoss = 0;
            int parasiteLoss = 0;

            if (parasitesCheckBox.isSelected()) {
                startParasites = Integer.parseInt(startParasiteNumberField.getText());
                hostLoss = Integer.parseInt(hostEnergyLossPerParasiteField.getText());
                parasiteLoss = Integer.parseInt(energyLossForParasiteWithoutHostField.getText());
                startParasiteEnergy = Integer.parseInt(startParasiteEnergyField.getText());
            }

            if (height <= 0 || width <= 0) {
                throw new IllegalArgumentException("Wymiary mapy muszą być dodatnie!");
            }
            if (genomeLen <= 0 || minMut < 0 || maxMut <= 0) {
                throw new IllegalArgumentException("Ujemne wartości w genomie!");
            }
            if (minMut > maxMut) {
                throw new IllegalArgumentException("Minimalna liczba mutacji nie może być większa od maksymalnej!");
            }
            if (maxMut > genomeLen) {
                throw new IllegalArgumentException("Liczba mutacji nie może przekraczać długości genomu!");
            }
            if (startEnergy <= 0 || startParasiteEnergy <= 0) {
                throw new IllegalArgumentException("Liczba energii startu musi być większa od 0!");
            }
            if (saturation <= 0) {
                throw new IllegalArgumentException("Energia saturacji być większa od 0!");
            }
            if (startAnimals + startParasites > width * height) {
                throw new IllegalArgumentException("Więcej zwierzaków niż miejsca!");
            }
            if (startAnimals < 0 || startGrass < 0) {
                throw new IllegalArgumentException("Liczba obiektów nie może być ujemna!");
            }
            if (energyGrass <= 0 || hostLoss <= 0 || parasiteLoss <= 0) {
                throw new IllegalArgumentException("Energie dodane i stracone nie mogą być ujemne!");
            }

            return new Parameters(
                    height, width, startGrass, energyGrass, newGrass,
                    startEnergy, startAnimals, lossDay, saturation,
                    minMut, maxMut, genomeLen, startParasiteEnergy,
                    startParasites,
                    hostLoss,
                    parasiteLoss
            );

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Wszystkie pola muszą być liczbami!");
        }
    }
}