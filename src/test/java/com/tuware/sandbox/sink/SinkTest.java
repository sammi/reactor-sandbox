package com.tuware.sandbox.sink;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

@Log4j2
class SinkTest {

    @Test
    void sinkOne() {
        Sinks.One<Object> sinkOne = Sinks.one();
        Mono<Object> objectMono = sinkOne.asMono();

        sinkOne.tryEmitValue("Hello");
        sinkOne.tryEmitValue("Hi");

        StepVerifier.create(objectMono).expectNext("Hello").expectComplete().verify();
    }

    @Test
    void sinkManyUnicast() {
        Sinks.Many<Object> sinkMany = Sinks.many().unicast().onBackpressureBuffer();

        Flux<Object> flux = sinkMany.asFlux();

        sinkMany.tryEmitNext("hi");
        sinkMany.tryEmitNext("how are you");
        sinkMany.tryEmitNext("?");
        sinkMany.tryEmitComplete();

        StepVerifier.create(flux).expectNext("hi","how are you","?").verifyComplete();

        StepVerifier.create(flux).expectErrorMatches(e -> e.getMessage().equals("UnicastProcessor allows only a single Subscriber")).verify();
    }
    @Test
    void sinkManyReplayAll() {
        Sinks.Many<Object> sinkMany = Sinks.many().replay().all();

        Flux<Object> flux = sinkMany.asFlux();

        sinkMany.tryEmitNext("hi");
        sinkMany.tryEmitNext("how are you");
        sinkMany.tryEmitNext("?");
        sinkMany.tryEmitComplete();

        StepVerifier.create(flux).expectNext("hi","how are you","?").verifyComplete();

        StepVerifier.create(flux).expectNext("hi","how are you","?").verifyComplete();
    }

    @Test
    void sinkManyMulticast() {
        Sinks.Many<Object> sinkMany = Sinks.many().multicast().onBackpressureBuffer();

        Flux<Object> flux = sinkMany.asFlux();

        sinkMany.tryEmitNext("hi");
        sinkMany.tryEmitNext("how are you");
        sinkMany.tryEmitNext("?");
        sinkMany.tryEmitComplete();

        StepVerifier.create(flux).expectNext("hi","how are you","?").verifyComplete();

        StepVerifier.create(flux).verifyComplete();
    }
}
