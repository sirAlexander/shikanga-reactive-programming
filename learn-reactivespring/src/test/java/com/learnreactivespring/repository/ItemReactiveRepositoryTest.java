package com.learnreactivespring.repository;

import com.learnreactivespring.document.Item;
import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> items = Arrays.asList(
            new Item(null, "Samsung TV", 400.00),
            new Item(null, "LG TV", 420.00),
            new Item(null, "Apple Watch", 299.99),
            new Item(null, "Beats Headphones", 149.99),
            new Item("ABC", "Bose Headphones", 159.00)
    );

    @Before
    public void setUp() {

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(items))
                .flatMap(itemReactiveRepository::save)
                .doOnNext(item -> System.out.println(" Inserted Item is : " + item))
                .blockLast();
    }

    @Test
    public void getAllItems() {

        StepVerifier.create(itemReactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(items.size())
                .verifyComplete();
    }

    @Test
    public void getItemById() {

        StepVerifier.create(itemReactiveRepository.findById("ABC"))
                .expectSubscription()
                .expectNextMatches(item -> item.getDescription().equals("Bose Headphones"))
                .verifyComplete();
    }

    @Test
    public void getItemByDescription() {

        StepVerifier.create(itemReactiveRepository.findByDescription("Bose Headphones").log(" findByDescription : "))
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem() {

        Item item = new Item(null, "Google Home Mini", 30.00);
        Mono<Item> savedItem = itemReactiveRepository.save(item);

        StepVerifier.create(savedItem.log("saved item : "))
                .expectSubscription()
                .expectNextMatches( item1 ->
                        StringUtils.isNotBlank(item1.getId()) && item1.getDescription().equals("Google Home Mini")
                ).verifyComplete();

    }

    @Test
    public void updateItem() {

        double newPrice = 520.00;

        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV")
                .flatMap(item -> {
                    item.setPrice(newPrice);
                    return itemReactiveRepository.save(item);
                });

        StepVerifier.create(updatedItem)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice().equals(newPrice))
                .verifyComplete();

    }

    @Test
    public void deleteItemById() {

        Mono<Void> deletedItem = itemReactiveRepository.findById("ABC")
                .flatMap(item -> itemReactiveRepository.deleteById(item.getId()));

        StepVerifier.create(deletedItem.log(" Deleted Item By Id Log: "))
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log(" The new item list : "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void deleteItem() {

        Mono<Void> deletedItem = itemReactiveRepository.findByDescription("LG TV")
                .flatMap(item -> itemReactiveRepository.delete(item));

        StepVerifier.create(deletedItem.log(" Deleted Item Log: "))
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log(" The new item list : "))
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }


}
