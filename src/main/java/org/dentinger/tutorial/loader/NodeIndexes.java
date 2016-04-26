package org.dentinger.tutorial.loader;

import org.dentinger.tutorial.autoconfig.Neo4jProperties;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.stereotype.Component;

@Component
public class NodeIndexes {

  private static Logger logger = LoggerFactory.getLogger(NodeIndexes.class);
  private Neo4jProperties neo4jProperties;
  private SessionFactory sessionFactory;

  private static String TEAM_INDEX = "CREATE INDEX on :Team(id)";
  private static String LEAGUE_INDEX = "CREATE INDEX on :League(id)";
  private static String REGION_INDEX = "CREATE INDEX on :Region(id)";

  @Autowired
  public NodeIndexes(
      Neo4jProperties neo4jProperties,
      SessionFactory sessionFactory) {
    this.neo4jProperties = neo4jProperties;
    this.sessionFactory = sessionFactory;
  }

  public void createIndexes() {
    Neo4jTemplate neo4jTemplate = getNeo4jTemplate();

    neo4jTemplate.execute(TEAM_INDEX);
    neo4jTemplate.execute(LEAGUE_INDEX);
    neo4jTemplate.execute(REGION_INDEX);

  }

  private Neo4jTemplate getNeo4jTemplate() {
    Session session = sessionFactory.openSession(neo4jProperties.getUrl(),
        neo4jProperties.getUsername(), neo4jProperties.getPassword());

    return new Neo4jTemplate(session);
  }
}
