package org.dentinger.tutorial.autoconfig;

import java.util.List;
import org.neo4j.ogm.session.Neo4jSession;
import org.neo4j.ogm.session.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.config.Neo4jConfiguration;
import org.springframework.data.neo4j.server.Neo4jServer;
import org.springframework.data.neo4j.server.RemoteServer;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnClass({Neo4jSession.class})
@EnableConfigurationProperties(Neo4jProperties.class)
public class Neo4jAutoConfiguration extends Neo4jConfiguration {

  @Autowired
  private Neo4jProperties properties;

  @Override
  public Neo4jServer neo4jServer() {
    if (StringUtils.isEmpty(properties.getUsername()) ||
        StringUtils.isEmpty(properties.getPassword())) {
      return new RemoteServer(properties.getUrl());
    } else {
      return new RemoteServer(properties.getUrl(),
          properties.getUsername(), properties.getPassword());
    }
  }

  @Override
  public SessionFactory getSessionFactory() {
    List<String> packages = properties.getPackages();
    return new SessionFactory(packages.toArray(new String[packages.size()]));
  }
}
