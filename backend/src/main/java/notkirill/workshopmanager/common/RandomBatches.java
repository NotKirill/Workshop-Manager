package notkirill.workshopmanager.common;

import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;

public final class RandomBatches {
    private RandomBatches() {
    }


    @SuppressWarnings("java:S127")
    public static <T> List<List<T>> split(List<T> items, int minSize, int maxSize, RandomGenerator random) {
        if (minSize < 1 || maxSize < minSize) {
            throw new IllegalArgumentException("Invalid batch size range " + minSize + ".." + maxSize);
        }
        List<List<T>> chunks = new ArrayList<>();
        for (int from = 0; from < items.size(); ) {
            int to = Math.min(items.size(), from + random.nextInt(minSize, maxSize + 1));
            chunks.add(items.subList(from, to));
            from = to;
        }
        return chunks;
    }
}
