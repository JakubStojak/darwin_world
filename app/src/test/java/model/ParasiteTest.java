package model;

import org.junit.jupiter.api.Test;
import util.MapDirection;
import util.Parameters;

import static org.junit.jupiter.api.Assertions.*;

class ParasiteTest {
    Parameters params = new Parameters(5, 5, 1, 1, 1,
            5, 5, 1, 1, 1,
            2, 5, 1, 1,1, 1);

    @Test
    void testMoveWithoutHost() {
        Vector2d startPosition = new Vector2d(3, 3);
        Parasite parasite = new Parasite(params, MapDirection.NORTH, startPosition, new Genome(params));
        assertFalse(parasite.hasHost());

        MapDirection moveDirection = MapDirection.fromInt(parasite.getGenome().getMove());
        Vector2d expectedPosition = startPosition.add(moveDirection.toUnitVector());

        parasite.move();

        assertEquals(expectedPosition, parasite.position());
    }

    @Test
    void testConsume() {
        Parasite parasite = new Parasite(params, MapDirection.NORTH, new Vector2d(0, 0), new Genome(params));
        int initialEnergy = parasite.getEnergy();

        parasite.consume();

        assertEquals(initialEnergy + params.hostEnergyLossPerParasite(), parasite.getEnergy());
    }

    @Test
    void testHostManagementAndPanic() {
        Parasite parasite = new Parasite(params, MapDirection.NORTH, new Vector2d(0, 0), new Genome(params));
        Herbivore host = new Herbivore(params, MapDirection.NORTH, new Vector2d(0, 0), new Genome(params));

        parasite.setHost(host);
        assertTrue(parasite.hasHost());
        assertEquals(host, parasite.getHost());

        Herbivore otherHost = new Herbivore(params, MapDirection.SOUTH, new Vector2d(0, 0), new Genome(params));
        parasite.setHost(otherHost);
        assertEquals(host, parasite.getHost());

        parasite.panic();
        assertFalse(parasite.hasHost());
        assertNull(parasite.getHost());
    }

    @Test
    void testLoseEnergyMethods() {
        Parasite parasite = new Parasite(params, MapDirection.NORTH, new Vector2d(0, 0), new Genome(params));
        int energy = parasite.getEnergy();

        parasite.loseEnergyWithoutHost();
        assertEquals(energy - params.energyLossForParasiteWithoutHost(), parasite.getEnergy());

        energy = parasite.getEnergy();
        parasite.loseEnergyWithHost();
        assertEquals(energy - params.energyLossPerDay(), parasite.getEnergy());
    }
}