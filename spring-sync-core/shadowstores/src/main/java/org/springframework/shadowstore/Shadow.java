package org.springframework.shadowstore;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Shadow<T> {

  private T resource;
  // aka serverVersion in the context of a server app
  private int serverVersion;
  // aka clientVersion in the context of a server app
  private int clientVersion;
}
