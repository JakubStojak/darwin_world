package model.file_management;

import util.Parameters;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigManager {
    private static final String CONFIG_DIRECTORY = "configs";
    private final Path configPath;

    public ConfigManager() {
        Path localPath = Paths.get(CONFIG_DIRECTORY);
        Path modulePath = Paths.get("app", CONFIG_DIRECTORY);

        if (Files.exists(localPath)) {
            this.configPath = localPath;
        } else if (Files.exists(modulePath)) {
            this.configPath = modulePath;
        } else {
            if (Files.exists(Paths.get("app"))) {
                this.configPath = modulePath;
            } else {
                this.configPath = localPath;
            }
        }
        createConfigDirectory();
    }

    private void createConfigDirectory() {
        try {
            if (!Files.exists(configPath)) {
                Files.createDirectories(configPath);
            }
        } catch (IOException e) {
            System.err.println("Nie udało się utworzyć katalogu konfiguracji: " + e.getMessage());
        }
    }

    public void saveConfig(String name, Parameters params) throws IOException {
        Properties props = new Properties();

        props.setProperty("mapHeight", String.valueOf(params.mapHeight()));
        props.setProperty("mapWidth", String.valueOf(params.mapWidth()));
        props.setProperty("startGrassNumber", String.valueOf(params.startGrassNumber()));
        props.setProperty("energyPerGrass", String.valueOf(params.energyPerGrass()));
        props.setProperty("newGrassPerDay", String.valueOf(params.newGrassPerDay()));
        props.setProperty("startAnimalEnergy", String.valueOf(params.startAnimalEnergy()));
        props.setProperty("startAnimalNumber", String.valueOf(params.startAnimalNumber()));
        props.setProperty("energyLossPerDay", String.valueOf(params.energyLossPerDay()));
        props.setProperty("saturationEnergy", String.valueOf(params.saturationEnergy()));
        props.setProperty("minimumMutations", String.valueOf(params.minimumMutations()));
        props.setProperty("maximumMutations", String.valueOf(params.maximumMutations()));
        props.setProperty("genomeLength", String.valueOf(params.genomeLength()));
        props.setProperty("startParasiteEnergy", String.valueOf(params.startParasiteEnergy()));
        props.setProperty("startParasiteNumber", String.valueOf(params.startParasiteNumber()));
        props.setProperty("hostEnergyLossPerParasite", String.valueOf(params.hostEnergyLossPerParasite()));
        props.setProperty("energyLossForParasiteWithoutHost", String.valueOf(params.energyLossForParasiteWithoutHost()));

        Path fullPath = configPath.resolve(name + ".properties");
        try (Writer writer = Files.newBufferedWriter(fullPath)) {
            props.store(writer, "Konfiguracja symulacji: " + name);
        }
    }

    public Parameters loadConfig(String name) throws IOException {
        Properties props = new Properties();
        Path fullPath = configPath.resolve(name + ".properties");

        try (Reader reader = Files.newBufferedReader(fullPath)) {
            props.load(reader);
        }

        return new Parameters(
                Integer.parseInt(props.getProperty("mapHeight")),
                Integer.parseInt(props.getProperty("mapWidth")),
                Integer.parseInt(props.getProperty("startGrassNumber")),
                Integer.parseInt(props.getProperty("energyPerGrass")),
                Integer.parseInt(props.getProperty("newGrassPerDay")),
                Integer.parseInt(props.getProperty("startAnimalEnergy")),
                Integer.parseInt(props.getProperty("startAnimalNumber")),
                Integer.parseInt(props.getProperty("energyLossPerDay")),
                Integer.parseInt(props.getProperty("saturationEnergy")),
                Integer.parseInt(props.getProperty("minimumMutations")),
                Integer.parseInt(props.getProperty("maximumMutations")),
                Integer.parseInt(props.getProperty("genomeLength")),
                Integer.parseInt(props.getProperty("startParasiteEnergy")),
                Integer.parseInt(props.getProperty("startParasiteNumber")),
                Integer.parseInt(props.getProperty("hostEnergyLossPerParasite")),
                Integer.parseInt(props.getProperty("energyLossForParasiteWithoutHost"))
        );
    }

    public List<String> getAvailableConfigs() {
        if (!Files.exists(configPath)) {
            return Collections.emptyList();
        }
        try (Stream<Path> paths = Files.list(configPath)) {
            return paths
                    .filter(file -> !Files.isDirectory(file))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .filter(name -> name.endsWith(".properties"))
                    .map(name -> name.replace(".properties", ""))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }
}


