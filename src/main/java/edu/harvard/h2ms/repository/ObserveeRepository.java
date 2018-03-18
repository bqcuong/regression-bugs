package edu.harvard.h2ms.repository;

import edu.harvard.h2ms.domain.core.Observee;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface ObserveeRepository extends PagingAndSortingRepository<Observee, Long> {
}
