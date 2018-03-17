package edu.harvard.h2ms.repository;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import edu.harvard.h2ms.domain.core.RelativeMoment;

@RepositoryRestResource(collectionResourceRel = "relative_moments", path = "relative_moments")
public interface RelativeMomentRepository extends PagingAndSortingRepository<RelativeMoment, Long>{

	RelativeMoment findByName( String Name);
}
