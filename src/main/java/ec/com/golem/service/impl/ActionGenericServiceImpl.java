package ec.com.golem.service.impl;

import ec.com.golem.domain.ActionGeneric;
import ec.com.golem.repository.ActionGenericRepository;
import ec.com.golem.repository.search.ActionGenericSearchRepository;
import ec.com.golem.service.ActionGenericService;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ec.com.golem.domain.ActionGeneric}.
 */
@Service
@Transactional
public class ActionGenericServiceImpl implements ActionGenericService {

    private final Logger log = LoggerFactory.getLogger(ActionGenericServiceImpl.class);

    private final ActionGenericRepository actionGenericRepository;

    private final ActionGenericSearchRepository actionGenericSearchRepository;

    public ActionGenericServiceImpl(
        ActionGenericRepository actionGenericRepository,
        ActionGenericSearchRepository actionGenericSearchRepository
    ) {
        this.actionGenericRepository = actionGenericRepository;
        this.actionGenericSearchRepository = actionGenericSearchRepository;
    }

    @Override
    public ActionGeneric save(ActionGeneric actionGeneric) {
        log.debug("Request to save ActionGeneric : {}", actionGeneric);
        ActionGeneric result = actionGenericRepository.save(actionGeneric);
        actionGenericSearchRepository.index(result);
        return result;
    }

    @Override
    public ActionGeneric update(ActionGeneric actionGeneric) {
        log.debug("Request to update ActionGeneric : {}", actionGeneric);
        ActionGeneric result = actionGenericRepository.save(actionGeneric);
        actionGenericSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<ActionGeneric> partialUpdate(ActionGeneric actionGeneric) {
        log.debug("Request to partially update ActionGeneric : {}", actionGeneric);

        return actionGenericRepository
            .findById(actionGeneric.getId())
            .map(existingActionGeneric -> {
                if (actionGeneric.getMessage() != null) {
                    existingActionGeneric.setMessage(actionGeneric.getMessage());
                }

                return existingActionGeneric;
            })
            .map(actionGenericRepository::save)
            .map(savedActionGeneric -> {
                actionGenericSearchRepository.index(savedActionGeneric);
                return savedActionGeneric;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionGeneric> findAll() {
        log.debug("Request to get all ActionGenerics");
        return actionGenericRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ActionGeneric> findOne(Long id) {
        log.debug("Request to get ActionGeneric : {}", id);
        return actionGenericRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete ActionGeneric : {}", id);
        actionGenericRepository.deleteById(id);
        actionGenericSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ActionGeneric> search(String query) {
        log.debug("Request to search ActionGenerics for query {}", query);
        try {
            return StreamSupport.stream(actionGenericSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
