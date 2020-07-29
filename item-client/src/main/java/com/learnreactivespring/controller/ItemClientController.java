package com.learnreactivespring.controller;

import com.learnreactivespring.domain.Item;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class ItemClientController {

    WebClient webClient = WebClient.create("http://localhost:8080");

    @GetMapping("/client/retrieve")
    public Flux<Item> getAllItemsUsingRetrieve() {

        return webClient.get().uri("/v1/items")
                .retrieve()
                .bodyToFlux(Item.class)
                .log("Items in Client Project retrieve :");
    }

    @GetMapping("/client/exchange")
    public Flux<Item> getAllItemsUsingExchange() {

        return webClient.get().uri("/v1/items")
                .exchange()
                .flatMapMany(clientResponse -> clientResponse.bodyToFlux(Item.class))
                .log("Items in Client Project exchange :");
    }

    @GetMapping("/client/retrieve/singleItem/{id}")
    public Mono<Item> getOneItemUsingRetrieve(@PathVariable String id) {

        return webClient.get().uri("/v1/items/{id}", id)
                .retrieve()
                .bodyToMono(Item.class)
                .log("Items in Client Project retrieve single item :");
    }

    @GetMapping("/client/exchange/singleItem/{id}")
    public Mono<Item> getOneItemUsingExchange(@PathVariable String id) {

        return webClient.get().uri("/v1/items/{id}", id)
                .exchange()
                .flatMap(clientResponse -> clientResponse.bodyToMono(Item.class))
                .log("Items in Client Project exchange single item :");
    }


}
