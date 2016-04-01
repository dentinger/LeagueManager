package org.dentinger.tutorial.domain;

import java.util.Set;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Team {
  @GraphId
  private Long id;

  private String name;

  @Relationship(type = "PLAYSON", direction = Relationship.UNDIRECTED)
  private Set<Person> roster;

  @Relationship(type = "MEMBER_OF")
  private Set<League> leagues;

}
