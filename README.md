# Shikanga Reactive Programming
Reactive programming is programming with asynchronous data streams. 

[The reactive Manifesto](https://www.reactivemanifesto.org/)

1. ### Shikanga Vertx Starter
This is a simple application to get you started with the Vert.x toolkit.
 
The code features
two processes running simultaneously. One process (`vertx.setPeriodic()`) 
writes a message to the
console every two seconds, while the other (`vertx.createHttpServer()`) 
serves requests on HTTP port 8080.
[Shikanga Vertx Starter](https://github.com/sirAlexander/shikanga-reactive-programming/tree/master/shikanga-vertx-starter)