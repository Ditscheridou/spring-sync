package org.springframework.sync.diffsync;

import java.io.Serializable;

public interface IPersistenceCallbackRegistry {
  void addPersistenceCallback(PersistenceCallback<? extends Serializable> persistenceCallback);

  PersistenceCallback<? extends Serializable> findPersistenceCallback(String key);
}
