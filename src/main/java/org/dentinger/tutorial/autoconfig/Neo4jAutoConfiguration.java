package org.dentinger.tutorial.autoconfig;

import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

// turn off since we have a duplicate config
//@Configuration
@EnableNeo4jRepositories(basePackages = "org.dentinger.tutorial.repository")
@EnableTransactionManagement
public class Neo4jAutoConfiguration extends Neo4jConfiguration {

  @Override
  public SessionFactory getSessionFactory() {
    return new SessionFactory("org.dentinger.tutorial.domain");
  }

  /*
  @Bean
  @Scope(value = "prototype", proxyMode = ScopedProxyMode.TARGET_CLASS)
  public Session getSession() throws Exception {
    return super.getSession();
  }
  */

  // needed for session in view in web-applications
  /*
  @Bean
  @Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
  public Session getSession() throws Exception {
    return super.getSession();
  }
  */
}
