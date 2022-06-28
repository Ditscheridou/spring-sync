package org.springframework.shadowstore;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;

@RequiredArgsConstructor
public class ShadowStoreFactory {

  private final Class<? extends ShadowStore> shadowStore;

  public ShadowStore getShadowStore(String id)
      throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
    return shadowStore.getConstructor(String.class).newInstance(id);
  }
}
