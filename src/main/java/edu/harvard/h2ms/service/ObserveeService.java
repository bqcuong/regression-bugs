package edu.harvard.h2ms.service;

import edu.harvard.h2ms.domain.core.Observee;
import java.util.List;

/**
 * Observee service...
 */
public interface ObserveeService {

    // Save an observee
    Observee save(Observee observee);

    // Finds all observee
    List<Observee> findAll();

    // Find an observee by its ID
    Observee findById(Long id);


}
