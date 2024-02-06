package ec.com.golem.domain;

import static ec.com.golem.domain.ProcessTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ec.com.golem.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProcessTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Process.class);
        Process process1 = getProcessSample1();
        Process process2 = new Process();
        assertThat(process1).isNotEqualTo(process2);

        process2.setId(process1.getId());
        assertThat(process1).isEqualTo(process2);

        process2 = getProcessSample2();
        assertThat(process1).isNotEqualTo(process2);
    }
}
