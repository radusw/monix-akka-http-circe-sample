# monix-akka-http-circe-example
An example/template of using Monix (https://monix.io/) for limiting the number of simultaneous requests sent (using akka-http client) to an endpoint


### To see it in action
$curl http://localhost:9000/tick


You should see:

```
2017-02-01T02:40:29.015+0100 [my-system-akka.actor.default-dispatcher-2] INFO  Main$ - Server online. Type `exit` to stop...
2017-02-01T02:40:31.885+0100 [my-system-akka.actor.default-dispatcher-15] INFO  Handler - Sending data (0%)...; Current one: RequestContext(0,1000)
2017-02-01T02:40:34.459+0100 [my-system-akka.actor.default-dispatcher-7] INFO  Handler - Sending data (5%)...; Current one: RequestContext(50,1000)
2017-02-01T02:40:36.483+0100 [my-system-akka.actor.default-dispatcher-11] INFO  Handler - Sending data (10%)...; Current one: RequestContext(100,1000)
2017-02-01T02:40:38.603+0100 [my-system-akka.actor.default-dispatcher-2] INFO  Handler - Sending data (15%)...; Current one: RequestContext(150,1000)
2017-02-01T02:40:40.762+0100 [my-system-akka.actor.default-dispatcher-2] INFO  Handler - Sending data (20%)...; Current one: RequestContext(200,1000)
2017-02-01T02:40:44.522+0100 [my-system-akka.actor.default-dispatcher-10] INFO  Handler - Sending data (25%)...; Current one: RequestContext(250,1000)
2017-02-01T02:40:46.739+0100 [my-system-akka.actor.default-dispatcher-10] INFO  Handler - Sending data (30%)...; Current one: RequestContext(300,1000)
2017-02-01T02:40:48.849+0100 [my-system-akka.actor.default-dispatcher-10] INFO  Handler - Sending data (35%)...; Current one: RequestContext(350,1000)
2017-02-01T02:40:51.859+0100 [my-system-akka.actor.default-dispatcher-15] INFO  Handler - Sending data (40%)...; Current one: RequestContext(400,1000)
2017-02-01T02:40:54.736+0100 [my-system-akka.actor.default-dispatcher-2] INFO  Handler - Sending data (45%)...; Current one: RequestContext(450,1000)
2017-02-01T02:40:56.688+0100 [my-system-akka.actor.default-dispatcher-15] INFO  Handler - Sending data (50%)...; Current one: RequestContext(500,1000)
2017-02-01T02:40:58.467+0100 [my-system-akka.actor.default-dispatcher-7] INFO  Handler - Sending data (55%)...; Current one: RequestContext(550,1000)
2017-02-01T02:41:01.070+0100 [my-system-akka.actor.default-dispatcher-4] INFO  Handler - Sending data (60%)...; Current one: RequestContext(600,1000)
2017-02-01T02:41:03.970+0100 [my-system-akka.actor.default-dispatcher-26] INFO  Handler - Sending data (65%)...; Current one: RequestContext(650,1000)
2017-02-01T02:41:05.933+0100 [my-system-akka.actor.default-dispatcher-11] INFO  Handler - Sending data (70%)...; Current one: RequestContext(700,1000)
2017-02-01T02:41:07.833+0100 [my-system-akka.actor.default-dispatcher-26] INFO  Handler - Sending data (75%)...; Current one: RequestContext(750,1000)
2017-02-01T02:41:10.091+0100 [my-system-akka.actor.default-dispatcher-11] INFO  Handler - Sending data (80%)...; Current one: RequestContext(800,1000)
2017-02-01T02:41:13.300+0100 [my-system-akka.actor.default-dispatcher-6] INFO  Handler - Sending data (85%)...; Current one: RequestContext(850,1000)
2017-02-01T02:41:15.317+0100 [my-system-akka.actor.default-dispatcher-26] INFO  Handler - Sending data (90%)...; Current one: RequestContext(900,1000)
2017-02-01T02:41:17.279+0100 [my-system-akka.actor.default-dispatcher-3] INFO  Handler - Sending data (95%)...; Current one: RequestContext(950,1000)
2017-02-01T02:41:19.007+0100 [my-system-akka.actor.default-dispatcher-25] INFO  Handler - Done sending data (100%)...; Last one: RequestContext(999,1000)
```
