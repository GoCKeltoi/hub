[![Build Status](https://ci-jenkins.corp.mobile.de/jenkins/buildStatus/icon?job=ebay-github/mobile-de/inventory-list-indexer-v2/master-inventory-list-indexer-v2)](https://ci-jenkins.corp.mobile.de/jenkins/job/ebay-github/job/mobile-de/job/inventory-list-indexer-v2/job/master-inventory-list-indexer-v2/)
[![team](https://img.shields.io/badge/team-Board%20Runners-ff69b4.svg)](https://ecgwiki.corp.ebay.com/display/Project/Board+Runners)

# Endpoints
| Method | Resource          | Meaning                                       |
|--------|-------------------|-----------------------------------------------|
| POST   | /internal/reindex | Trigger creation of a new Elasticsearch index |

* [Kibana](https://kibana4.corp.mobile.de/#/discover/inventory-list-indexer)

# Development

```bash
# start docker containers
(cd docker-dev && docker-compose up -d)

# start the app
./gradlew clean dev

# trigger reindexing process (optional, it is done automatically in dev and integra on startup)
curl -XPOST localhost:8080/internal/reindex

# watch the docker logs
(cd docker-dev && docker-compose logs -f)

# shut down containers
(cd docker-dev && docker-compose down)
```

* ElasticSearch UI: [localhost:9200/_plugin/head/](http://localhost:9200/_plugin/head/)
* Graphite UI: [localhost:9080](http://localhost:9080/)

Useful scripts in `docker-dev` folder:
* Publish insert event: [insert-event-publish.sh](docker-dev/insert-event-publish.sh)
* Publish delete event: [delete-event-publish.sh](docker-dev/insert-event-publish.sh)
* List events in kafka: [list-events.sh](docker-dev/list-events.sh)

# Integration tests
Integration tests can be run locally using docker:
```bash
cd integration-tests
./run.sh
```
The tests themselves are in `integration-tests/tests-image/data/test/` folder.


# Graphite metrics:

| Metric                          | Meaning                                      |
|---------------------------------|----------------------------------------------|
| `inbound.kafka.<groupId>.count` | Number of messages consumed by this group id |
| `inbound.fullindex.count`       | Number of new indexes                        |
| `outbound.es.error.count`       | Number of indexing errors                    |



http://stackoverflow.com/questions/16870502/how-to-connect-cassandra-using-java-class

String cqlStatement = "CREATE KEYSPACE myfirstcassandradb WITH " + 
  "replication = {'class':'SimpleStrategy','replication_factor':1}";

session.execute(cqlStatement);

// based on the above keyspace, we would change the cluster and session as follows:
Cluster cluster = Cluster.builder()
  .addContactPoints(serverIP)
  .build();
Session session = cluster.connect("myfirstcassandradb");

String cqlStatement = "CREATE TABLE users (" + 
                      " user_name varchar PRIMARY KEY," + 
                      " password varchar " + 
                      ");";

session.execute(cqlStatement);

