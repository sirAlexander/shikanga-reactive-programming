package uk.co.shikanga.reactive;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Vertx;

public class MyFirstVerticle extends AbstractVerticle {

    private long startTime = System.currentTimeMillis();
    private long counter = 1;

    @Override
    public void start() {
        vertx.setPeriodic(2000, counter -> {
            long runTime = (System.currentTimeMillis() - startTime) / 1000;
            System.out.println("Server run time: " + runTime + " seconds.");
        });

        vertx.createHttpServer()
                .requestHandler(req -> {
                    System.out.println("Request #" + counter++ +
                            " from " + req.remoteAddress().host());
                    req.response().end("Hello from My first Vertx Verticle!");
                })
                .listen(8080);

        System.out.println("----------------------------------------------");
        System.out.println("---> My first Vertx Verticle now listening on localhost:8080");
        System.out.println("----------------------------------------------");
    }

    @Override
    public void stop() {
        System.out.println("---------------------------------------------");
        System.out.println("---> My first Vertx Verticle now signing off! Have a great day.");
        System.out.println("---------------------------------------------");
    }

    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MyFirstVerticle());
    }
}
