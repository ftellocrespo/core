package ec.com.golem.service.impl;

import ec.com.golem.domain.Action;
import ec.com.golem.repository.ActionRepository;
import ec.com.golem.repository.search.ActionSearchRepository;
import ec.com.golem.service.ActionService;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ec.com.golem.domain.Action}.
 */
@Service
@Transactional
public class ActionServiceImpl implements ActionService {

    private final Logger log = LoggerFactory.getLogger(ActionServiceImpl.class);

    private final ActionRepository actionRepository;

    private final ActionSearchRepository actionSearchRepository;

    public ActionServiceImpl(ActionRepository actionRepository, ActionSearchRepository actionSearchRepository) {
        this.actionRepository = actionRepository;
        this.actionSearchRepository = actionSearchRepository;
    }

    @Override
    public Action save(Action action) {
        log.debug("Request to save Action : {}", action);
        Action result = actionRepository.save(action);
        actionSearchRepository.index(result);
        return result;
    }

    @Override
    public Action update(Action action) {
        log.debug("Request to update Action : {}", action);
        Action result = actionRepository.save(action);
        actionSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Action> partialUpdate(Action action) {
        log.debug("Request to partially update Action : {}", action);

        return actionRepository
            .findById(action.getId())
            .map(existingAction -> {
                if (action.getName() != null) {
                    existingAction.setName(action.getName());
                }

                return existingAction;
            })
            .map(actionRepository::save)
            .map(savedAction -> {
                actionSearchRepository.index(savedAction);
                return savedAction;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Action> findAll() {
        log.debug("Request to get all Actions");
        return actionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Action> findOne(Long id) {
        log.debug("Request to get Action : {}", id);
        return actionRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Action : {}", id);
        actionRepository.deleteById(id);
        actionSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Action> search(String query) {
        log.debug("Request to search Actions for query {}", query);
        try {
            return StreamSupport.stream(actionSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
