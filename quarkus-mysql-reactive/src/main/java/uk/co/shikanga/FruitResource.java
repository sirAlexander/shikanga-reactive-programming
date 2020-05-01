package uk.co.shikanga;


import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import reactor.core.publisher.Flux;
import uk.co.shikanga.data.Fruit;
import uk.co.shikanga.service.FruitService;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.net.URI;
import java.time.Duration;

@Path("fruits")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class FruitResource {

    @Inject
    @ConfigProperty(name = "myapp.schema.create", defaultValue = "true")
    boolean schemaCreate;

    @Inject
    FruitService fruitService;

    @PostConstruct
    void config() {
        if (schemaCreate) {
            fruitService.initDb();
        }
    }


    @GET
    public Multi<Fruit> get() {
        return fruitService.findAll();
    }

    @GET
    @Path("{id}")
    public Uni<Response> getSingle(@PathParam("id") Long id) {
        return fruitService.findById(id)
                .onItem().apply(fruit -> fruit != null ? Response.ok(fruit) : Response.status(Status.NOT_FOUND))
                .onItem().apply(Response.ResponseBuilder::build);
    }

    @POST
    public Uni<Response> create(Fruit fruit) {
        return fruitService.save(fruit)
                .onItem().apply(id -> URI.create("/fruits/" + id))
                .onItem().apply(uri -> Response.created(uri).build());
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(@PathParam("id") Long id, Fruit fruit) {
        return fruitService.update(fruit)
                .onItem().apply(updated -> updated ? Status.OK : Status.NOT_FOUND)
                .onItem().apply(status -> Response.status(status).build());
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(@PathParam("id") Long id) {
        return fruitService.delete(id)
                .onItem().apply(deleted -> deleted ? Status.NO_CONTENT : Status.NOT_FOUND)
                .onItem().apply(status -> Response.status(status).build());
    }

    @GET
    @Path("/flux-stream")
    @Produces("application/stream+json")
    public Flux<Integer> returnFluxStream() {

        return Flux.just(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .delaySequence(Duration.ofSeconds(1))
                .log();
    }

    @GET
    @Path("/reactive")
    @Produces("application/stream+json")
    public Flux<Fruit> returnReactively() {

        return fruitService.findAllFlux()
                .log();
    }
}