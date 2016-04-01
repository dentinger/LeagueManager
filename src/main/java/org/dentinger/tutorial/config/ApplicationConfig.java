package org.dentinger.tutorial.config;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.data.neo4j.server.Neo4jServer;

@Configuration
@EnableNeo4jRepositories
class ApplicationConfig extends Neo4jConfiguration {

  public ApplicationConfig() {

    //setBasePackage("hello");
  }

  @Override public Neo4jServer neo4jServer() {
    return null;
  }

  @Override public SessionFactory getSessionFactory() {
    return null;
  }

  @Bean GraphDatabaseService graphDatabaseService() {
    return new GraphDatabaseFactory().newEmbeddedDatabase("accessingdataneo4j.db");
  }
}