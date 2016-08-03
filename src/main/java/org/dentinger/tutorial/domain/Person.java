package org.dentinger.tutorial.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.dentinger.tutorial.util.UUIDConverter;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.annotation.typeconversion.DateLong;

@NodeEntity
@JsonFilter("person")
public class Person {

  @GraphId
  private Long id;
  private Long personId;
  @Convert(UUIDConverter.class)
  private UUID uuid;
  private String name;
  @DateLong
  private Date dateOfBirth;
 // @JsonBackReference
  @Relationship(type = "PLAYS_ON", direction = Relationship.UNDIRECTED)
  private Set<Team> playson;
  //@JsonBackReference
  @Relationship(type = "FAN_OF", direction = Relationship.UNDIRECTED)
  private Set<Team> fanOf;

  private static Random rnd = new Random();

  ///

  public Person() {
    this.playson = new HashSet<>();
    this.fanOf = new HashSet<>();
  }

  public Person(Long personId, UUID uuid, String name) {
    this();
    this.personId = personId;
    this.uuid = uuid;
    this.name = name;
    // Get an Epoch value roughly between 1940 and 2010
    // -946771200000L = January 1, 1940
    // Add up to 70 years to it (using modulus on the next long)
    this.dateOfBirth = new Date(-946771200000L + (Math.abs(rnd.nextLong()) % (70L * 365 * 24 * 60 * 60 * 1000)));
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getPersonId() {
    return personId;
  }

  public void setPersonId(Long personId) {
    this.personId = personId;
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

  public Set<Team> getFanOf() {
    return fanOf;
  }

  public void setFanOf(Set<Team> fanOf) {
    this.fanOf = fanOf;
  }

  public void addTeam(Team team){
    playson.add(team);
  }

  public void fanOf(Team team){ fanOf.add(team); }

  @Override public String toString() {
    return "Person{" +
        "id=" + id +
        ", personId=" + personId +
        ", uuid='" + uuid + '\'' +
        ", name='" + name + '\'' +
        ", dateOfBirth=" + dateOfBirth +
        ", playson count=" + playson.size() +
        '}';
  }
}
