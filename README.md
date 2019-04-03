# tqks-neo4j-client
Topic-centric extensible simple Neo4J client

A BaseNeoClient and a WebPageGraphClient which serves as an example for extending the base client for specific kinds of topics.

This client is designed to work within the TopicQuests Framework in which an extension of the class org.topicquests.support.RootEnvironment has been established for configuration and logging.

To compile:
* mvn clean install -DskipTests -Dgpg.skip
