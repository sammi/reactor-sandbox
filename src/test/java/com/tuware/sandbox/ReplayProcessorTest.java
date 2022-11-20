package com.tuware.sandbox;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.FluxSink;
import reactor.core.publisher.ReplayProcessor;
import reactor.test.StepVerifier;

class ReplayProcessorTest {

    @Test
    void replayProcessor() {

        int historySize = 2;
        boolean unbounded = false;

        ReplayProcessor<String> processor = ReplayProcessor.create(historySize, unbounded);

        FluxSink<String> sink = processor.sink();
        sink.next("1");
        sink.next("2");
        sink.next("3");
        sink.complete();

        for (int i = 0; i < 5; i++)
            StepVerifier
                    .create(processor)
                    .expectNext("2")
                    .expectNext("3")
                    .verifyComplete();
    }
}
