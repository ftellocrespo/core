package ec.com.golem.domain;

import static ec.com.golem.domain.ErrorLogTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ec.com.golem.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ErrorLogTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ErrorLog.class);
        ErrorLog errorLog1 = getErrorLogSample1();
        ErrorLog errorLog2 = new ErrorLog();
        assertThat(errorLog1).isNotEqualTo(errorLog2);

        errorLog2.setId(errorLog1.getId());
        assertThat(errorLog1).isEqualTo(errorLog2);

        errorLog2 = getErrorLogSample2();
        assertThat(errorLog1).isNotEqualTo(errorLog2);
    }
}
