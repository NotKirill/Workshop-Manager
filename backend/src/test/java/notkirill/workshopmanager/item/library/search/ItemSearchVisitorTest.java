package notkirill.workshopmanager.item.library.search;

import notkirill.workshopmanager.item.library.search.antlr.ItemSearchGrammarLexer;
import notkirill.workshopmanager.item.library.search.antlr.ItemSearchGrammarParser;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemSearchVisitorTest {
    private final ItemSearchPredicateBuilder predicateBuilder = mock(ItemSearchPredicateBuilder.class);

    @Test
    void unescapesQuotesAndBackslashes() {
        String query = "\"say \\\"hi\\\" C:\\\\\"";

        List<String> values = searchedValues(query, 1);

        assertThat(values).containsExactly("say \"hi\" C:\\");
    }

    @Test
    void phraseEndingWithBackslashDoesNotSwallowTheNextPhrase() {
        String query = "\"C:\\\\\" \"maps\"";

        List<String> values = searchedValues(query, 2);

        assertThat(values).containsExactly("C:\\", "maps");
    }

    @Test
    void keepsOtherBackslashesAsTyped() {
        String query = "\"C:\\Games\"";

        List<String> values = searchedValues(query, 1);

        assertThat(values).containsExactly("C:\\Games");
    }

    private List<String> searchedValues(String query, int expectedCount) {
        ItemSearchGrammarParser parser = new ItemSearchGrammarParser(
                new CommonTokenStream(new ItemSearchGrammarLexer(CharStreams.fromString(query))));
        parser.setErrorHandler(new SearchQueryErrorStrategy());
        ItemSearchVisitor visitor = new ItemSearchVisitor(
                predicateBuilder, new ItemSearchProperties(null, null, null, null));

        visitor.visit(parser.query()).toPredicate(null, null, mock());

        ArgumentCaptor<String> values = ArgumentCaptor.forClass(String.class);
        verify(predicateBuilder, times(expectedCount)).buildDefaultSearch(any(), any(), any(), values.capture());
        return values.getAllValues();
    }
}
