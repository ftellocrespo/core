package ec.com.golem.domain;

import static ec.com.golem.domain.ActionTestSamples.*;
import static ec.com.golem.domain.ActivityTestSamples.*;
import static ec.com.golem.domain.ProcessTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import ec.com.golem.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ActivityTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Activity.class);
        Activity activity1 = getActivitySample1();
        Activity activity2 = new Activity();
        assertThat(activity1).isNotEqualTo(activity2);

        activity2.setId(activity1.getId());
        assertThat(activity1).isEqualTo(activity2);

        activity2 = getActivitySample2();
        assertThat(activity1).isNotEqualTo(activity2);
    }

    @Test
    void actionTest() throws Exception {
        Activity activity = getActivityRandomSampleGenerator();
        Action actionBack = getActionRandomSampleGenerator();

        activity.addAction(actionBack);
        assertThat(activity.getActions()).containsOnly(actionBack);
        assertThat(actionBack.getActivity()).isEqualTo(activity);

        activity.removeAction(actionBack);
        assertThat(activity.getActions()).doesNotContain(actionBack);
        assertThat(actionBack.getActivity()).isNull();

        activity.actions(new HashSet<>(Set.of(actionBack)));
        assertThat(activity.getActions()).containsOnly(actionBack);
        assertThat(actionBack.getActivity()).isEqualTo(activity);

        activity.setActions(new HashSet<>());
        assertThat(activity.getActions()).doesNotContain(actionBack);
        assertThat(actionBack.getActivity()).isNull();
    }

    @Test
    void processTest() throws Exception {
        Activity activity = getActivityRandomSampleGenerator();
        Process processBack = getProcessRandomSampleGenerator();

        activity.setProcess(processBack);
        assertThat(activity.getProcess()).isEqualTo(processBack);

        activity.process(null);
        assertThat(activity.getProcess()).isNull();
    }
}
