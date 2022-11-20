package com.tuware.sandbox.stream;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HotStreamTest3 {

    private List<Integer> one = new ArrayList<>();

    private List<Integer> two = new ArrayList<>();

    private List<Integer> three = new ArrayList<>();

    private Consumer<Integer> subscribe(List<Integer> list) {
        return list::add;
    }

    @Test
    void publish() {
        Flux<Integer> pileOn = Flux.just(1, 2, 3).publish().autoConnect(3)
                .subscribeOn(Schedulers.immediate());

        pileOn.subscribe(subscribe(one));
        assertEquals(0, this.one.size());

        pileOn.subscribe(subscribe(two));
        assertEquals(0, this.two.size());

        pileOn.subscribe(subscribe(three));

        assertEquals(3, this.three.size());
        assertEquals(3, this.two.size());
        assertEquals(3, this.three.size());
    }

}
