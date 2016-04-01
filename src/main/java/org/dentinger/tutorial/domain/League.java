package org.dentinger.tutorial.domain;

import java.util.Set;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class League {

  @GraphId
  private Long id;

  private String name;

  @Relationship(type = "MEMBER_OF", direction = Relationship.UNDIRECTED)
  private Set<Team> members;

  @Relationship(type = "USES")
  private Set<Gym> venues;
}
