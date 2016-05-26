package org.dentinger.tutorial.config;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableNeo4jRepositories(basePackages = "org.dentinger.tutorial.repository")
@EnableTransactionManagement
public class NeoConfiguration extends Neo4jConfiguration {
  @Value("${neo4j.url}")
  private String neo4jUri;
  @Value("${neo4j.username}")
  private String neo4jUsername;
  @Value("${neo4j.password}")
  private String neo4jPassword;
  @Value("${neo4j.driver}")
  private String neo4jDriver;

  @Bean
  public org.neo4j.ogm.config.Configuration getConfiguration() {
    org.neo4j.ogm.config.Configuration config = new org.neo4j.ogm.config.Configuration();
    config.driverConfiguration()
        .setURI(neo4jUri)
        .setCredentials(neo4jUsername, neo4jPassword)
        .setDriverClassName(neo4jDriver);

    return config;
  }

  @Override
  @Bean
  public SessionFactory getSessionFactory() {
    return new SessionFactory(getConfiguration(), "com.example.neo.domain");
  }

  // needed for session in view in web-applications
  @Bean
  @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
  public Session getSession() throws Exception {
    return super.getSession();
  }

}
