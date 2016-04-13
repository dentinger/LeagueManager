package org.dentinger.tutorial.domain;

import java.util.Date;
import java.util.HashSet;
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

  public Person(Long id, String name){
    this.id = id;
    this.name = name;
    this.playson = new HashSet<>();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Date getDateOfBirth() {
    return dateOfBirth;
  }

  public void setDateOfBirth(Date dateOfBirth) {
    this.dateOfBirth = dateOfBirth;
  }

  public Set<Team> getPlayson() {
    return playson;
  }

  public void setPlayson(Set<Team> playson) {
    this.playson = playson;
  }

  public Set<Team> getCoaches() {
    return coaches;
  }

  public void setCoaches(Set<Team> coaches) {
    this.coaches = coaches;
  }

  public Set<Team> getFollows() {
    return follows;
  }

  public void setFollows(Set<Team> follows) {
    this.follows = follows;
  }

  public void addTeam(Team team){
    playson.add(team);
  }
}
