package util;

import model.Vector2d;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

public class RandomPositionGenerator implements Iterable<Vector2d>{
    private final int animalCount;
    private final int[] positions;
    private final int numberOfPositions;
    private final int maxWidth;

    public RandomPositionGenerator(int maxWidth, int maxHeight, int animalCount) {
        if (maxWidth <= 0 || maxHeight <= 0 || animalCount < 0) {
            throw new IllegalArgumentException("Niepoprawne argumenty");
        }
        this.animalCount = animalCount;
        this.maxWidth = maxWidth;
        numberOfPositions = maxWidth * maxHeight;
        positions = new int[numberOfPositions];
        for (int index = 0; index < numberOfPositions; index++) {
            positions[index] = index;
        }
    }

    @Override
    public Iterator<Vector2d> iterator() {
        return new Iterator<Vector2d>() {
            int generated = 0;
            Random random = new Random();

            @Override
            public boolean hasNext() {
                return generated < animalCount && generated < numberOfPositions;
            }

            @Override
            public Vector2d next() {
                if (!hasNext()) {
                    throw new NoSuchElementException("Nie można generować kolejnej pozycji");
                }
                int currentIndex = random.nextInt(numberOfPositions - generated) + generated;
                int currentPosition = positions[currentIndex];
                int x = currentPosition % maxWidth;
                int y = currentPosition / maxWidth;
                positions[currentIndex] = positions[generated];
                positions[generated] = currentPosition;
                generated++;
                return new Vector2d(x, y);
            }
        };
    }
}
