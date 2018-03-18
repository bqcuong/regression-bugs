package edu.harvard.h2ms.service;

import com.google.common.collect.Lists;
import edu.harvard.h2ms.domain.core.Observee;
import edu.harvard.h2ms.repository.ObserveeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service("observeeService")
@Repository
@Transactional
public class ObserveeServiceImpl implements ObserveeService {

    private ObserveeRepository observeeRepository;

    @Autowired
    public void setObserveeRepository(ObserveeRepository observeeRepository) {
        this.observeeRepository = observeeRepository;
    }

    // Saves a new observee
    public Observee save(Observee observee) {
        return observeeRepository.save(observee);
    }

    // Find all observee
    @Transactional(readOnly=true)
    public List<Observee> findAll() {
        return Lists.newArrayList(observeeRepository.findAll());
    }

    // Find an Observee by its ID
    @Transactional(readOnly=true)
    public Observee findById(Long id) {
        return observeeRepository.findOne(id);
    }

}
