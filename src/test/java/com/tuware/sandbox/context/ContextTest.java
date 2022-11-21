package com.tuware.sandbox.context;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Signal;
import reactor.core.publisher.SignalType;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Log4j2
class ContextTest {

    @Test
    void context() throws InterruptedException {

        var observedContextValues = new ConcurrentHashMap<String, AtomicInteger>();

        var max = 3;
        var key = "key1";
        var cdl = new CountDownLatch(max);

        ContextView contextView = Context.of(key, "value1");

        Flux<Integer> just = Flux.range(0, max)
                .delayElements(Duration.ofMillis(1))
                .doOnEach(
                        (Signal<Integer> integerSignal) -> {
                            ContextView currentContextView = integerSignal.getContextView();
                            if (integerSignal.getType().equals(SignalType.ON_NEXT)) {
                                String key1 = currentContextView.get(key);
                                assertNotNull(key1);
                                assertEquals(key1, "value1");
                                observedContextValues
                                        .computeIfAbsent("key1", k -> new AtomicInteger(0))
                                        .incrementAndGet();
                            }
                        })
                .contextWrite(contextView);

        just.subscribe(integer -> {
            log.info("integer: " + integer);
            cdl.countDown();
        });

        cdl.await();

        assertEquals(max, observedContextValues.get(key).get());

    }

}
