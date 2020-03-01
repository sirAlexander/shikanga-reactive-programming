package com.learnreactivespring.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;



@RestController
public class FluxAndMonoController {

    @GetMapping("/flux")
    public Flux<Integer> returnFlux(){

        return Flux.just(1, 2, 3, 4)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping(value = "/flux-stream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Integer> returnFluxStream(){

        return Flux.just(1, 2, 3, 4, 5, 6, 7)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping(value = "/infinite-flux-stream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Long> returnInfiniteFluxStream(){

        return Flux.interval(Duration.ofSeconds(1))
                .log();
    }

    @GetMapping("/mono")
    public Mono<Integer> returnMono(){

        return Mono.just(1)
                .log();
    }


}
