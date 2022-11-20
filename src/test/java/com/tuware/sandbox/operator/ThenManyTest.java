package com.tuware.sandbox.operator;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ThenManyTest {

    @Test
    void thenMany() {
        var letters = new AtomicInteger();
        var numbers = new AtomicInteger();

        Flux<String> lettersPublisher = Flux.just("a", "b", "c").doOnNext(value -> letters.incrementAndGet());

        Flux<Integer> numbersPublisher = Flux.just(1, 2, 3).doOnNext(number -> numbers.incrementAndGet());

        Flux<Integer> thisBeforeThat = lettersPublisher.thenMany(numbersPublisher);

        StepVerifier.create(thisBeforeThat).expectNext(1, 2, 3).verifyComplete();

        assertEquals(letters.get(), 3);
        assertEquals(numbers.get(), 3);
    }

}
