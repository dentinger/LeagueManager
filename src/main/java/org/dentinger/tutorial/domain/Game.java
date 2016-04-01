package org.dentinger.tutorial.domain;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class Game {
  @GraphId
  private Long id;

}
