package com.learnreactivespring.fluxandmonoplayground;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam", "anna", "jack", "jenny");

    @Test
    public void transformUsingMap() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .map(String::toUpperCase) // ADAM, ANNA, JACK, JENNY
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("ADAM", "ANNA", "JACK", "JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(String::length)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length_repeat() {

        Flux<Integer> namesFlux = Flux.fromIterable(names)
                .map(String::length)
                .repeat(1)
                .log();

        StepVerifier.create(namesFlux)
                .expectNext(4, 4, 4, 5, 4, 4, 4, 5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Filter() {

        Flux<String> namesFlux = Flux.fromIterable(names)
                .filter(s -> s.length() > 4)
                .map(String::toUpperCase) // JENNY
                .log();

        StepVerifier.create(namesFlux)
                .expectNext("JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) // A, B, C, D, E, F
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s)); // A -> List[A, newValue], B -> List[B, newValue]
                }) // normally used for db or external service call that returns a flux -> s -> Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_Using_Parallel_v1() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) //Flux<String>
                .window(2) // Flux<Flux<String>> -> (A, B), (C, D), (E, F)
                .flatMap(sFlux -> {
                    return sFlux.map(s -> {
                        return convertToList(s);
                    }).subscribeOn(parallel()); // Flux<String>
                }) // normally used for db or external service call that returns a flux -> s -> Flux<String>
                .flatMap(sList -> {
                    return Flux.fromIterable(sList);
                })
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }


    @Test // v1 but optimised
    public void transformUsingFlatMap_Using_Parallel_v1_2() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) //Flux<String>
                .window(2) // Flux<Flux<String>> -> (A, B), (C, D), (E, F)
                .flatMap(sFlux -> sFlux.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>>
                .flatMap(sList -> Flux.fromIterable(sList)) // Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_Using_Parallel_Maintain_Order() {

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C", "D", "E", "F")) //Flux<String>
                .window(2) // Flux<Flux<String>> -> (A, B), (C, D), (E, F)
                //.concatMap(sFlux -> sFlux.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>> - don't use concat as it's very inefficient
                .flatMapSequential(sFlux -> sFlux.map(this::convertToList).subscribeOn(parallel())) // Flux<List<String>> - `flatMapSequential` is more performant
                .flatMap(sList -> Flux.fromIterable(sList)) // Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }


    private List<String> convertToList(String s) {

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }

}
