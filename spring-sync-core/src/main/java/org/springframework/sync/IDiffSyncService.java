package org.springframework.sync;

public interface IDiffSyncService {
  Patch patch(String resource, String resourceId, final String shadowStoreId, Patch patch);

  Patch patch(String resource, Patch patch);
}
