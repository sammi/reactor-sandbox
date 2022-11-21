package com.tuware.sandbox.debug;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertTrue;

class CheckpointTest {

    private static String stackTraceToString(Throwable throwable) {
        try (var sw = new StringWriter(); var pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            return sw.toString();
        }
        catch (Exception ioEx) {
            throw new RuntimeException(ioEx);
        }
    }

    @Test
    void checkpoint() {

        var stackTrace = new AtomicReference<String>();

        var checkpoint = Flux
                .error(new IllegalArgumentException("Oops!"))
                .checkpoint()
                .delayElements(Duration.ofMillis(1));

        StepVerifier
                .create(checkpoint)
                .expectErrorMatches(e -> {
                    stackTrace.set(stackTraceToString(e));
                    return e instanceof IllegalArgumentException;
                })
                .verify();

        assertTrue(stackTrace.get().contains("Error has been observed at the following site(s):"));
    }

}
