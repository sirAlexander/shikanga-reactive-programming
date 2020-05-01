package uk.co.shikanga.data;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

public interface FruitRepository {

    Multi<Fruit> findAll();

    Uni<Fruit> findById(Long id);

    Uni<Long> save(Fruit fruit);

    Uni<Boolean> update(Fruit fruit);

    Uni<Boolean> delete(Long id);

    void initDb();

}
