/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.sync.diffsync;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import lombok.NoArgsConstructor;
import org.springframework.sync.Patch;

/**
 * Registry for looking up {@link PersistenceCallback}s to be used in the course of applying a {@link Patch} via
 * Differential Synchronization.
 *
 * @author Craig Walls
 */
@NoArgsConstructor
public class PersistenceCallbackRegistry implements IPersistenceCallbackRegistry {

  private final Map<String, PersistenceCallback<? extends Serializable>> persistenceCallbacks = new HashMap<>();

  /**
   * Adds a {@link PersistenceCallback} to the registry with a key that is pluralized by the pluralize() method.
   *
   * @param persistenceCallback the {@link PersistenceCallback} to add to the registry.
   */
  @Override
  public void addPersistenceCallback(PersistenceCallback<? extends Serializable> persistenceCallback) {
    Class<?> entityType = persistenceCallback.getEntityType();
    String key = pluralize(entityType.getSimpleName());
    persistenceCallbacks.put(key, persistenceCallback);
  }

  /**
   * Looks up a {@link PersistenceCallback} from the registry.
   *
   * @param key the key that the {@link PersistenceCallback} has been registered under.
   * @return the {@link PersistenceCallback}
   */
  @Override
  public PersistenceCallback<? extends Serializable> findPersistenceCallback(String key) {
    return persistenceCallbacks.get(key);
  }

  /**
   * Pluralizes an entity's type name. Default implementation is to naively add an 's' to the end of the given String.
   * Override to implement a more elegant pluralization technique.
   *
   * @param entityTypeName the entity type name to be pluralized.
   * @return the pluralized type name.
   */
  protected String pluralize(String entityTypeName) {
    return entityTypeName.toLowerCase() + "s";
  }

}
