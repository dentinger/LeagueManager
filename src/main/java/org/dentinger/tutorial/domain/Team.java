package org.dentinger.tutorial.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFilter;
import java.util.HashSet;
import java.util.Set;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
@JsonFilter("team")
public class Team {
  @GraphId
  private Long id;
  private Long teamId;
  private String name;
 // @JsonBackReference
  @Relationship(type = "MEMBERSHIP", direction = Relationship.UNDIRECTED)
  private Set<League> leagues;
  @Relationship(type = "PLAYS_ON", direction = Relationship.UNDIRECTED)
  private Set<Person> players;
  @Relationship(type = "FAN_OF", direction = Relationship.UNDIRECTED)
  private Set<Person> fans;

  public Team() {
    this.leagues = new HashSet<>();
    this.players = new HashSet<>();
    this.fans = new HashSet<>();
  }

  public Team(Long teamId, String teamName) {
    this();
    this.teamId = teamId;
    this.name = teamName;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getTeamId() {
    return teamId;
  }

  public void setTeamId(Long teamId) {
    this.teamId = teamId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<League> getLeagues() {
    return leagues;
  }

  public Set<Person> getPlayers() {
    return players;
  }

  public void setPlayers(Set<Person> players) {
    this.players = players;
  }

  public Set<Person> getFans() {
    return fans;
  }

  public void setFans(Set<Person> fans) {
    this.fans = fans;
  }

  public void setLeagues(Set<League> leagues) {
    this.leagues = leagues;
  }

  public void addLeague(League league) {
    leagues.add(league);
  }

  @Override public String toString() {
    return "Team{" +
        "id=" + id +
        ", teamId=" + teamId +
        ", name='" + name + '\'' +
        ", league count=" + leagues.size() +
        '}';
  }
}
