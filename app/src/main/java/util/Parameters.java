package util;

public record Parameters(
     int mapHeight,
     int mapWidth,

     int startGrassNumber,
     int energyPerGrass,
     int newGrassPerDay,

     int startAnimalEnergy,
     int startAnimalNumber,
     int energyLossPerDay,
     int saturationEnergy,

     int minimumMutations,
     int maximumMutations,

     int genomeLength,
     int startParasiteEnergy,
     int startParasiteNumber,
     int hostEnergyLossPerParasite,
     int energyLossForParasiteWithoutHost
){}