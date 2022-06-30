package de.jds;

import org.springframework.sync.diffsync.PersistenceCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DemoPersistenceCallback implements PersistenceCallback<String> {

  final Map<String, String> store = new HashMap<>();

  @Override
  public List<String> findAll() {
    return new ArrayList<>(store.values());
  }

  @Override
  public String findOne(final String s) {
    return store.get(s);
  }

  @Override
  public void persistChange(final String s) {
  }

  @Override
  public void persistChanges(final List<String> list, final List<String> list1) {

  }

  @Override
  public Class<String> getEntityType() {
    return String.class;
  }
}
