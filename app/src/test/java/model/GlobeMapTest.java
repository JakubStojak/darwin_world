package model;

import org.junit.jupiter.api.Test;
import util.MapDirection;
import util.Parameters;

import static org.junit.jupiter.api.Assertions.*;

class GlobeMapTest {

    Parameters params = new Parameters(5, 5, 1, 1, 1,
            5, 5, 1, 1, 1,
            2, 5, 1, 1,1, 1);

    @Test
    void testGlobeMapWrapping() {
        GlobeMap map = new GlobeMap(params);

        Vector2d edgePos = new Vector2d(4, 2);
        Vector2d wrappedX = map.validatePosition(new Vector2d(5, 2), null);
        assertEquals(0, wrappedX.getX());
        assertEquals(2, wrappedX.getY());

        Herbivore h = new Herbivore(params, MapDirection.NORTH, new Vector2d(2, 4), new Genome(params));
        Vector2d polarMove = map.validatePosition(new Vector2d(2, 5), h);

        assertEquals(4, polarMove.getY());
    }

    @Test
    void testParasiteInfection() throws IncorrectPositionException {
        GlobeMap map = new GlobeMap(params);
        Vector2d pos = new Vector2d(2, 2);

        Herbivore host = new Herbivore(params, MapDirection.NORTH, pos, new Genome(params));
        Parasite parasite = new Parasite(params, MapDirection.NORTH, pos, new Genome(params));

        map.place(host);
        map.place(parasite);

        assertFalse(parasite.hasHost());

        map.animalInteractions();

        assertTrue(parasite.hasHost());
        assertEquals(host, parasite.getHost());
        assertTrue(host.getParasites().contains(parasite));
    }

    @Test
    void testParasiteFollowsHost() throws IncorrectPositionException {
        GlobeMap map = new GlobeMap(params);
        Vector2d pos1 = new Vector2d(1, 1);
        Vector2d pos2 = new Vector2d(1, 1);

        Herbivore host = new Herbivore(params, MapDirection.NORTH, pos1, new Genome(params));
        Parasite parasite = new Parasite(params, MapDirection.NORTH, pos2, new Genome(params));

        map.place(host);
        map.place(parasite);
        parasite.setHost(host);

        map.removeAnimal(host);
        host.setPosition(new Vector2d(2, 2));
        map.place(host);

        map.move(parasite);

        Vector2d distance = parasite.position().subtract(host.position());
        assertTrue(Math.sqrt(distance.getX()^2 + distance.getY()^2) <= Math.sqrt(2), "Pasożyt powinien być obok hosta po jego ruchu");
    }

    @Test
    void testDeadAnimalRemovalAndPanic() throws IncorrectPositionException {
        GlobeMap map = new GlobeMap(params);
        Herbivore victim = new Herbivore(params, MapDirection.NORTH, new Vector2d(1,1), new Genome(params));
        Parasite p = new Parasite(params, MapDirection.NORTH, new Vector2d(1,1), new Genome(params));

        map.place(victim);
        map.place(p);
        p.setHost(victim);
        victim.addParasite(p);

        for (int i = 0; i <10; i++) {
            victim.loseEnergyForDay();
        }

        map.removeDeadAnimals(10);

        assertFalse(p.hasHost());
        assertNull(p.getHost());
    }
}