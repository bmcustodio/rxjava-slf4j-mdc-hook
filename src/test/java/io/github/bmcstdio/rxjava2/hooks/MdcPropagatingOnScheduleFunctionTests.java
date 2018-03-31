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

import com.google.common.collect.ImmutableMap;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.MDC;

import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.*;
import static org.powermock.api.mockito.PowerMockito.when;

@PrepareForTest(MDC.class)
@RunWith(PowerMockRunner.class)
public final class MdcPropagatingOnScheduleFunctionTests {
    private static final String ALT_VAL_1 = "ALT_VAL_1";
    private static final String ALT_VAL_2 = "ALT_VAL_2";
    private static final String KEY_1 = "KEY_1";
    private static final String VAL_1 = "VAL_1";
    private static final String KEY_2 = "KEY_2";
    private static final String VAL_2 = "VAL_2";
    private static final Map<String, String> ALT_MAP;

    static {
        ALT_MAP = ImmutableMap.of(KEY_1, ALT_VAL_1, KEY_2, ALT_VAL_2);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        RxJavaPlugins.setScheduleHandler(null);
    }

    @BeforeClass
    public static void beforeClass() throws Exception {
        RxJavaPlugins.setScheduleHandler(new MdcPropagatingOnScheduleFunction());
    }

    @Before
    public void setUp() throws Exception {
        spy(MDC.class);
    }

    @Test
    public void doesNotCallSetContextMapIfMdcIsEmpty() throws Exception {
        MDC.clear();

        final TestObserver<Object> observer = new TestObserver<>();
        Observable.create(subscriber1 -> {
            subscriber1.onNext(Optional.ofNullable(MDC.get(KEY_1)));
            subscriber1.onNext(Optional.ofNullable(MDC.get(KEY_2)));
            subscriber1.onComplete();
        }).subscribeOn(Schedulers.computation()).subscribe(observer);

        observer.awaitTerminalEvent();
        observer.assertValues(Optional.empty(), Optional.empty());

        verifyStatic(never());
        MDC.setContextMap(anyStringMap());
    }

    @Test
    public void doesPropagateMdc() throws Exception {
        MDC.put(KEY_1, VAL_1);
        MDC.put(KEY_2, VAL_2);

        final TestObserver<Object> observer = new TestObserver<>();
        Observable.create(subscriber1 -> {
            subscriber1.onNext(MDC.get(KEY_1));
            subscriber1.onNext(MDC.get(KEY_2));
            subscriber1.onComplete();
        }).subscribeOn(Schedulers.computation()).subscribe(observer);

        observer.awaitTerminalEvent();
        observer.assertValues(VAL_1, VAL_2);

        verifyStatic(times(1));
        MDC.setContextMap(anyStringMap());
    }

    @Test
    public void doesRestoreMdc() throws Exception {
        when(MDC.getCopyOfContextMap()).then(new DoesRestoreMdcMdcGetCopyOfContextMapAnswer());

        MDC.put(KEY_1, VAL_1);
        MDC.put(KEY_2, VAL_2);

        final TestObserver<Object> observer = new TestObserver<>();
        Observable.create(subscriber1 -> {
            subscriber1.onNext(MDC.get(KEY_1));
            subscriber1.onNext(MDC.get(KEY_2));
            subscriber1.onComplete();
        }).subscribeOn(Schedulers.computation()).subscribe(observer);

        observer.awaitTerminalEvent();
        observer.assertValues(VAL_1, VAL_2);

        final ArgumentCaptor<Map<String, String>> captor = ArgumentCaptor.forClass(ALT_MAP.getClass());

        verifyStatic(after(500).times(2));
        MDC.setContextMap(captor.capture());

        assertEquals(VAL_1, captor.getAllValues().get(0).get(KEY_1));
        assertEquals(VAL_2, captor.getAllValues().get(0).get(KEY_2));
        assertEquals(ALT_VAL_1, captor.getAllValues().get(1).get(KEY_1));
        assertEquals(ALT_VAL_2, captor.getAllValues().get(1).get(KEY_2));
    }

    private static final class DoesRestoreMdcMdcGetCopyOfContextMapAnswer implements Answer {
        @Override
        public Object answer(final InvocationOnMock invocation) throws Throwable {
            if (Thread.currentThread().getName().startsWith("RxComputationThreadPool")) {
                return ALT_MAP;
            } else {
                return invocation.callRealMethod();
            }
        }
    }

    private static Map<String, String> anyStringMap() {
        return any();
    }
}