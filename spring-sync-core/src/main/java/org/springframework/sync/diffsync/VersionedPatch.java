package org.springframework.sync.diffsync;

import java.util.List;

import lombok.Getter;
import org.springframework.sync.Patch;
import org.springframework.sync.operations.PatchOperation;

@Getter
public class VersionedPatch extends Patch {

  private final long serverVersion;

  private final long clientVersion;

  public VersionedPatch(List<PatchOperation> operations, long serverVersion, long clientVersion) {
    super(operations);
    this.serverVersion = serverVersion;
    this.clientVersion = clientVersion;
  }

}
