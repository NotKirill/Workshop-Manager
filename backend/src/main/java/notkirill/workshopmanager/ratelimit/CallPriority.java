package notkirill.workshopmanager.ratelimit;

import java.util.function.Supplier;

public enum CallPriority {
    INTERACTIVE,
    BACKGROUND;

    private static final ScopedValue<CallPriority> CURRENT = ScopedValue.newInstance();

    public static CallPriority current() {
        return CURRENT.orElse(INTERACTIVE);
    }

    public void run(Runnable action) {
        ScopedValue.where(CURRENT, this).run(action);
    }

    public <T> T call(Supplier<T> action) {
        return ScopedValue.where(CURRENT, this).call(action::get);
    }
}
