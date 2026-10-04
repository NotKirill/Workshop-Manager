package notkirill.workshopmanager.common;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RandomBatchesTest {
    @Test
    void chunksStayInRangeAndKeepEveryItemInOrder() {
        List<Integer> items = IntStream.range(0, 1_000).boxed().toList();

        List<List<Integer>> chunks = RandomBatches.split(items, 70, 100, new Random(42));

        assertThat(chunks.subList(0, chunks.size() - 1)).allSatisfy(chunk -> assertThat(chunk).hasSizeBetween(70, 100));
        assertThat(chunks.getLast()).hasSizeBetween(1, 100);
        assertThat(chunks.stream().flatMap(List::stream).toList()).isEqualTo(items);
        assertThat(chunks.stream().map(List::size).distinct().count()).isGreaterThan(1);
    }

    @Test
    void emptyListGivesNoChunks() {
        assertThat(RandomBatches.split(List.of(), 70, 100, new Random(1))).isEmpty();
    }

    @Test
    void rejectsInvalidRange() {
        assertThatThrownBy(() -> RandomBatches.split(List.of(1), 10, 5, new Random(1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
