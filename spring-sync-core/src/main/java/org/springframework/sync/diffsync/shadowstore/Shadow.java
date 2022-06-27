package org.springframework.sync.diffsync.shadowstore;

import lombok.Data;

@Data
public class Shadow<T> {

  private T resource;
  private int clientVersion; // aka clientVersion in the context of a server app
  private int serverVersion;  // aka serverVersion in the context of a server app

  public Shadow(T resource, int serverVersion, int clientVersion) {
    this.resource = resource;
    this.clientVersion = clientVersion;
    this.serverVersion = serverVersion;
  }
}
