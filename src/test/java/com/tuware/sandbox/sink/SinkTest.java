package com.tuware.sandbox.sink;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

        List<Object> events = new ArrayList<>();

        flux.subscribe(events::add);

        sinkMany.tryEmitNext("hi");
        sinkMany.tryEmitNext("how are you");
        sinkMany.tryEmitNext("?");

        assertEquals(events, Arrays.asList("hi", "how are you", "?"));

        StepVerifier.create(flux).expectErrorMatches(e -> e instanceof IllegalStateException).verify();
    }

}
