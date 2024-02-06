package ec.com.golem.service.impl;

import ec.com.golem.domain.UserX;
import ec.com.golem.repository.UserXRepository;
import ec.com.golem.repository.search.UserXSearchRepository;
import ec.com.golem.service.UserXService;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ec.com.golem.domain.UserX}.
 */
@Service
@Transactional
public class UserXServiceImpl implements UserXService {

    private final Logger log = LoggerFactory.getLogger(UserXServiceImpl.class);

    private final UserXRepository userXRepository;

    private final UserXSearchRepository userXSearchRepository;

    public UserXServiceImpl(UserXRepository userXRepository, UserXSearchRepository userXSearchRepository) {
        this.userXRepository = userXRepository;
        this.userXSearchRepository = userXSearchRepository;
    }

    @Override
    public UserX save(UserX userX) {
        log.debug("Request to save UserX : {}", userX);
        UserX result = userXRepository.save(userX);
        userXSearchRepository.index(result);
        return result;
    }

    @Override
    public UserX update(UserX userX) {
        log.debug("Request to update UserX : {}", userX);
        UserX result = userXRepository.save(userX);
        userXSearchRepository.index(result);
        return result;
    }

    @Override
    public Optional<UserX> partialUpdate(UserX userX) {
        log.debug("Request to partially update UserX : {}", userX);

        return userXRepository
            .findById(userX.getId())
            .map(existingUserX -> {
                if (userX.getEmail() != null) {
                    existingUserX.setEmail(userX.getEmail());
                }
                if (userX.getPassword() != null) {
                    existingUserX.setPassword(userX.getPassword());
                }
                if (userX.getFacebookId() != null) {
                    existingUserX.setFacebookId(userX.getFacebookId());
                }
                if (userX.getGoogleId() != null) {
                    existingUserX.setGoogleId(userX.getGoogleId());
                }
                if (userX.getFirstName() != null) {
                    existingUserX.setFirstName(userX.getFirstName());
                }
                if (userX.getLastName() != null) {
                    existingUserX.setLastName(userX.getLastName());
                }
                if (userX.getPhone() != null) {
                    existingUserX.setPhone(userX.getPhone());
                }
                if (userX.getBirth() != null) {
                    existingUserX.setBirth(userX.getBirth());
                }
                if (userX.getGender() != null) {
                    existingUserX.setGender(userX.getGender());
                }
                if (userX.getNationality() != null) {
                    existingUserX.setNationality(userX.getNationality());
                }
                if (userX.getAddress() != null) {
                    existingUserX.setAddress(userX.getAddress());
                }
                if (userX.getState() != null) {
                    existingUserX.setState(userX.getState());
                }

                return existingUserX;
            })
            .map(userXRepository::save)
            .map(savedUserX -> {
                userXSearchRepository.index(savedUserX);
                return savedUserX;
            });
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserX> findAll() {
        log.debug("Request to get all UserXES");
        return userXRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserX> findOne(Long id) {
        log.debug("Request to get UserX : {}", id);
        return userXRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete UserX : {}", id);
        userXRepository.deleteById(id);
        userXSearchRepository.deleteFromIndexById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserX> search(String query) {
        log.debug("Request to search UserXES for query {}", query);
        try {
            return StreamSupport.stream(userXSearchRepository.search(query).spliterator(), false).toList();
        } catch (RuntimeException e) {
            throw e;
        }
    }
}
