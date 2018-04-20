package edu.harvard.h2ms.service;

import edu.harvard.h2ms.domain.core.Location;
import java.util.Set;

public interface LocationService {
  public Set<Location> findTopLevel();
}
