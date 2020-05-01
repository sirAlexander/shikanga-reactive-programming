package uk.co.shikanga.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.converters.multi.MultiReactorConverters;
import reactor.core.publisher.Flux;
import uk.co.shikanga.data.Fruit;
import uk.co.shikanga.data.FruitRepository;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class FruitServiceImpl implements FruitService {

    private final FruitRepository fruitRepository;

    @Inject
    public FruitServiceImpl(FruitRepository fruitRepository) {
        this.fruitRepository = fruitRepository;
    }

    @Override
    public Multi<Fruit> findAll() {
        return fruitRepository.findAll();
    }

    @Override
    public Flux<Fruit> findAllFlux() {
        return fruitRepository.findAll()
                .convert()
                .with(MultiReactorConverters.toFlux());
    }

    @Override
    public Uni<Fruit> findById(Long id) {
        return fruitRepository.findById(id);
    }

    @Override
    public Uni<Long> save(Fruit fruit) {
        return fruitRepository.save(fruit);
    }

    @Override
    public Uni<Boolean> update(Fruit fruit) {
        return fruitRepository.update(fruit);
    }

    @Override
    public Uni<Boolean> delete(Long id) {
        return fruitRepository.delete(id);
    }

    @Override
    public void initDb() {
        fruitRepository.initDb();
    }
}
