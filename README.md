# What is this ?

`Note: Only the happy path of the app was considered in this service`

A quick project to explore the `elasticSearch rest high level 
client` api.

The service basically receives a post request with a shape like this

```
final case class Thoughts(
    name: Name,
    message: Message,
    regrets: Option[Regrets],
    date: Option[Date] = Some(Date(Instant.now()))
)
```

```

{
    "name" : "Nana Kumakumaa",
    "message" : "I made a lot of progress with Scala today",
    "regrets" : "I lived today to the fullest so I have no regrets"
 }
```

The event is then published to a `kafka-topic` which is then consumed by a
Kafka consumer and indexed into an elasticSearch index.

# How to start the application 
- `Option One ->` Open app in IntelliJ and click on the play button
- From the root of the application `run` the `./testServer.sh` script
- `Option two ->` Type in `sbt project eventStore`
- Then type in `run` to start the server

# To do
- Configure app so that it can be run using sbt without specifying the project
- When the app is run, docker compose is started automatically

# TechStack

- `cats-Effects`
- `Http4s`
- `Fs2-Kafka`
- `ElasticSearch`


