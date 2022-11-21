package com.tuware.sandbox.controlflow;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

class ControlFlowZipTest {

    private String from(Integer i, String s) {
        return i + ":" + s;
    }

    @Test
    void zip() {
        Flux<Integer> first = Flux.just(1, 2, 3);
        Flux<String> second = Flux.just("a", "b", "c");

        Flux<String> zip = Flux.zip(first, second).map(tuple -> this.from(tuple.getT1(), tuple.getT2()));

        StepVerifier.create(zip).expectNext("1:a", "2:b", "3:c").verifyComplete();
    }

}
