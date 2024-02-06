package ec.com.golem.service.impl;

import ec.com.golem.domain.Process;
import ec.com.golem.repository.ProcessRepository;
import ec.com.golem.repository.search.ProcessSearchRepository;
import ec.com.golem.service.ProcessService;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ec.com.golem.domain.Process}.
 */
@Service
@Transactional
public class ProcessServiceImpl implements ProcessService {

    private final Logger log = LoggerFactory.getLogger(ProcessServiceImpl.class);

    private final ProcessRepository processRepository;

    private final ProcessSearchRepository processSearchRepository;

    public ProcessServiceImpl(ProcessRepository processRepository, ProcessSearchRepository processSearchRepository) {
        this.processRepository = processRepository;
        this.processSearchRepository = processSearchRepository;
    }

    @Override
    public Process save(Process process) {
        log.debug("Request to save Process : {}", process);
        Process result = processRepository.save(process);
        processSearchRepository.index(result);
        return result;
    }

    @Override
    public Process update(Process process) {
        log.debug("Request to update Process : {}", process);
        Process result = processRepository.save(process);
        processSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<Process> partialUpdate(Process process) {
        log.debug("Request to partially update Process : {}", process);

        return processRepository
            .findById(process.getId())
            .map(existingProcess -> {
                if (process.getName() != null) {
                    existingProcess.setName(process.getName());
                }
                if (process.getMeta() != null) {
                    existingProcess.setMeta(process.getMeta());
                }

                return existingProcess;
            })
            .map(processRepository::save)
            .map(savedProcess -> {
                processSearchRepository.index(savedProcess);
                return savedProcess;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Process> findAll() {
        log.debug("Request to get all Processes");
        return processRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Process> findOne(Long id) {
        log.debug("Request to get Process : {}", id);
        return processRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete Process : {}", id);
        processRepository.deleteById(id);
        processSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Process> search(String query) {
        log.debug("Request to search Processes for query {}", query);
        try {
            return StreamSupport.stream(processSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
