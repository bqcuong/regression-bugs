package edu.harvard.h2ms.repository;

import java.util.List;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import edu.harvard.h2ms.domain.core.Location;


@RepositoryRestResource(collectionResourceRel = "locations", path = "locations")
public interface LocationRepository extends PagingAndSortingRepository<Location, Long> {
	List<Location> findByHospitalName(@Param("hospitalName") String hospitalName);
	List<Location> findByWardName(@Param("hardName") String wardName);
}

