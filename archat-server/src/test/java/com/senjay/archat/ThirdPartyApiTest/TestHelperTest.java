package com.senjay.archat.ThirdPartyApiTest;

import com.senjay.archat.common.service.TestHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TestHelperTest {

    private final TestHelper testHelper = new TestHelper();

    @Test
    @DisplayName("a < b 时应返回 a + b")
    void shouldReturnSumWhenALessThanB() {
        int result = testHelper.sum(2, 5);
        assertThat(result).isEqualTo(7);
    }

    @Test
    @DisplayName("a >= b 时应返回 a - b")
    void shouldReturnDiffWhenAGreaterOrEqualB() {
        int result = testHelper.sum(5, 2);
        assertThat(result).isEqualTo(3);
    }

    @Test
    @DisplayName("a = b 时也应返回 a - b，即 0")
    void shouldReturnZeroWhenAEqualsB() {
        int result = testHelper.sum(3, 3);
        assertThat(result).isEqualTo(0);
    }
}
