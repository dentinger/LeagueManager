package org.dentinger.tutorial.loader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.neo4j.template.Neo4jOperations;
import org.springframework.data.neo4j.template.Neo4jTemplate;
import org.springframework.stereotype.Component;

@Component
public class NodeIndexes {

  private static Logger logger = LoggerFactory.getLogger(NodeIndexes.class);
  private Neo4jOperations neo4jTemplate;

  @Autowired
  public NodeIndexes(Neo4jOperations neo4jTemplate) {
    this.neo4jTemplate = neo4jTemplate;
  }

  @SuppressWarnings("deprecation") public void createIndexes() {
    neo4jTemplate.execute("CREATE INDEX on :Region(id)");
    neo4jTemplate.execute("CREATE INDEX on :League(id)");
    neo4jTemplate.execute("CREATE INDEX on :Team(id)");
    neo4jTemplate.execute("CREATE INDEX on :Person(id)");
    neo4jTemplate.execute("CREATE INDEX on :Person(uuid)");
    logger.info("Creation of indexes complete");
  }
}
