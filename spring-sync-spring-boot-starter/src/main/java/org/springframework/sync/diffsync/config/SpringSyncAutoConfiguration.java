package org.springframework.sync.diffsync.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.shadowstore.MapBasedShadowStore;
import org.springframework.shadowstore.ShadowStore;
import org.springframework.shadowstore.ShadowStoreFactory;
import org.springframework.sync.DiffSyncService;
import org.springframework.sync.IDiffSyncService;
import org.springframework.sync.diffsync.DiffSync;
import org.springframework.sync.diffsync.Equivalency;
import org.springframework.sync.diffsync.IdPropertyEquivalency;
import org.springframework.sync.diffsync.PersistenceCallbackRegistry;
import org.springframwork.sync.config.DiffSyncConfigurer;

import java.util.List;

@Configuration
@ConditionalOnClass({ DiffSyncService.class, DiffSync.class })
public class SpringSyncAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean(PersistenceCallbackRegistry.class)
  public PersistenceCallbackRegistry persistenceCallbackRegistry(List<DiffSyncConfigurer> configurers) {
    final PersistenceCallbackRegistry persistenceCallbackRegistry = new PersistenceCallbackRegistry();
    configurers.forEach(
        diffSyncConfigurer -> diffSyncConfigurer.addPersistenceCallbacks(persistenceCallbackRegistry));
    return persistenceCallbackRegistry;
  }

  @Bean
  @ConditionalOnMissingBean(ShadowStore.class)
  public ShadowStoreFactory mapBasedShadowStore() {
    return new ShadowStoreFactory(MapBasedShadowStore.class);
  }

  @Bean
  @ConditionalOnMissingBean(Equivalency.class)
  public Equivalency equivalency() {
    return new IdPropertyEquivalency();
  }

  @Bean
  public IDiffSyncService diffSyncService(ShadowStoreFactory shadowStoreFactory, Equivalency equivalency,
      PersistenceCallbackRegistry persistenceCallbackRegistry) {
    return new DiffSyncService(shadowStoreFactory, equivalency, persistenceCallbackRegistry);
  }
}
