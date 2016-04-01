package org.dentinger.tutorial.domain;

import java.util.Date;
import java.util.Set;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Person {
  @GraphId
  private Long id;

  private String name;

  private Date dateOfBirth;

  @Relationship(type = "PLAYSON")
Set<Team> playson;

  @Relationship(type = "COACHES")
  Set<Team> coaches;

  @Relationship(type = "FANOF")
  Set<Team> follows;

}
