package notkirill.workshopmanager.modspace.steam;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SteamPropertiesTest {
    private static <T> Set<ConstraintViolation<T>> validate(T object) {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            return factory.getValidator().validate(object);
        }
    }

    @Test
    void batchSizesDefaultTo70To100() {
        SteamProperties properties = new SteamProperties(null, null, null, null);

        assertThat(properties.batchMinSize()).isEqualTo(70);
        assertThat(properties.batchMaxSize()).isEqualTo(100);
    }

    @Test
    void maxAboveSteamLimitIsRejected() {
        SteamProperties properties = new SteamProperties(null, null, 101, null);

        assertThat(validate(properties)).extracting(violation -> violation.getPropertyPath().toString())
                .containsExactly("batchMaxSize");
    }

    @Test
    void steamLimitItselfIsAccepted() {
        SteamProperties properties = new SteamProperties(null, 100, 100, null);

        assertThat(validate(properties)).isEmpty();
    }

    @Test
    void smallMaxLowersTheDefaultMin() {
        SteamProperties properties = new SteamProperties(null, null, 20, null);

        assertThat(properties.batchMinSize()).isEqualTo(20);
    }

    @Test
    void minAboveMaxIsRejected() {
        assertThatThrownBy(() -> new SteamProperties(null, 90, 50, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
