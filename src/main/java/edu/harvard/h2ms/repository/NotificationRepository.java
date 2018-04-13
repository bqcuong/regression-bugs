package edu.harvard.h2ms.repository;

import edu.harvard.h2ms.domain.core.Notification;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "notifications", path = "notifications")
public interface NotificationRepository {
  Notification findOneByName(String name);
}
