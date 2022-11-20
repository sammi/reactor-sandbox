package com.tuware.sandbox;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

class AsyncApiIntegrationTest {
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);

    private void sleep(long s) {
        try {
            Thread.sleep(s);
        }
        catch (Exception e) {
            //
        }
    }

    @Test
    void async() {

        Flux<Integer> integers = Flux.create(emitter -> executorService.submit(() -> {
            var integer = new AtomicInteger();
            while (integer.get() < 5) {
                double random = Math.random();
                emitter.next(integer.incrementAndGet());
                sleep((long) (random * 1_000));
            }
            emitter.complete();
        }));

        StepVerifier.create(
            integers.doFinally(signalType -> executorService.shutdown())
        ).expectNextCount(5).verifyComplete();

    }
}
