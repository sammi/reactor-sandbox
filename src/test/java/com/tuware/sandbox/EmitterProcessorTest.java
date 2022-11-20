package com.tuware.sandbox;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.FluxSink;
import reactor.test.StepVerifier;

class EmitterProcessorTest {

    @Test
    void emitterProcessor() {

        EmitterProcessor<String> processor = EmitterProcessor.create();

        FluxSink<String> sink = processor.sink();

        sink.next("1");
        sink.next("2");
        sink.next("3");
        sink.complete();

        StepVerifier.create(processor)
                .expectNext("1")
                .expectNext("2")
                .expectNext("3")
                .verifyComplete();
    }
}
