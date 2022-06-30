package de.jds;

import org.springframework.context.annotation.Configuration;
import org.springframework.sync.diffsync.PersistenceCallbackRegistry;
import org.springframwork.sync.config.DiffSyncConfigurer;

@Configuration
public class DemoSynConfigurer implements DiffSyncConfigurer {
  @Override
  public void addPersistenceCallbacks(final PersistenceCallbackRegistry persistenceCallbackRegistry) {
    persistenceCallbackRegistry.addPersistenceCallback(new DemoPersistenceCallback());
  }
}
