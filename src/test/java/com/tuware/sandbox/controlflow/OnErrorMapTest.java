package com.tuware.sandbox.controlflow;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OnErrorMapTest {

    @Test
    void onErrorMap() {
        class GenericException extends RuntimeException {}

        var counter = new AtomicInteger();

        Flux<Integer> resultsInError = Flux.error(new IllegalArgumentException("oops!"));

        Flux<Integer> errorHandlingStream = resultsInError.onErrorMap(IllegalArgumentException.class, ex -> new GenericException())
                .doOnError(GenericException.class, ge -> counter.incrementAndGet());

        StepVerifier.create(errorHandlingStream).expectError().verify();

        assertEquals(1, counter.get());
    }

}
