package ec.com.golem.service.impl;

import ec.com.golem.domain.Activity;
import ec.com.golem.repository.ActivityRepository;
import ec.com.golem.repository.search.ActivitySearchRepository;
import ec.com.golem.service.ActivityService;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ec.com.golem.domain.Activity}.
 */
@Service
@Transactional
public class ActivityServiceImpl implements ActivityService {

    private final Logger log = LoggerFactory.getLogger(ActivityServiceImpl.class);

    private final ActivityRepository activityRepository;

    private final ActivitySearchRepository activitySearchRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository, ActivitySearchRepository activitySearchRepository) {
        this.activityRepository = activityRepository;
        this.activitySearchRepository = activitySearchRepository;
    }

    @Override
    public Activity save(Activity activity) {
        log.debug("Request to save Activity : {}", activity);
        Activity result = activityRepository.save(activity);
        activitySearchRepository.index(result);
        return result;
    }

    @Override
    public Activity update(Activity activity) {
        log.debug("Request to update Activity : {}", activity);
        Activity result = activityRepository.save(activity);
        activitySearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Activity> partialUpdate(Activity activity) {
        log.debug("Request to partially update Activity : {}", activity);

        return activityRepository
            .findById(activity.getId())
            .map(existingActivity -> {
                if (activity.getType() != null) {
                    existingActivity.setType(activity.getType());
                }

                return existingActivity;
            })
            .map(activityRepository::save)
            .map(savedActivity -> {
                activitySearchRepository.index(savedActivity);
                return savedActivity;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Activity> findAll() {
        log.debug("Request to get all Activities");
        return activityRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Activity> findOne(Long id) {
        log.debug("Request to get Activity : {}", id);
        return activityRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Activity : {}", id);
        activityRepository.deleteById(id);
        activitySearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Activity> search(String query) {
        log.debug("Request to search Activities for query {}", query);
        try {
            return StreamSupport.stream(activitySearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
