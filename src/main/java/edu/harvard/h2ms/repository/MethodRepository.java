package edu.harvard.h2ms.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import edu.harvard.h2ms.domain.core.Method;

@RepositoryRestResource(collectionResourceRel = "methods", path = "methods")
public interface MethodRepository extends PagingAndSortingRepository<Method, Long> {
    List<Method> findByName(@Param("name") String name);
}
