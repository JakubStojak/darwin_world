package model;

import model.world_elements.Genome;
import model.world_elements.Herbivore;
import model.world_elements.Parasite;
import org.junit.jupiter.api.Test;
import util.MapDirection;
import util.Parameters;
import util.Vector2d;

import static org.junit.jupiter.api.Assertions.*;

class HerbivoreTest {
    Parameters params = new Parameters(5, 5, 1, 1, 1,
            5, 5, 1, 1, 1,
            2, 5, 1, 1,1, 1);

    @Test
    void TestReproduce() {
        Herbivore herbivore1 = new Herbivore(params, MapDirection.SOUTH, new Vector2d(1,1), new Genome(params));
        Herbivore herbivore2 = new Herbivore(params, MapDirection.NORTH, new Vector2d(1,1), new Genome(params));
        Herbivore herbivore3 = new Herbivore(params, MapDirection.NORTH, new Vector2d(2,1), new Genome(params));
        Parasite parasite = new Parasite(params, MapDirection.NORTH, new Vector2d(2,1), new Genome(params));
        assertInstanceOf(Herbivore.class, herbivore1.reproduce(herbivore2));
        assertNotEquals(Herbivore.class, herbivore1.reproduce(herbivore3));
    }

    @Test
    void testEat() {
        Herbivore herbivore = new Herbivore(params, MapDirection.NORTH, new Vector2d(2, 2), new Genome(params));
        int initialEnergy = herbivore.getEnergy();

        herbivore.eat();

        assertEquals(initialEnergy + params.energyPerGrass(), herbivore.getEnergy(),
                "Energia powinna wzrosnąć o wartość energyPerGrass");
    }

    @Test
    void testMove() {
        Vector2d startPosition = new Vector2d(2, 2);
        Herbivore herbivore = new Herbivore(params, MapDirection.NORTH, startPosition, new Genome(params));

        MapDirection moveDirection = MapDirection.fromInt(herbivore.getGenome().getMove());
        Vector2d expectedPosition = startPosition.add(moveDirection.toUnitVector());

        herbivore.move();

        assertEquals(expectedPosition, herbivore.position());
    }
}