package model;

import model.world_elements.AnimalComparator;
import model.world_elements.Genome;
import model.world_elements.Herbivore;
import org.junit.jupiter.api.Test;
import util.MapDirection;
import util.Parameters;
import util.Vector2d;

import static org.junit.jupiter.api.Assertions.*;

class AnimalComparatorTest {
    Parameters params = new Parameters(5, 5, 1, 1, 1,
            5, 5, 1, 1, 1,
            2, 5, 1, 1,1, 1);


    @Test
    void testCompareShouldPreferAnimalWithMoreChildren() {
        Herbivore parent = new Herbivore(params, MapDirection.SOUTH, new Vector2d(1,1), new Genome(params));
        Herbivore lonely = new Herbivore(params, MapDirection.NORTH, new Vector2d(1,1), new Genome(params));
        Herbivore partner = new Herbivore(params, MapDirection.NORTH, new Vector2d(1,1), new Genome(params));

        parent.reproduce(partner);

        AnimalComparator comparator = new AnimalComparator();
        int result = comparator.compare(parent, lonely);

        assertTrue(result > 0);
    }
}