package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import util.Parameters;

import java.util.List;

class GenomeTest {
    Parameters params = new Parameters(10, 10, 1, 1, 1,
                                    100, 50, 2, 2, 10,
                                    0, 0, 0, 0, 0, 0);

    @Test
    void testNextAndGetMove() {
        Genome genome = new Genome(params, List.of(1, 2, 3));
        assertEquals(1, genome.getMove());
        genome.next();
        assertEquals(2, genome.getMove());
        genome.next();
        assertEquals(3, genome.getMove());
        genome.next();
        assertEquals(1, genome.getMove());
    }

    @Test
    void testCalculateDistance() {
        Genome g1 = new Genome(params, List.of(1, 2, 3, 4, 5));
        Genome g2 = new Genome(params, List.of(1, 0, 3, 0, 5));
        assertEquals(2, g1.calculateDistance(g2));
    }
}