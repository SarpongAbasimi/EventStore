package com.sarps.core.config

import ciris.{ConfigValue, Effect}
import ciris.env
import cats.implicits._

final case class Index(name: String)       extends AnyVal
final case class Host(host: String)        extends AnyVal
final case class Port(port: Int)           extends AnyVal
final case class Scheme(scheme: String)    extends AnyVal
final case class Shards(value: Int)        extends AnyVal
final case class ShardReplicas(value: Int) extends AnyVal
final case class Cluster(name: String)     extends AnyVal

case class ElasticSearchConfig(
    index: Index,
    host: Host,
    port: Port,
    scheme: Scheme,
    shards: Shards,
    replicas: ShardReplicas,
    cluster: Cluster
)

object ElasticSearchConfig {
  val elasticSearchConfig: ConfigValue[Effect, ElasticSearchConfig] =
    (
      env("INDEX").default("thoughtsIndex"),
      env("HOST").default("localhost"),
      env("PORT").as[Int].default(9200),
      env("SCHEME").default("Http"),
      env("NUMBER_OF_SHARDS").as[Int].default(3),
      env("NUMBER_OF_REPLICAS").as[Int].default(2),
      env("CLUSTER_NAME").as[String].default("elasticSearchCluster")
    ).mapN((index, host, port, scheme, numberOfShards, replicas, cluster) =>
      ElasticSearchConfig(
        Index(index),
        Host(host),
        Port(port),
        Scheme(scheme),
        Shards(numberOfShards),
        ShardReplicas(replicas),
        Cluster(cluster)
      )
    )
}
