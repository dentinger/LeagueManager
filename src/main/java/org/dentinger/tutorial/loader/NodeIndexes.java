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

  private static final String TEAM_INDEX = "CREATE INDEX on :Team(id)";
  private static final String LEAGUE_INDEX = "CREATE INDEX on :League(id)";
  private static final String REGION_INDEX = "CREATE INDEX on :Region(id)";

  @Autowired
  public NodeIndexes(Neo4jOperations neo4jTemplate) {
    this.neo4jTemplate = neo4jTemplate;
  }

  public void createIndexes() {
    neo4jTemplate.execute(TEAM_INDEX);
    neo4jTemplate.execute(LEAGUE_INDEX);
    neo4jTemplate.execute(REGION_INDEX);
  }
}
