package model;

public record Parameters(
     int mapHeight,
     int startGrassNumber,
     int energyPerGrass,
     int newGrassPerDay,
     int startAnimalEnergy,
     int startAnimalNumber,
     int energyLossPerDay,
     int saturationEnergy,
     int energyLossPerBreed,
     int minimumMutations,
     int maximumMutations,
     int genomeLength,
     int startParasiteNumber,
     int hostEnergyLossPerParasite,
     int energyLossForParasiteWithoutHost
){}