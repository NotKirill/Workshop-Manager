package notkirill.workshopmanager.item.library.search;

import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DateRangeParserTest {
    private final DateRangeParser parser =
            new DateRangeParser(Clock.fixed(Instant.parse("2025-01-15T15:15:00Z"), ZoneOffset.UTC));

    @Test
    void parsesCalendarPeriodsAsHalfOpenRanges() {
        DateRangeParser.DateRange month = parser.parse("2024-03");
        assertThat(month.start()).isEqualTo(OffsetDateTime.parse("2024-03-01T00:00Z"));
        assertThat(month.end()).isEqualTo(OffsetDateTime.parse("2024-04-01T00:00Z"));
        assertThat(month.isRolling()).isFalse();

        DateRangeParser.DateRange year = parser.parse("2024");
        assertThat(year.end()).isEqualTo(OffsetDateTime.parse("2025-01-01T00:00Z"));
    }

    @Test
    void parsesRollingWindows() {
        DateRangeParser.DateRange week = parser.parse("week");
        assertThat(week.isRolling()).isTrue();
        assertThat(week.start()).isEqualTo(OffsetDateTime.parse("2025-01-08T15:15Z"));
    }

    @Test
    void parsesRelativeDays() {
        DateRangeParser.DateRange day = parser.parse("3_days_ago");
        assertThat(day.start()).isEqualTo(OffsetDateTime.parse("2025-01-12T00:00Z"));
        assertThat(day.end()).isEqualTo(OffsetDateTime.parse("2025-01-13T00:00Z"));
    }

    @Test
    void rejectsUnsupportedValues() {
        assertThatThrownBy(() -> parser.parse("yesterday")).isInstanceOf(DateTimeException.class);
    }
}
