/*
 * Copyright 2016 brunomcustodio
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.bmcstdio.rxjava.hooks;

import org.slf4j.MDC;
import rx.functions.Action0;

import java.util.Map;

/**
 * Decorates an {@link Action0} so that it executes with the current {@link MDC} as its context.
 */
public final class MdcPropagatingAction implements Action0 {
  private final Action0 action0;
  private final Map<String, String> context;

  /**
   * Decorates an {@link Action0} so that it executes with the current {@link MDC} as its context.
   * @param action0 the {@link Action0} to decorate.
   */
  public MdcPropagatingAction(final Action0 action0) {
    this.action0 = action0;
    this.context = MDC.getCopyOfContextMap();
  }

  @Override
  public void call() {
    final Map<String, String> originalMdc = MDC.getCopyOfContextMap();

    if (context != null) {
      MDC.setContextMap(context);
    }
    try {
      this.action0.call();
    } finally {
      if (originalMdc != null) {
        MDC.setContextMap(originalMdc);
      }
    }
  }
}
