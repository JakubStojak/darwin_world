package presenter;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.ConfigManager;
import util.Parameters;
import util.StatType;

import java.io.IOException;
import java.util.Optional;

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
    private ComboBox<String> configComboBox;
    private final ConfigManager configManager = new ConfigManager();

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
        saturationEnergyField.setText("10");
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

        refreshConfigList();
    }

    private void refreshConfigList() {
        configComboBox.getItems().clear();
        configComboBox.getItems().addAll(configManager.getAvailableConfigs());
    }

    private void toggleParasiteFields(boolean isEnabled) {
        startParasiteNumberField.setDisable(!isEnabled);
        startParasiteEnergyField.setDisable(!isEnabled);
        hostEnergyLossPerParasiteField.setDisable(!isEnabled);
        energyLossForParasiteWithoutHostField.setDisable(!isEnabled);
    }


    @FXML
    public void onSaveConfigClicked() {
        try {
            Parameters currentParams = getParametersFromFields();
            TextInputDialog dialog = new TextInputDialog("my_preset");
            dialog.setTitle("Zapisz konfigurację");
            dialog.setHeaderText("Zapisywanie ustawień");
            dialog.setContentText("Podaj nazwę konfiguracji:");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String name = result.get();
                if (!name.trim().isEmpty()) {
                    configManager.saveConfig(name, currentParams);
                    refreshConfigList();
                    configComboBox.getSelectionModel().select(name);
                    errorLabel.setText("Zapisano: " + name);
                    errorLabel.setStyle("-fx-text-fill: green;");
                }
            }
        } catch (IllegalArgumentException e) {
            errorLabel.setText("Popraw dane przed zapisem: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
        } catch (IOException e) {
            errorLabel.setText("Błąd zapisu pliku: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    public void onLoadConfigClicked() {
        String selectedConfig = configComboBox.getValue();
        if (selectedConfig == null) {
            errorLabel.setText("Wybierz preset z listy!");
            errorLabel.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            Parameters params = configManager.loadConfig(selectedConfig);
            fillFieldsWithParameters(params);
            errorLabel.setText("Wczytano: " + selectedConfig);
            errorLabel.setStyle("-fx-text-fill: green;");
        } catch (IOException e) {
            errorLabel.setText("Błąd odczytu pliku: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red;");
        }
    }

    private void fillFieldsWithParameters(Parameters p) {
        mapHeightField.setText(String.valueOf(p.mapHeight()));
        mapWidthField.setText(String.valueOf(p.mapWidth()));
        startGrassNumberField.setText(String.valueOf(p.startGrassNumber()));
        energyPerGrassField.setText(String.valueOf(p.energyPerGrass()));
        newGrassPerDayField.setText(String.valueOf(p.newGrassPerDay()));
        startAnimalEnergyField.setText(String.valueOf(p.startAnimalEnergy()));
        startAnimalNumberField.setText(String.valueOf(p.startAnimalNumber()));
        energyLossPerDayField.setText(String.valueOf(p.energyLossPerDay()));
        saturationEnergyField.setText(String.valueOf(p.saturationEnergy()));
        minimumMutationsField.setText(String.valueOf(p.minimumMutations()));
        maximumMutationsField.setText(String.valueOf(p.maximumMutations()));
        genomeLengthField.setText(String.valueOf(p.genomeLength()));

        startParasiteNumberField.setText(String.valueOf(p.startParasiteNumber()));
        startParasiteEnergyField.setText(String.valueOf(p.startParasiteEnergy()));
        hostEnergyLossPerParasiteField.setText(String.valueOf(p.hostEnergyLossPerParasite()));
        energyLossForParasiteWithoutHostField.setText(String.valueOf(p.energyLossForParasiteWithoutHost()));

        parasitesCheckBox.setSelected(p.startParasiteNumber() > 0);
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

            if (height <= 0 || width <= 0) throw new IllegalArgumentException("Wymiary mapy muszą być dodatnie!");
            if (genomeLen <= 0 || minMut < 0 || maxMut < 0)
                throw new IllegalArgumentException("Ujemne wartości w genomie!");
            if (minMut > maxMut) throw new IllegalArgumentException("Min mutacji nie może być większe od Max!");
            if (maxMut > genomeLen)
                throw new IllegalArgumentException("Max mutacji nie może być większe od dł. genomu!");
            if (startEnergy <= 0) throw new IllegalArgumentException("Energia startowa zwierząt musi być > 0!");
            if (saturation <= 0) throw new IllegalArgumentException("Energia nasycenia musi być > 0!");
            if (startAnimals < 0 || startGrass < 0)
                throw new IllegalArgumentException("Ilość obiektów nie może być ujemna!");
            if (2 * saturation < startEnergy) {
                throw new IllegalArgumentException("Energia startowa musi być mniejsza lub równa dwukrotności energii saturacji!");
            }

            if (parasitesCheckBox.isSelected()) {
                if (startParasiteEnergy <= 0)
                    throw new IllegalArgumentException("Energia startowa pasożyta musi być > 0!");
                if (startParasites <= 0) throw new IllegalArgumentException("Liczba pasożytów musi być > 0!");
                if (hostLoss < 0 || parasiteLoss < 0)
                    throw new IllegalArgumentException("Straty energii nie mogą być ujemne!");
                if (startAnimals + startParasites > width * height)
                    throw new IllegalArgumentException("Za dużo zwierząt i pasożytów na mapie!");
            } else {
                if (startAnimals > width * height) throw new IllegalArgumentException("Za dużo zwierząt na mapie!");
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