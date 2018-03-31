/*
 * Copyright 2018 brunomcustodio
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

package io.github.bmcstdio.rxjava2.hooks;

import org.slf4j.MDC;

import java.util.Map;

/**
 * Decorates an {@link Runnable} so that it executes with the current {@link MDC} as its context.
 */
public final class MdcPropagatingRunnable implements Runnable {
    private final Runnable runnable0;
    private final Map<String, String> context;

    /**
     * Decorates an {@link Runnable} so that it executes with the current {@link MDC} as its context.
     * @param runnable0 the {@link Runnable} to decorate.
     */
    public MdcPropagatingRunnable(final Runnable runnable0) {
        this.runnable0 = runnable0;
        this.context = MDC.getCopyOfContextMap();
    }

    @Override
    public void run() {
        final Map<String, String> originalMdc = MDC.getCopyOfContextMap();

        if (context != null) {
            MDC.setContextMap(context);
        }
        try {
            this.runnable0.run();
        } finally {
            if (originalMdc != null) {
                MDC.setContextMap(originalMdc);
            }
        }
    }
}