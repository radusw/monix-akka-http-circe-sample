# monix-akka-http-circe-example
An example/template of using Monix (https://monix.io/) for limiting the number of simultaneous requests sent (using akka-http client) to an endpoint


### To see it in action
```
$sbt run
or
$sbt "run conf/prod.conf"

$curl http://localhost:9000/tick
```

You should see:

```
2017-02-01T02:40:29.015+0100 [my-system-akka.actor.default-dispatcher-2] INFO  Main$ - Server online. Type `exit` to stop...
2017-02-01T02:40:31.885+0100 [my-system-akka.actor.default-dispatcher-15] INFO  Handler - Sending data (0%)...; Current one: RequestContext(0,1000)
2017-02-01T02:40:34.459+0100 [my-system-akka.actor.default-dispatcher-7] INFO  Handler - Sending data (5%)...; Current one: RequestContext(50,1000)
2017-02-01T02:40:36.483+0100 [my-system-akka.actor.default-dispatcher-11] INFO  Handler - Sending data (10%)...; Current one: RequestContext(100,1000)
2017-02-01T02:40:38.603+0100 [my-system-akka.actor.default-dispatcher-2] INFO  Handler - Sending data (15%)...; Current one: RequestContext(150,1000)
...
2017-02-01T02:41:17.279+0100 [my-system-akka.actor.default-dispatcher-3] INFO  Handler - Sending data (95%)...; Current one: RequestContext(950,1000)
2017-02-01T02:41:19.007+0100 [my-system-akka.actor.default-dispatcher-25] INFO  Handler - Done sending data (100%)...; Last one: RequestContext(999,1000)
```

### Dockerized
```
$sbt docker:publishLocal
$docker run -d -p 9000:9000 --restart unless-stopped --name monix-akka-http radusw/monix-akka-http-client:1.0

$docker logs monix-akka-http --follow

$curl http://localhost:9000/tick
```
