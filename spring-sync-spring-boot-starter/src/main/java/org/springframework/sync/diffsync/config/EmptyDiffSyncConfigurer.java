package org.springframework.sync.diffsync.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.sync.diffsync.PersistenceCallbackRegistry;
import org.springframwork.sync.config.DiffSyncConfigurer;

@Configuration
public class EmptyDiffSyncConfigurer implements DiffSyncConfigurer {
  @Override
  public void addPersistenceCallbacks(final PersistenceCallbackRegistry registry) {
    // default implementation so the application will start
  }
}
