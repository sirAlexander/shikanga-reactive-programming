package uk.co.shikanga.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import reactor.core.publisher.Flux;
import uk.co.shikanga.data.Fruit;

public interface FruitService {

    Multi<Fruit> findAll();

    Flux<Fruit> findAllFlux();

    Uni<Fruit> findById(Long id);

    Uni<Long> save(Fruit fruit);

    Uni<Boolean> update(Fruit fruit);

    Uni<Boolean> delete(Long id);

    void initDb();
}
