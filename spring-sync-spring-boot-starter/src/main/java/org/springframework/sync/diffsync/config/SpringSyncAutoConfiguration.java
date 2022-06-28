package org.springframework.sync.diffsync.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.shadowstore.MapBasedShadowStore;
import org.springframework.shadowstore.RedisShadowStore;
import org.springframework.shadowstore.ShadowStore;
import org.springframework.shadowstore.ShadowStoreFactory;
import org.springframework.sync.DiffSyncService;
import org.springframework.sync.IDiffSyncService;
import org.springframework.sync.diffsync.DiffSync;
import org.springframework.sync.diffsync.Equivalency;
import org.springframework.sync.diffsync.IdPropertyEquivalency;
import org.springframework.sync.diffsync.PersistenceCallbackRegistry;
import org.springframework.util.Assert;
import org.springframwork.sync.config.DiffSyncConfigurer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@ConditionalOnClass({ DiffSyncService.class, DiffSync.class })
public class SpringSyncAutoConfiguration {

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
    final PersistenceCallbackRegistry persistenceCallbackRegistry = new PersistenceCallbackRegistry();
    diffSyncConfigurers.forEach(
        diffSyncConfigurer -> diffSyncConfigurer.addPersistenceCallbacks(persistenceCallbackRegistry));
    return persistenceCallbackRegistry;
  }

  @Bean
  @ConditionalOnMissingBean(ShadowStore.class)
  @ConditionalOnBean(RedisTemplate.class)
  public ShadowStoreFactory redisShadowStore() {
    return new ShadowStoreFactory(RedisShadowStore.class);
  }

  @Bean
  @ConditionalOnMissingBean(ShadowStore.class)
  @ConditionalOnBean(MapBasedShadowStore.class)
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
