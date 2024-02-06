package ec.com.golem.domain;

import static ec.com.golem.domain.ActionTestSamples.*;
import static ec.com.golem.domain.ActivityTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ec.com.golem.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ActionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Action.class);
        Action action1 = getActionSample1();
        Action action2 = new Action();
        assertThat(action1).isNotEqualTo(action2);

        action2.setId(action1.getId());
        assertThat(action1).isEqualTo(action2);

        action2 = getActionSample2();
        assertThat(action1).isNotEqualTo(action2);
    }

    @Test
    void activityTest() throws Exception {
        Action action = getActionRandomSampleGenerator();
        Activity activityBack = getActivityRandomSampleGenerator();

        action.setActivity(activityBack);
        assertThat(action.getActivity()).isEqualTo(activityBack);

        action.activity(null);
        assertThat(action.getActivity()).isNull();
    }
}
