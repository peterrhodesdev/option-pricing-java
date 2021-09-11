package dev.peterrhodes.optionpricing.options;

import dev.peterrhodes.optionpricing.enums.OptionType;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EuropeanOptionTest {

    @Test
    void zeroSpotPrice() {
        // Arrange Act Assert
        assertThatThrownBy(() -> {
            EuropeanOption ex = new EuropeanOption(OptionType.CALL, 0.0, 100.0, 1.0, 0.25, 0.1);
        }).isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("greater than zero");
    }
}
