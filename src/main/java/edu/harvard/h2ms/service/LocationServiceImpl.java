package edu.harvard.h2ms.service;

import edu.harvard.h2ms.domain.core.*;
import edu.harvard.h2ms.repository.LocationRepository;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("locationService")
public class LocationServiceImpl implements LocationService {
  final Logger log = LoggerFactory.getLogger(LocationServiceImpl.class);

  @Autowired private LocationRepository locationRepository;

  @Override
  public Set<Location> findTopLevel() {
    return locationRepository.findByParent(null);
  }
}
