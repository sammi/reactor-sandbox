package com.tuware.sandbox.operator;

import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

class ConcatMapTest {

    @AllArgsConstructor
    private static class Pair {

        private int id;

        private long delay;

    }

    @Test
    void concatMap() {

        Flux<Integer> data = Flux
                .just(
                        new Pair(1, 300),
                        new Pair(2, 200),
                        new Pair(3, 100)
                )
                .concatMap(id -> Flux.just(id.id).delayElements(Duration.ofMillis(id.delay)));

        StepVerifier
                .create(data)
                .expectNext(1, 2, 3)
                .verifyComplete();
    }

}
