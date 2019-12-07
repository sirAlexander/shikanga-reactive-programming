# Shikanga Vertx Starter
This is a simple application to get you started with the Vert.x toolkit.
 
The code features
two processes running simultaneously. One process (`vertx.setPeriodic()`) 
writes a message to the
console every two seconds, while the other (`vertx.createHttpServer()`) 
serves requests on HTTP port 8080. 

## Building and running the code

The following commands build and run the code: 

```
mvn clean package
java -jar target/shikanga-vertx-starter-1.0-SNAPSHOT.jar
```
## Sample output 

Sample output looks like this: 

````
----------------------------------------------
---> My first Vertx Verticle now listening on localhost:8080
----------------------------------------------
Dec 07, 2019 8:17:19 PM io.vertx.core.impl.launcher.commands.VertxIsolatedDeployer
INFO: Succeeded in deploying verticle
Server run time: 2 seconds.
Server run time: 4 seconds.
Server run time: 6 seconds.
Server run time: 8 seconds.
.
.
Server run time: 48 seconds.
Request #1 from 0:0:0:0:0:0:0:1
Request #2 from 0:0:0:0:0:0:0:1
Server run time: 50 seconds.
Server run time: 52 seconds.
Server run time: 54 seconds.
Server run time: 56 seconds.
^C---------------------------------------------
---> My first Vertx Verticle now signing off! Have a great day.
---------------------------------------------
```

## Some notes on the output: 

First of all, notice that we `@Override` the `start()` and `stop()` methods. 
Overriding the `start()` method is typical because you probably want to set 
up some things when your verticle is loaded. Overriding `stop()` is less 
common, but notice that typing Ctrl+C at the command line invoked the `stop()` 
method before the system killed the code. 
 
Second, the print statements in the `start()` method were executed before the 
verticle was up and running. The Vert.x runtime doesn’t print the 
“Succeeded in deploying verticle” message until the `start()` method is 
finished. The output says the code is listening on port 8080, but that’s not
technically true until a fraction of a second later when the verticle is fully loaded. 
 
You can see the two asynchronous processes the verticle uses. 
One (`vertx.setPeriodic()`) is invoked every 
two seconds, the other (`vertx.createHttpServer()`) is invoked whenever an 
HTTP request comes in on localhost:8080. 
As long as the verticle is running, these two processes operate 
independently of each other.