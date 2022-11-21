package com.tuware.sandbox.debug;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;

class BlockHoundTest {
    private static class BlockingCallError extends Error {
        BlockingCallError(String msg) {
            super(msg);
        }
    }

    private Mono<Long> buildBlockingMono() {
        return Mono.just(1L).doOnNext(it -> block());
    }

    @SneakyThrows
    private void block() {
        Thread.sleep(1000);
    }

    private final static AtomicBoolean atomicBoolean = new AtomicBoolean();

    @BeforeEach
    void before() {

        atomicBoolean.set(true);

        var integrations = new ArrayList<BlockHoundIntegration>();
        var services = ServiceLoader.load(BlockHoundIntegration.class);
        services.forEach(integrations::add);

        integrations.add(builder -> builder.blockingMethodCallback(blockingMethod -> {
            if (atomicBoolean.get()) {
                throw new BlockingCallError(blockingMethod.toString());
            }
        }));

        BlockHound.install(integrations.toArray(new BlockHoundIntegration[0]));
    }

    @AfterEach
    public void after() {
        atomicBoolean.set(false);
    }

    @Test
    public void notOk() {
        StepVerifier
                .create(this.buildBlockingMono().subscribeOn(Schedulers.parallel()))
                .expectErrorMatches(e -> e instanceof BlockingCallError)
                .verify();
    }

    @Test
    public void ok() {
        StepVerifier
                .create(this.buildBlockingMono().subscribeOn(Schedulers.boundedElastic()))
                .expectNext(1L)
                .verifyComplete();
    }

}
