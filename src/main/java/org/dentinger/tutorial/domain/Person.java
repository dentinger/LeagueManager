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
  private Long id;

  private UUID uuid;

  private String name;

  private Date dateOfBirth;

  @Relationship(type = "PLAYSON")
  Set<Team> playson;

  public Person(Long id, UUID uuid, String name){
    this.id = id;
    this.uuid = uuid;
    this.name = name;
    this.playson = new HashSet<>();
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public UUID getUuid() {
    return uuid;
  }

  public void setUuid(UUID uuid) {
    this.uuid = uuid;
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

  public void addTeam(Team team){
    playson.add(team);
  }

  @Override public String toString() {
    return "Person{" +
        "id=" + id +
        ", uuid='" + uuid + '\'' +
        ", name='" + name + '\'' +
        ", dateOfBirth=" + dateOfBirth +
        ", playson count=" + playson.size() +
        '}';
  }
}
