package notkirill.workshopmanager.item.library.search;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DateRangeParser {
    private static final Pattern RELATIVE_AGO_PATTERN =
            Pattern.compile("^(\\d+)_(day|days|week|weeks|month|months|year|years)_ago$", Pattern.CASE_INSENSITIVE);
    private static final Pattern YYYY_MM_DD = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");
    private static final Pattern YYYY_MM = Pattern.compile("^\\d{4}-\\d{2}$");
    private static final Pattern YYYY = Pattern.compile("^\\d{4}$");

    private final Clock clock;

    @Autowired
    public DateRangeParser(Clock clock) {
        this.clock = clock;
    }

    public DateRange parse(String raw) {
        OffsetDateTime now = OffsetDateTime.now(clock);

        return switch (raw.toLowerCase(Locale.ROOT)) {
            case "day" -> new DateRange(now.minusDays(1), now, true);
            case "week" -> new DateRange(now.minusWeeks(1), now, true);
            case "month" -> new DateRange(now.minusMonths(1), now, true);
            case "year" -> new DateRange(now.minusYears(1), now, true);
            default -> parsePattern(raw, now);
        };
    }

    private DateRange parsePattern(String raw, OffsetDateTime now) {
        ZoneId zone = clock.getZone();

        Matcher matcher = RELATIVE_AGO_PATTERN.matcher(raw);
        if (matcher.matches()) {
            int amount = Integer.parseInt(matcher.group(1));
            String unit = matcher.group(2).toLowerCase(Locale.ROOT);

            LocalDate targetDay = switch (unit) {
                case "day", "days" -> now.toLocalDate().minusDays(amount);
                case "week", "weeks" -> now.toLocalDate().minusWeeks(amount);
                case "month", "months" -> now.toLocalDate().minusMonths(amount);
                case "year", "years" -> now.toLocalDate().minusYears(amount);
                default -> now.toLocalDate();
            };

            return toDateRange(targetDay.atStartOfDay(zone), targetDay.plusDays(1).atStartOfDay(zone), true);
        }

        if (YYYY_MM_DD.matcher(raw).matches()) {
            LocalDate date = LocalDate.parse(raw);
            return toDateRange(date.atStartOfDay(zone), date.plusDays(1).atStartOfDay(zone), false);
        }

        if (YYYY_MM.matcher(raw).matches()) {
            YearMonth yearMonth = YearMonth.parse(raw);
            return toDateRange(
                    yearMonth.atDay(1).atStartOfDay(zone),
                    yearMonth.plusMonths(1).atDay(1).atStartOfDay(zone),
                    false
            );
        }

        if (YYYY.matcher(raw).matches()) {
            int year = Integer.parseInt(raw);
            return toDateRange(
                    LocalDate.of(year, 1, 1).atStartOfDay(zone),
                    LocalDate.of(year + 1, 1, 1).atStartOfDay(zone),
                    false
            );
        }

        throw new DateTimeException("Unsupported date: " + raw);
    }

    private DateRange toDateRange(ZonedDateTime start, ZonedDateTime endExclusive, boolean isRolling) {
        return new DateRange(
                start.toOffsetDateTime(),
                endExclusive.toOffsetDateTime(),
                isRolling
        );
    }

    public record DateRange(OffsetDateTime start, OffsetDateTime end, boolean isRolling) {
        public DateRange(OffsetDateTime start, OffsetDateTime end) {
            this(start, end, false);
        }
    }
}