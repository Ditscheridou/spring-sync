package org.springframework.sync;

import lombok.RequiredArgsConstructor;
import org.springframework.sync.diffsync.DiffSync;
import org.springframework.sync.diffsync.Equivalency;
import org.springframework.sync.diffsync.PersistenceCallback;
import org.springframework.sync.diffsync.PersistenceCallbackRegistry;
import org.springframework.sync.diffsync.shadowstore.ShadowStore;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class DiffSyncService implements IDiffSyncService {

  private final ShadowStore shadowStore;

  private final Equivalency equivalency;

  private final PersistenceCallbackRegistry callbackRegistry;

  @Override
  public Patch patch(final String resource, final String resourceId, final String shadowStoreId, final Patch patch) {
    PersistenceCallback<?> persistenceCallback = callbackRegistry.findPersistenceCallback(resource);
    Object findOne = persistenceCallback.findOne(resourceId);
    return applyAndDiff(patch, findOne, persistenceCallback);
  }

  @SuppressWarnings("unchecked")
  private <T> Patch applyAndDiff(Patch patch, Object target, PersistenceCallback<T> persistenceCallback) {
    DiffSync<T> sync = new DiffSync<>(shadowStore, persistenceCallback.getEntityType());
    T patched = sync.apply((T) target, patch);
    persistenceCallback.persistChange(patched);
    return sync.diff(patched);
  }

  private <T> Patch applyAndDiffAgainstList(Patch patch, List<T> target, PersistenceCallback<T> persistenceCallback) {
    DiffSync<T> sync = new DiffSync<>(shadowStore, persistenceCallback.getEntityType());

    List<T> patched = sync.apply(target, patch);

    List<T> itemsToSave = new ArrayList<>(patched);
    itemsToSave.removeAll(target);

    // Determine which items should be deleted.
    // Make a shallow copy of the target, remove items that are equivalent to items in the working copy.
    // Equivalent is not the same as equals. It means "this is the same resource, even if it has changed".
    // It usually means "are the id properties equals".
    List<T> itemsToDelete = new ArrayList<>(target);
    for (T candidate : target) {
      for (T item : patched) {
        if (equivalency.isEquivalent(candidate, item)) {
          itemsToDelete.remove(candidate);
          break;
        }
      }
    }
    persistenceCallback.persistChanges(itemsToSave, itemsToDelete);

    return sync.diff(patched);
  }

  @Override
  public Patch patch(final String resource, final Patch patch) {
    PersistenceCallback<?> persistenceCallback = callbackRegistry.findPersistenceCallback(resource);
    return applyAndDiffAgainstList(patch, (List) persistenceCallback.findAll(), persistenceCallback);
  }
}
