package com.tuware.sandbox.stream;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log4j2
class HotStreamTest {
    private Stream<String> getMovie(){
        return Stream.of(
                "scene 1",
                "scene 2",
                "scene 3",
                "scene 4",
                "scene 5"
        );
    }

    private Consumer<SignalType> signalTypeConsumer(CountDownLatch cdl) {
        return signal -> {
            if (signal.equals(SignalType.ON_COMPLETE)) {
                cdl.countDown();
            }
        };
    }

    @Test
    void hotString() throws InterruptedException {
        var first = new ArrayList<String>();
        var second = new ArrayList<String>();
        Flux<String> movieTheatre = Flux.fromStream(this::getMovie).delayElements(Duration.ofMillis(10)).share();
        movieTheatre.subscribe(first::add);
        Thread.sleep(30);
        movieTheatre.subscribe(second::add);
        assertTrue(first.size() > second.size());
    }

    @Test
    void cache() throws InterruptedException {
        var first = new ArrayList<String>();
        var second = new ArrayList<String>();
        Flux<String> movieTheatre = Flux.fromStream(this::getMovie).delayElements(Duration.ofMillis(10)).cache();
        movieTheatre.subscribe(first::add);
        Thread.sleep(300);
        movieTheatre.subscribe(second::add);
        assertEquals(first.size(), second.size());
    }

    @Test
    void hotInteger() throws Exception {
        int factor = 10;
        var countDownLatch = new CountDownLatch(2);

        Flux<Integer> live = Flux.range(0, 10).delayElements(Duration.ofMillis(factor)).share();

        var one = new ArrayList<Integer>();
        live.doFinally(signalTypeConsumer(countDownLatch)).subscribe(one::add);

        Thread.sleep(factor * 2);

        var two = new ArrayList<Integer>();
        live.doFinally(signalTypeConsumer(countDownLatch)).subscribe(two::add);

        countDownLatch.await(5, TimeUnit.SECONDS);

        assertTrue(one.size() > two.size());
    }

    @Test
    void publish() {
        List<Integer> one = new ArrayList<>();
        List<Integer> two = new ArrayList<>();
        List<Integer> three = new ArrayList<>();

        Flux<Integer> pileOn = Flux.just(1, 2, 3).publish().autoConnect(3)
                .subscribeOn(Schedulers.immediate());

        pileOn.subscribe(one::add);
        assertEquals(0, one.size());

        pileOn.subscribe(two::add);
        assertEquals(0, two.size());

        pileOn.subscribe(three::add);

        assertEquals(3, three.size());
        assertEquals(3, two.size());
        assertEquals(3, three.size());
    }

}
