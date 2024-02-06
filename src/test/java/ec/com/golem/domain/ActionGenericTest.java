package ec.com.golem.domain;

import static ec.com.golem.domain.ActionGenericTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ec.com.golem.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ActionGenericTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ActionGeneric.class);
        ActionGeneric actionGeneric1 = getActionGenericSample1();
        ActionGeneric actionGeneric2 = new ActionGeneric();
        assertThat(actionGeneric1).isNotEqualTo(actionGeneric2);

        actionGeneric2.setId(actionGeneric1.getId());
        assertThat(actionGeneric1).isEqualTo(actionGeneric2);

        actionGeneric2 = getActionGenericSample2();
        assertThat(actionGeneric1).isNotEqualTo(actionGeneric2);
    }
}
