package org.springframework.sync.diffsync.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.sync.Diff;
import org.springframework.sync.DiffSyncService;
import org.springframework.sync.IDiffSyncService;
import org.springframework.sync.diffsync.DiffSync;
import org.springframework.sync.diffsync.Equivalency;
import org.springframework.sync.diffsync.IdPropertyEquivalency;
import org.springframework.sync.diffsync.PersistenceCallbackRegistry;
import org.springframework.sync.diffsync.shadowstore.MapBasedShadowStore;
import org.springframework.sync.diffsync.shadowstore.ShadowStore;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration @ConditionalOnClass({ Diff.class, DiffSync.class }) public class SpringSyncAutoConfiguration {

  private List<DiffSyncConfigurer> diffSyncConfigurers = new ArrayList<>();

  @Autowired
  public void setDiffSyncConfigurers(List<DiffSyncConfigurer> diffSyncConfigurers) {
    Assert.notNull(diffSyncConfigurers, "At least one configuration class must implement DiffSyncConfigurer");
    Assert.notEmpty(diffSyncConfigurers, "At least one configuration class must implement DiffSyncConfigurer");
    this.diffSyncConfigurers = diffSyncConfigurers;
  }

  @Bean
  @ConditionalOnMissingBean(PersistenceCallbackRegistry.class)
  public PersistenceCallbackRegistry persistenceCallbackRegistry() {
    PersistenceCallbackRegistry registry = new PersistenceCallbackRegistry();
    for (DiffSyncConfigurer diffSyncConfigurer : diffSyncConfigurers) {
      diffSyncConfigurer.addPersistenceCallbacks(registry);
    }
    return registry;
  }

  @Bean
  @ConditionalOnMissingBean(ShadowStore.class)
  public ShadowStore shadowStore() {
    return new MapBasedShadowStore(UUID.randomUUID().toString());
  }

  @Bean
  @ConditionalOnMissingBean(Equivalency.class)
  public Equivalency equivalency() {
    return new IdPropertyEquivalency();
  }

  @Bean
  public IDiffSyncService diffSyncService(ShadowStore shadowStore, Equivalency equivalency,
      PersistenceCallbackRegistry persistenceCallbackRegistry) {
    return new DiffSyncService(shadowStore, equivalency, persistenceCallbackRegistry);
  }
}
