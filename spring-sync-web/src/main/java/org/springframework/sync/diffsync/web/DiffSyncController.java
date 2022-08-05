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
package org.springframework.sync.diffsync.web;

import de.sync.core.DiffSyncService;
import de.sync.core.Patch;
import de.sync.core.PatchException;
import de.sync.core.diffsync.DiffSync;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * Controller to handle PATCH requests an apply them to resources using {@link DiffSync}.
 *
 * @author Craig Walls
 */
@RestController
@RequiredArgsConstructor
public class DiffSyncController {

  private final DiffSyncService diffSyncService;

  @Transactional
  @SuppressWarnings({ "unchecked", "rawtypes" })
  @PatchMapping(value = "${spring.diffsync.path:}/{resource}")
  public Patch patch(@PathVariable("resource") String resource, @RequestBody Patch patch, HttpSession session)
      throws PatchException {
    return diffSyncService.patch(resource, patch, session.getId());
  }

  @Transactional
  @PatchMapping(value = "${spring.diffsync.path:}/{resource}/{id}")
  public Patch patch(@PathVariable("resource") String resource, @PathVariable("id") String id, @RequestBody Patch patch,
      HttpSession session)
      throws PatchException {
    return diffSyncService.patch(resource, id, session.getId(), patch);
  }

  @ExceptionHandler(PatchException.class)
  @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Unable to apply patch")
  public void handlePatchException(PatchException e) {
  }

}
