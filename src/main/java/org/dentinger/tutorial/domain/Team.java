package org.dentinger.tutorial.domain;

import java.util.HashSet;
import java.util.Set;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

@NodeEntity
public class Team {
  @GraphId
  private Long id;

  private String name;

  @Relationship(type = "MEMBERSHIP")
  private Set<League> leagues;

  public Team() {
    this.leagues = new HashSet<>();
  }

  public Team(Long teamId, String teamName) {
    this();
    this.id = teamId;
    this.name = teamName;
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

  public Set<League> getLeagues() {
    return leagues;
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
        ", name='" + name + '\'' +
        ", league count=" + leagues.size() +
        '}';
  }
}
