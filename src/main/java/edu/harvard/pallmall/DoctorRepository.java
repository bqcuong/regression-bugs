package edu.harvard.pallmall;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "doctors", path = "doctors")
public interface DoctorRepository extends PagingAndSortingRepository<Doctor, Long> {
    List<Doctor> findByLastName(@Param("lastName") String lastName);
}
