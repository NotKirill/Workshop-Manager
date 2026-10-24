package notkirill.workshopmanager.item.library.search;

import notkirill.workshopmanager.exception.BadRequestException;
import org.antlr.v4.runtime.*;

public class SearchQueryErrorStrategy extends DefaultErrorStrategy {
    @Override
    public void recover(Parser recognizer, RecognitionException e) {
        for (ParserRuleContext context = recognizer.getContext(); context != null; context = context.getParent()) {
            context.exception = e;
        }

        throw new BadRequestException("INVALID_SEARCH_QUERY", "Invalid search query", e);
    }

    @Override
    public void sync(Parser recognizer) {
        // NO-OP: Make sure we don't attempt to recover inline; if the parser successfully recovers, it won't throw an exception.
    }

    @Override
    public Token recoverInline(Parser recognizer) throws RecognitionException {
        InputMismatchException e = new InputMismatchException(recognizer);
        for (ParserRuleContext context = recognizer.getContext(); context != null; context = context.getParent()) {
            context.exception = e;
        }

        throw new BadRequestException("INVALID_SEARCH_QUERY", "Invalid search query", e);
    }
}
