package uk.co.shikanga.data;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.mysqlclient.MySQLPool;
import io.vertx.mutiny.sqlclient.Row;
import io.vertx.mutiny.sqlclient.RowSet;
import io.vertx.mutiny.sqlclient.Tuple;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.StreamSupport;

@Singleton
public final class FruitRepositoryImpl implements FruitRepository {

    private final MySQLPool client;

    @Inject
    public FruitRepositoryImpl(MySQLPool client) {
        this.client = client;
    }

    @Override
    public Multi<Fruit> findAll() {
        return client.query("SELECT id, name FROM fruits ORDER BY name ASC")
                // Create a Multi from the set of rows:
                .onItem().produceMulti(set -> Multi.createFrom().items(() -> StreamSupport.stream(set.spliterator(), false)))
                // For each row create a fruit instance
                .onItem().apply(this::from);
    }

    @Override
    public Uni<Fruit> findById(Long id) {
        return client.preparedQuery("SELECT id, name FROM fruits WHERE id = $1", Tuple.of(id))
                .onItem().apply(RowSet::iterator)
                .onItem().apply(iterator -> iterator.hasNext() ? from(iterator.next()) : null);
    }

    @Override
    public Uni<Long> save(Fruit fruit) {
        return client.preparedQuery("INSERT INTO fruits (name) VALUES ($1) RETURNING (id)", Tuple.of(fruit.getName()))
                .onItem().apply(pgRowSet -> pgRowSet.iterator().next().getLong("id"));
    }

    @Override
    public Uni<Boolean> update(Fruit fruit) {
        return client.preparedQuery("UPDATE fruits SET name = $1 WHERE id = $2", Tuple.of(fruit.getName(), fruit.getId()))
                .onItem().apply(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    @Override
    public Uni<Boolean> delete(Long id) {
        return client.preparedQuery("DELETE FROM fruits WHERE id = $1", Tuple.of(id))
                .onItem().apply(pgRowSet -> pgRowSet.rowCount() == 1);
    }

    @Override
    public void initDb() {
        client.query("DROP TABLE IF EXISTS fruits")
                .flatMap(r -> client.query("CREATE TABLE fruits (id SERIAL PRIMARY KEY, name TEXT NOT NULL)"))
                .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Kiwi')"))
                .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Durian')"))
                .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Pomelo')"))
                .flatMap(r -> client.query("INSERT INTO fruits (name) VALUES ('Lychee')"))
                .await().indefinitely();
    }

    private Fruit from(Row row) {
        return new Fruit(row.getLong("id"), row.getString("name"));
    }
}
