package org.dentinger.tutorial.domain;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Person {
  @GraphId
  private UUID id;

  private String name;

  private Date dateOfBirth;

  @Relationship(type = "PLAYSON")
  Set<Team> playson;

  @Relationship(type = "COACHES")
  Set<Team> coaches;

  @Relationship(type = "FANOF")
  Set<Team> follows;

  public Person(UUID id, String name){
    this.id = id;
    this.name = name;
    this.playson = new HashSet<>();
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
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

  @Override public String toString() {
    return "Person{" +
        "id=" + id.toString() +
        ", name='" + name + '\'' +
        '}';
  }
}
