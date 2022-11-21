package com.tuware.sandbox.controlflow;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
class ControlFlowRetryTest {

    @Test
    public void retry() {

        var errored = new AtomicBoolean();

        Flux<String> producer = Flux.create(sink -> {
            if (!errored.get()) {
                errored.set(true);
                sink.error(new RuntimeException("Nope!"));
                log.info("returning a " + RuntimeException.class.getName() + "!");
            }
            else {
                log.info("we've already errored so here's the value");
                sink.next("hello");
            }
            sink.complete();
        });

        Flux<String> retryOnError = producer.retry();

        StepVerifier.create(retryOnError).expectNext("hello").verifyComplete();
    }
}
