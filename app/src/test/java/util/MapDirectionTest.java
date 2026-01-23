package util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapDirectionTest {

    @Test
    void TestNext() {
        assertEquals(MapDirection.EAST, MapDirection.NORTHEAST.next());
        assertEquals(MapDirection.NORTHEAST, MapDirection.NORTH.next());
    }

    @Test
    void TestRotate() {
        int genome = 3;
        assertEquals(MapDirection.NORTH, MapDirection.SOUTHWEST.rotate(genome));
        assertEquals(MapDirection.SOUTH, MapDirection.NORTHEAST.rotate(genome));
    }

    @Test
    void TestToUnitVector() {
        assertEquals(new Vector2d(1,0), MapDirection.EAST.toUnitVector());
        assertEquals(new Vector2d(1,-1), MapDirection.SOUTHEAST.toUnitVector());
    }

    @Test
    void TestFromInt() {
        assertEquals(MapDirection.NORTH, MapDirection.fromInt(0));
        assertEquals(MapDirection.SOUTHWEST, MapDirection.fromInt(5));
    }
}