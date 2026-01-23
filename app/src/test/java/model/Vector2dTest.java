package model;

import org.junit.jupiter.api.Test;
import util.Vector2d;

import static org.junit.jupiter.api.Assertions.*;

public class Vector2dTest {

    @Test
    public void TestEquals() {
        Vector2d v1 = new Vector2d(2,3);
        Vector2d v2 = new Vector2d(2,3);
        Vector2d v3 = new Vector2d(1,2);

        assertTrue(v1.equals(v2));
        assertTrue(v2.equals(v1));
        assertFalse(v2.equals(v3));
        assertFalse(v1.equals(2));
    }

    @Test
    public void TestToString() {
        Vector2d v1 = new Vector2d(2,3);

        assertEquals("(2,3)", v1.toString());
    }

    @Test
    public void TestPrecedes() {
        Vector2d v1 = new Vector2d(2,3);
        Vector2d v2 = new Vector2d(1,2);
        Vector2d v3 = new Vector2d(3, 1);

        assertEquals(false, v1.precedes(v2));
        assertEquals(true, v2.precedes(v1));
        assertEquals(false, v3.precedes(v2));
    }

    @Test
    public void TestFollows() {
        Vector2d v1 = new Vector2d(2,3);
        Vector2d v2 = new Vector2d(1,2);
        Vector2d v3 = new Vector2d(3, 1);

        assertEquals(true, v1.follows(v2));
        assertEquals(false, v2.follows(v1));
        assertEquals(false, v3.follows(v2));
    }

    @Test
    public void TestUpperRight() {
        Vector2d v1 = new Vector2d(2,3);
        Vector2d v2 = new Vector2d(1,2);
        Vector2d expectedUpperRight = new Vector2d(2, 3);

        assertEquals(expectedUpperRight, v1.upperRight(v2));
        assertEquals(expectedUpperRight, v2.upperRight(v1));
    }

    @Test
    public void TestLowerLeft() {
        Vector2d v1 = new Vector2d(2,3);
        Vector2d v2 = new Vector2d(1,2);
        Vector2d expectedLowerLeft = new Vector2d(1, 2);

        assertEquals(expectedLowerLeft, v1.lowerLeft(v2));
        assertEquals(expectedLowerLeft, v2.lowerLeft(v1));
    }

    @Test
    public void TestAdd() {
        Vector2d v1 = new Vector2d(2,3);
        Vector2d v2 = new Vector2d(1,2);
        Vector2d result = new Vector2d(3, 5);

        assertEquals(result, v1.add(v2));
        assertEquals(result, v2.add(v1));
    }

    @Test
    public void TestSubtract() {
        Vector2d v1 = new Vector2d(2,3);
        Vector2d v2 = new Vector2d(1,2);
        Vector2d resultV1 = new Vector2d(1, 1);
        Vector2d resultV2 = new Vector2d(-1, -1);

        assertEquals(resultV1, v1.subtract(v2));
        assertEquals(resultV2, v2.subtract(v1));
    }

    @Test
    public void TestOpposite() {
        Vector2d v1 = new Vector2d(2,3);
        Vector2d v2 = new Vector2d(-2, 3);
        Vector2d opV1 = new Vector2d(-2, -3);
        Vector2d opV2 = new Vector2d(2, -3);

        assertEquals(opV1, v1.opposite());
        assertEquals(opV2, v2.opposite());

    }
}