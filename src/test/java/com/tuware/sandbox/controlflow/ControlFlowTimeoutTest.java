package com.tuware.sandbox.controlflow;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
class ControlFlowTimeoutTest {

    private Flux<Integer> given(Throwable t) {
        assertTrue(t instanceof TimeoutException);
        return Flux.just(0);
    }

    @Test
    void timeout() {
        Flux<Integer> ids = Flux.just(1, 2, 3).delayElements(Duration.ofSeconds(1))
                .timeout(Duration.ofMillis(500)).onErrorResume(this::given);

        StepVerifier.create(ids).expectNext(0).verifyComplete();
    }

}
